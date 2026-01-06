package com.miko.util;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * SSH连接工具类（带认证调试）
 */
@Slf4j
public class SSHUtil implements AutoCloseable {
    // 核心配置
    private static final int DEFAULT_SSH_PORT = 22;
    private static final int DEFAULT_CONNECT_TIMEOUT = 30000;
    private static final int DEFAULT_COMMAND_TIMEOUT = 15000;
    public static final int DEFAULT_IDLE_TIMEOUT_SECONDS = 300;
    private static final int IDLE_CHECK_PERIOD_SECONDS = 10;
    private static final int DEFAULT_CONNECT_RETRY_TIMES = 3;
    private static final int CONNECT_RETRY_INTERVAL = 2000;

    // 全局线程池
    private static final ScheduledExecutorService IDLE_CHECK_EXECUTOR = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "ssh-idle-check-thread");
        thread.setDaemon(true);
        return thread;
    });

    // 连接参数
    private String host;
    private int port;
    private String username;
    private String password;
    private String privateKeyPath;

    // 配置参数
    private int idleTimeoutSeconds;
    private int connectTimeout;
    private int connectRetryTimes;

    // 状态变量
    private final AtomicLong lastOperateTime = new AtomicLong(System.currentTimeMillis());
    private volatile boolean idleCheckScheduled = false;
    private volatile boolean isConnected = false;

    // Shell通道核心对象
    private JSch jsch;
    private Session session;
    private ChannelShell channelShell;
    private OutputStream shellOutput;
    private InputStream shellInput;
    private BufferedReader shellReader;

    // 路径信息
    private String currentPath = "~";
    private String homeDir = "~";

    /**
     * 私有构造方法
     */
    private SSHUtil(String host, int port, String username, String password, String privateKeyPath,
                    int idleTimeoutSeconds, int connectTimeout, int connectRetryTimes) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.privateKeyPath = privateKeyPath;
        this.idleTimeoutSeconds = idleTimeoutSeconds;
        this.connectTimeout = connectTimeout;
        this.connectRetryTimes = connectRetryTimes;
        this.jsch = new JSch();

        // 新增：打印认证信息（调试用）
        log.debug("SSH认证信息 - 主机：{}:{}，用户名：{}，密码长度：{}",
                host, port, username, password == null ? 0 : password.length());
    }

    /**
     * 静态工厂方法：密码登录（默认端口）
     */
    public static SSHUtil createWithPassword(String host, String username, String password) {
        return new SSHUtil(host, DEFAULT_SSH_PORT, username, password, null,
                DEFAULT_IDLE_TIMEOUT_SECONDS, DEFAULT_CONNECT_TIMEOUT, DEFAULT_CONNECT_RETRY_TIMES);
    }

    /**
     * 静态工厂方法：密码登录（指定端口+自定义超时）
     */
    public static SSHUtil createWithPassword(String host, int port, String username, String password, int connectTimeout) {
        return new SSHUtil(host, port, username, password, null,
                DEFAULT_IDLE_TIMEOUT_SECONDS, connectTimeout, DEFAULT_CONNECT_RETRY_TIMES);
    }

    /**
     * 建立SSH连接
     */
    public boolean connect() throws JSchException, IOException, InterruptedException {
        int retryCount = 0;
        JSchException lastException = null;

        while (retryCount <= connectRetryTimes) {
            try {
                log.info("========================================");
                log.info("开始连接SSH服务器");
                log.info("========================================");
                log.info("主机：{}:{}，用户名：{}", host, port, username);
                log.info("重试次数：{}/{}，超时：{}ms", retryCount + 1, connectRetryTimes + 1, connectTimeout);

                // 1. 创建Session
                log.debug("步骤1：创建Session...");
                session = jsch.getSession(username, host, port);
                session.setPassword(password);
                log.debug("Session创建成功");

                // 2. Session配置（简化配置，避免兼容性问题）
                log.debug("步骤2：配置Session参数...");
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                session.setTimeout(connectTimeout);
                log.debug("Session配置完成");

                // 3. 连接Session
                log.info("步骤3：正在建立SSH连接（认证）...");
                long connectStart = System.currentTimeMillis();
                session.connect(connectTimeout);
                long connectTime = System.currentTimeMillis() - connectStart;
                log.info("✓ SSH Session连接成功！认证通过，耗时：{}ms", connectTime);

                // 4. 创建Shell通道
                log.info("步骤4：正在创建Shell通道...");
                createShellChannel();
                log.info("✓ Shell通道创建成功");

                // 5. 初始化路径（同步执行，避免时序问题）
                log.info("步骤5：正在初始化路径信息...");
                initPathInfo();
                log.info("✓ 路径初始化完成，当前路径：{}", currentPath);

                // 6. 标记连接成功
                isConnected = true;
                log.info("========================================");
                log.info("✓ SSH连接成功！{}:{} 已就绪", host, port);
                log.info("========================================");

                // 7. 启动空闲检测
                updateLastOperateTime();
                scheduleIdleCheckTask();
                return true;

            } catch (JSchException e) {
                lastException = e;
                retryCount++;

                log.error("========================================");
                log.error("✗ 第{}次连接失败", retryCount);
                log.error("========================================");
                log.error("错误类型：{}", e.getClass().getName());
                log.error("错误信息：{}", e.getMessage());

                Throwable cause = e.getCause();
                if (cause != null) {
                    log.error("根本原因：{}", cause.getMessage());
                }

                log.error("异常堆栈：");
                for (StackTraceElement element : e.getStackTrace()) {
                    log.error("  at {}", element);
                }

                cleanUp();

                if (retryCount <= connectRetryTimes) {
                    log.info("等待 {}ms 后进行第 {} 次重试...", CONNECT_RETRY_INTERVAL, retryCount + 1);
                    try {
                        Thread.sleep(CONNECT_RETRY_INTERVAL);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new InterruptedException("连接重试被中断");
                    }
                }
            }
        }

        log.error("========================================");
        log.error("✗ 所有重试均失败，无法连接到 {}:{}",
                host, port);
        log.error("========================================");
        throw new JSchException(String.format("连接SSH服务器%s:%d失败（重试%d次）",
                host, port, connectRetryTimes), lastException);
    }

    /**
     * 创建Shell通道（简化版）
     */
    private void createShellChannel() throws JSchException, IOException, InterruptedException {
        log.debug("  - 打开shell通道...");
        channelShell = (ChannelShell) session.openChannel("shell");

        // 不使用PTY，避免兼容性问题
        // channelShell.setPty(true);
        // channelShell.setPtyType("vt100");
        log.debug("  - Shell通道已打开（禁用PTY以提高兼容性）");

        // 获取流
        log.debug("  - 获取输入输出流...");
        shellOutput = channelShell.getOutputStream();
        shellInput = channelShell.getInputStream();
        shellReader = new BufferedReader(new InputStreamReader(shellInput, StandardCharsets.UTF_8));
        log.debug("  - 流已获取");

        // 连接通道 - 使用较短的超时
        log.debug("  - 正在连接Shell通道（超时：{}ms）...", 5000);
        long channelStart = System.currentTimeMillis();
        channelShell.connect(5000); // 5秒超时
        long channelTime = System.currentTimeMillis() - channelStart;
        log.debug("  - Shell通道连接成功，耗时：{}ms", channelTime);

        // 清空初始输出（同步执行）
        log.debug("  - 清空初始输出...");
        clearInitialOutput();
        log.debug("  - 初始输出已清空");
    }

    /**
     * 清空初始输出（同步）
     */
    private void clearInitialOutput() throws InterruptedException, IOException {
        log.debug("清空Shell初始输出...");
        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < 3000) {
            if (shellReader.ready()) {
                String line = shellReader.readLine();
                if (line == null) break;
                log.debug("初始输出：{}", line);
            } else {
                Thread.sleep(100);
            }
        }
        log.debug("初始输出清空完成");
    }

    /**
     * 初始化路径信息（同步）
     */
    private void initPathInfo() throws IOException, InterruptedException {
        try {
            // 检查连接状态
            if (!isConnected || session == null || !session.isConnected()) {
                throw new IOException("SSH会话未建立，无法初始化路径");
            }

            // 获取家目录
            String homeResult = executeCommandInternal("echo $HOME");
            this.homeDir = homeResult.trim().isEmpty() ? "~" : homeResult.trim();
            log.debug("家目录：{}", homeDir);

            // 获取初始路径
            String pwdResult = executeCommandInternal("pwd");
            updateCurrentPath(pwdResult);
            log.debug("路径初始化完成 - 当前路径：{}", currentPath);
        } catch (Exception e) {
            log.error("路径初始化失败：{}，连接状态：{}，Session状态：{}",
                    e.getMessage(),
                    isConnected,
                    session != null ? session.isConnected() : "null");
            this.homeDir = "~";
            this.currentPath = "~";
            throw e; // 抛出异常，让调用方知道初始化失败
        }
    }

    /**
     * 更新当前路径
     */
    private void updateCurrentPath(String pwdResult) {
        String realPath = pwdResult.trim().isEmpty() ? homeDir : pwdResult.trim();
        // 替换家目录为~
        if (realPath.startsWith(homeDir) && !realPath.equals(homeDir)) {
            this.currentPath = "~" + realPath.substring(homeDir.length());
        } else if (realPath.equals(homeDir)) {
            this.currentPath = "~";
        } else {
            this.currentPath = realPath;
        }
    }

    /**
     * 执行内部命令（极简版）
     */
    private String executeCommandInternal(String command) throws IOException, InterruptedException {
        if (!isConnected) {
            throw new IOException("SSH未连接");
        }

        StringBuilder result = new StringBuilder();

        // 写入命令
        shellOutput.write((command + "\n").getBytes(StandardCharsets.UTF_8));
        shellOutput.flush();
        Thread.sleep(200); // 等待命令执行

        // 读取输出（不过滤，保留所有内容）
        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < DEFAULT_COMMAND_TIMEOUT) {
            while (shellReader.ready()) {
                String line = shellReader.readLine();
                if (line == null) break;

                // 只过滤命令回显的第一行（命令本身）
                if (!line.trim().equals(command)) {
                    result.append(line).append("\n");
                }
            }

            // 简单判断：如果有输出且短时间内没有新输出，认为执行完成
            if (result.length() > 0 && !shellReader.ready()) {
                Thread.sleep(100);
                if (!shellReader.ready()) {
                    break;
                }
            } else {
                Thread.sleep(100);
            }
        }

        String output = result.toString().trim();
        log.debug("内部命令[{}]输出：{}", command, output);
        return output;
    }

    /**
     * 执行单个命令（对外暴露）
     */
    public String executeCommand(String command) throws IOException, InterruptedException {
        if (!isConnected) {
            throw new IOException("SSH连接未建立");
        }

        updateLastOperateTime();
        log.info("执行命令：{}", command);

        StringBuilder result = new StringBuilder();

        try {
            // 写入命令
            shellOutput.write((command + "\n").getBytes(StandardCharsets.UTF_8));
            shellOutput.flush();
            Thread.sleep(300); // 等待命令执行

            // 读取输出（核心修复：保留所有有效输出）
            long startTime = System.currentTimeMillis();
            boolean hasOutput = false;

            while ((System.currentTimeMillis() - startTime) < DEFAULT_COMMAND_TIMEOUT) {
                while (shellReader.ready()) {
                    hasOutput = true;
                    String line = shellReader.readLine();
                    if (line == null) break;

                    // 过滤条件：只过滤空行和纯命令回显
                    if (!line.isEmpty() && !line.trim().equals(command)) {
                        // 进一步过滤提示符行（适配Termux）
                        if (!(line.contains(username + "@") && (line.endsWith("$") || line.endsWith("#")))) {
                            result.append(line).append("\n");
                        }
                    }
                }

                // 退出条件：有输出且无新内容
                if (hasOutput && !shellReader.ready()) {
                    Thread.sleep(200);
                    if (!shellReader.ready()) {
                        break;
                    }
                } else {
                    Thread.sleep(100);
                }
            }

            // 更新路径（只对cd命令生效）
            if (command.trim().startsWith("cd ")) {
                try {
                    String pwdResult = executeCommandInternal("pwd");
                    updateCurrentPath(pwdResult);
                } catch (Exception e) {
                    log.warn("更新cd命令路径失败", e);
                }
            }

            String cmdResult = result.toString().trim();
            log.info("命令执行完成：{}，结果：{}", command, cmdResult.isEmpty() ? "(空)" : cmdResult);
            return cmdResult;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedException("命令执行被中断：" + command);
        }
    }

    /**
     * 执行多条命令（带提示符输出）
     */
    public String executeCommands(String... commands) throws IOException, InterruptedException {
        updateLastOperateTime();
        log.info("执行多条命令，共{}条", commands.length);

        StringBuilder allResults = new StringBuilder();
        for (String cmd : commands) {
            // 生成本地提示符
            String prompt = getLocalPrompt();
            // 拼接提示符+命令
            allResults.append(prompt).append(" ").append(cmd).append("\n");
            // 执行命令
            String result = executeCommand(cmd);
            // 拼接结果
            allResults.append(result).append("\n\n");
        }

        log.info("多条命令执行完成");
        return allResults.toString();
    }

    /**
     * 生成本地提示符
     */
    private String getLocalPrompt() {
        String symbol = "root".equals(this.username) ? "#" : "$";
        return String.format("%s@%s:%s%s", username, host, currentPath, symbol);
    }

    /**
     * 更新最后操作时间
     */
    private void updateLastOperateTime() {
        lastOperateTime.set(System.currentTimeMillis());
    }

    /**
     * 启动空闲检测
     */
    private void scheduleIdleCheckTask() {
        if (idleCheckScheduled) return;

        IDLE_CHECK_EXECUTOR.scheduleAtFixedRate(() -> {
            try {
                if (!isConnected) {
                    idleCheckScheduled = false;
                    return;
                }

                long idleTime = System.currentTimeMillis() - lastOperateTime.get();
                long idleSeconds = idleTime / 1000;

                if (idleSeconds >= idleTimeoutSeconds) {
                    log.warn("SSH连接空闲超时（{}秒），自动关闭", idleSeconds);
                    disconnect();
                } else {
                    log.debug("SSH连接空闲时间：{}秒", idleSeconds);
                }
            } catch (Exception e) {
                log.error("空闲检测异常", e);
            }
        }, IDLE_CHECK_PERIOD_SECONDS, IDLE_CHECK_PERIOD_SECONDS, TimeUnit.SECONDS);

        idleCheckScheduled = true;
    }

    /**
     * 清理资源
     */
    private void cleanUp() {
        isConnected = false;

        // 关闭流（加保护）
        try {
            if (shellReader != null) {
                shellReader.close();
            }
        } catch (IOException e) {
            log.debug("关闭shellReader失败", e);
        }

        try {
            if (shellInput != null) {
                shellInput.close();
            }
        } catch (IOException e) {
            log.debug("关闭shellInput失败", e);
        }

        try {
            if (shellOutput != null) {
                shellOutput.close();
            }
        } catch (IOException e) {
            log.debug("关闭shellOutput失败", e);
        }

        // 关闭通道
        if (channelShell != null && channelShell.isConnected()) {
            channelShell.disconnect();
            log.debug("Shell通道已关闭");
        }

        // 关闭Session
        if (session != null && session.isConnected()) {
            session.disconnect();
            log.debug("SSH Session已关闭");
        }

        idleCheckScheduled = false;
    }

    /**
     * 关闭连接
     */
    public void disconnect() {
        cleanUp();
        log.info("SSH连接已关闭：{}:{}", host, port);
    }

    @Override
    public void close() {
        disconnect();
    }

    /**
     * 获取连接状态
     */
    public boolean isConnected() {
        return isConnected;
    }
}