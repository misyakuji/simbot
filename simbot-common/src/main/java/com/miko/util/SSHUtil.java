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
 * SSH连接工具类（改用Shell通道，保持命令上下文）
 * 封装SSH连接、命令执行、资源释放等核心逻辑
 */
@Slf4j
public class SSHUtil implements AutoCloseable {
    // 默认空闲超时时间（秒）- 无操作超过此时间自动关闭连接
    public static final int DEFAULT_IDLE_TIMEOUT_SECONDS = 300;
    // 默认SSH端口
    private static final int DEFAULT_SSH_PORT = 22;
    // 连接超时时间（15秒）
    private static final int DEFAULT_CONNECT_TIMEOUT = 15000;
    // 命令执行超时时间（毫秒）
    private static final int DEFAULT_COMMAND_TIMEOUT = 5000;
    // 空闲检测线程池周期（秒）
    private static final int IDLE_CHECK_PERIOD_SECONDS = 10;
    // 连接重试次数
    private static final int DEFAULT_CONNECT_RETRY_TIMES = 2;
    // 连接重试间隔（毫秒）
    private static final int CONNECT_RETRY_INTERVAL = 1000;

    // 全局空闲检测线程池
    private static final ScheduledExecutorService IDLE_CHECK_EXECUTOR = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "ssh-idle-check-thread");
        thread.setDaemon(true);
        return thread;
    });

    // SSH连接核心参数
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String privateKeyPath;

    // 超时/重试配置
    private final int idleTimeoutSeconds;
    private final int connectTimeout;
    private final int connectRetryTimes;

    // 空闲超时变量
    private final AtomicLong lastOperateTime = new AtomicLong(System.currentTimeMillis());
    // Shell通道相关（核心：保持会话上下文）
    private final JSch jsch;
    private volatile boolean idleCheckScheduled = false;
    private Session session;
    private ChannelShell channelShell;
    private OutputStream shellOutput; // 向shell写入命令
    private InputStream shellInput;   // 从shell读取结果
    private BufferedReader shellReader;

    // 路径相关
    private String currentPath;
    private String homeDir;

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
        this.currentPath = "~";
        this.homeDir = "~";
    }

    /**
     * 静态工厂方法：密码登录
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
     * 测试方法（验证cd命令上下文保持）
     */
    public static void main(String[] args) {
        try {
            // 创建SSH连接（Termux环境）
            SSHUtil ssh = SSHUtil.createWithPassword("192.168.1.20", 8022, "u0_a369", "123456", 30000);
            ssh.connect();

            // 测试单条命令
            System.out.println("=== 测试单条pwd命令 ===");
            String pwdResult = ssh.executeCommand("pwd");
            System.out.println("单条命令执行结果：\n" + pwdResult);

            // 测试cd命令
            System.out.println("\n=== 测试cd test命令 ===");
            String cdResult = ssh.executeCommand("cd test");
            System.out.println("单条命令执行结果：\n" + cdResult);

            // 再次执行pwd验证路径
            System.out.println("\n=== 再次执行pwd验证路径 ===");
            String pwdResult2 = ssh.executeCommand("pwd");
            System.out.println("单条命令执行结果：\n" + pwdResult2);

            // 测试多条命令
            System.out.println("\n=== 测试多条命令 ===");
            String multiResult = ssh.executeCommands(
                    "mkdir test",
                    "cd test",
                    "pwd",
                    "cd ..",
                    "pwd"
            );
            System.out.println("多条命令执行结果：\n" + multiResult);

            // 验证空闲超时
            log.info("等待10秒，验证空闲超时自动关闭...");
            Thread.sleep(10000);

            // 超时后执行命令
            System.out.println("\n=== 超时后执行pwd ===");
            String pwdResult3 = ssh.executeCommand("pwd");
            System.out.println("执行结果：\n" + pwdResult3);

            ssh.disconnect();
        } catch (Exception e) {
            log.error("测试失败", e);
        }
    }

    /**
     * 建立SSH连接（创建Shell通道）
     */
    public void connect() throws JSchException, IOException {
        int retryCount = 0;
        JSchException lastException = null;

        while (retryCount <= connectRetryTimes) {
            try {
                // 创建Session
                session = jsch.getSession(username, host, port);
                log.info("开始连接SSH服务器：{}:{}，用户名：{}（第{}次尝试）",
                        host, port, username, retryCount + 1);

                // 配置认证
                if (privateKeyPath != null && !privateKeyPath.isEmpty()) {
                    jsch.addIdentity(privateKeyPath);
                    log.debug("使用私钥登录");
                } else {
                    session.setPassword(password);
                    log.debug("使用密码登录");
                }

                // Session配置
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");
                config.put("ConnectTimeout", String.valueOf(connectTimeout));
                config.put("compression.s2c", "none");
                config.put("compression.c2s", "none");
                session.setConfig(config);
                session.setTimeout(connectTimeout);
                session.connect();

                // 核心：创建Shell通道（保持会话上下文）
                channelShell = (ChannelShell) session.openChannel("shell");
                // 配置Shell通道
                channelShell.setPty(true); // 启用伪终端，模拟真实终端
                channelShell.setPtyType("vt100"); // 终端类型
                // 启用输入输出流
                shellOutput = channelShell.getOutputStream();
                shellInput = channelShell.getInputStream();
                shellReader = new BufferedReader(new InputStreamReader(shellInput, StandardCharsets.UTF_8));
                // 连接Shell通道
                channelShell.connect();

                log.info("SSH服务器连接成功，Shell通道已创建：{}:{}", host, port);

                // 初始化家目录和当前路径（仅一次）
                initPathInfo();
                // 更新操作时间+启动空闲检测
                updateLastOperateTime();
                scheduleIdleCheckTask();
                return;

            } catch (JSchException e) {
                lastException = e;
                retryCount++;
                log.warn("第{}次连接失败：{}", retryCount, e.getMessage());
                if (retryCount <= connectRetryTimes) {
                    try {
                        Thread.sleep(CONNECT_RETRY_INTERVAL);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new JSchException("连接重试被中断", ie);
                    }
                }
            }
        }

        throw new JSchException(String.format("连接SSH服务器%s:%d失败（重试%d次）",
                host, port, connectRetryTimes), lastException);
    }

    /**
     * 初始化家目录和当前路径
     */
    private void initPathInfo() throws IOException {
        // 获取家目录
        String homeResult = executeCommand("echo $HOME", true);
        this.homeDir = homeResult.trim().isEmpty() ? "~" : homeResult.trim();
        // 获取初始当前路径
        updateCurrentPath();
        log.debug("初始化路径信息 - 家目录：{}，当前路径：{}", homeDir, currentPath);
    }

    /**
     * 更新当前路径（执行pwd命令）
     */
    private void updateCurrentPath() throws IOException {
        String pwdResult = executeCommand("pwd", true);
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
     * 执行单个命令（核心：Shell通道执行，保持上下文）
     *
     * @param command 要执行的命令
     */
    public String executeCommand(String command) throws IOException {
        return executeCommand(command, false);
    }

    /**
     * 重载：执行单个命令（区分内部/外部命令）
     */
    private String executeCommand(String command, boolean isInternal) throws IOException {
        if (session == null || !session.isConnected() || channelShell == null || !channelShell.isConnected()) {
            throw new IOException("SSH连接或Shell通道未建立");
        }

        if (!isInternal) {
            updateLastOperateTime();
            log.info("开始执行SSH命令：{}", command);
        }

        StringBuilder result = new StringBuilder();
        try {
            // 向Shell通道写入命令（加换行符模拟回车执行）
            shellOutput.write((command + "\n").getBytes(StandardCharsets.UTF_8));
            shellOutput.flush();

            // 等待命令执行完成（简单的超时控制）
            long startTime = System.currentTimeMillis();
            String line;
            // 读取输出，直到出现提示符（表示命令执行完成）
            while ((System.currentTimeMillis() - startTime) < DEFAULT_COMMAND_TIMEOUT) {
                if (shellReader.ready()) {
                    line = shellReader.readLine();
                    if (line == null) break;

                    // 过滤掉命令回显和提示符（仅保留执行结果）
                    if (!isInternal) {
                        // 过滤掉命令本身的回显（如输入cd test后，shell会回显cd test）
                        if (!line.trim().equals(command) &&
                                !line.trim().startsWith(username + "@") && // 过滤提示符
                                !line.isEmpty()) { // 过滤空行
                            result.append(line).append(System.lineSeparator());
                        }
                    } else {
                        // 内部命令直接保留结果（如pwd的输出）
                        result.append(line);
                    }
                } else {
                    Thread.sleep(100);
                }
            }

            // 非内部命令：更新路径 + 记录日志
            if (!isInternal) {
                updateCurrentPath(); // 实时更新路径
                log.info("命令执行完成：{}，当前路径：{}", command, currentPath);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("命令执行被中断：" + command, e);
        }

        String cmdResult = result.toString().trim();
        if (!isInternal) {
            log.debug("命令执行结果：{}", cmdResult);
        }
        return cmdResult;
    }

    /**
     * 执行多条命令（带提示符输出）
     */
    public String executeCommands(String... commands) throws IOException {
        updateLastOperateTime();
        log.info("开始执行多条SSH命令，共{}条", commands.length);
        StringBuilder allResults = new StringBuilder();

        for (String cmd : commands) {
            // 生成实时提示符
            String prompt = getRealPrompt();
            // 拼接提示符+命令
            allResults.append(prompt).append(" ").append(cmd)
                    .append(System.lineSeparator());
            // 执行命令并获取结果
            String cmdResult = executeCommand(cmd);
            allResults.append(cmdResult)
                    .append(System.lineSeparator()).append(System.lineSeparator());
        }

        log.info("多条命令执行完成");
        return allResults.toString();
    }

    /**
     * 生成实时提示符
     */
    private String getRealPrompt() {
        String symbol = "root".equals(this.username) ? "#" : "$";
        return String.format("%s@%s:%s%s",
                this.username,
                this.host,
                this.currentPath,
                symbol);
    }

    /**
     * 更新最后操作时间
     */
    private void updateLastOperateTime() {
        lastOperateTime.set(System.currentTimeMillis());
    }

    /**
     * 启动空闲检测任务
     */
    private void scheduleIdleCheckTask() {
        if (idleCheckScheduled) return;

        IDLE_CHECK_EXECUTOR.scheduleAtFixedRate(() -> {
            try {
                if (session == null || !session.isConnected()) {
                    idleCheckScheduled = false;
                    return;
                }

                long idleTime = System.currentTimeMillis() - lastOperateTime.get();
                long idleTimeSeconds = idleTime / 1000;

                log.debug("SSH连接空闲时间：{}秒（阈值：{}秒）", idleTimeSeconds, idleTimeoutSeconds);

                if (idleTimeSeconds >= idleTimeoutSeconds) {
                    log.warn("SSH连接空闲超时，自动关闭：{}:{}", host, port);
                    disconnect();
                }
            } catch (Exception e) {
                log.error("空闲检测任务执行异常", e);
            }
        }, IDLE_CHECK_PERIOD_SECONDS, IDLE_CHECK_PERIOD_SECONDS, TimeUnit.SECONDS);

        idleCheckScheduled = true;
    }

    /**
     * 关闭连接（释放所有资源）
     */
    public void disconnect() {
        // 关闭Shell通道相关资源
        try {
            if (shellReader != null) shellReader.close();
            if (shellInput != null) shellInput.close();
            if (shellOutput != null) shellOutput.close();
        } catch (IOException e) {
            log.error("关闭Shell流失败", e);
        }

        // 关闭Shell通道
        if (channelShell != null && channelShell.isConnected()) {
            channelShell.disconnect();
            log.debug("Shell通道已关闭");
        }

        // 关闭Session
        if (session != null && session.isConnected()) {
            session.disconnect();
            log.info("关闭SSH连接：{}:{}", host, port);
        }

        idleCheckScheduled = false;
    }

    @Override
    public void close() {
        disconnect();
    }
}
