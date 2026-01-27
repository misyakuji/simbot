package com.miko.util;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * SSH工具类 - 使用exec通道实现（稳定版）
 * exec通道专为执行命令设计，不需要PTY，兼容性更好
 */
@Slf4j
public class SSHExecUtil implements AutoCloseable {
    private static final int DEFAULT_SSH_PORT = 22;
    private static final int DEFAULT_CONNECT_TIMEOUT = 30000;

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    private final JSch jsch;
    private Session session;
    private volatile boolean isConnected = false;

    private SSHExecUtil(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.jsch = new JSch();
    }

    /**
     * 静态工厂方法
     */
    public static SSHExecUtil create(String host, int port, String username, String password) {
        return new SSHExecUtil(host, port, username, password);
    }

    public static SSHExecUtil create(String host, String username, String password) {
        return new SSHExecUtil(host, DEFAULT_SSH_PORT, username, password);
    }

    /**
     * 连接SSH
     */
    public void connect() throws JSchException {
        log.info("开始连接SSH服务器：{}:{}，用户名：{}", host, port, username);

        session = jsch.getSession(username, host, port);
        session.setPassword(password);

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setTimeout(DEFAULT_CONNECT_TIMEOUT);

        log.debug("正在建立SSH连接...");
        long start = System.currentTimeMillis();
        session.connect(DEFAULT_CONNECT_TIMEOUT);
        long elapsed = System.currentTimeMillis() - start;
        log.info("SSH连接成功，耗时：{}ms", elapsed);

        isConnected = true;
    }

    /**
     * 执行命令
     */
    public String executeCommand(String command) throws Exception {
        if (!isConnected) {
            throw new Exception("SSH未连接");
        }

        log.info("执行命令：{}", command);
        long start = System.currentTimeMillis();

        ChannelExec channel = null;
        StringBuilder result = new StringBuilder();
        StringBuilder errorResult = new StringBuilder();

        try {
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);

            InputStream in = channel.getInputStream();
            InputStream err = channel.getExtInputStream();

            channel.connect();

            // 读取标准输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }

            // 读取错误输出
            BufferedReader errReader = new BufferedReader(new InputStreamReader(err, StandardCharsets.UTF_8));
            while ((line = errReader.readLine()) != null) {
                errorResult.append(line).append("\n");
            }

            // 等待命令完成
            while (!channel.isClosed()) {
                Thread.sleep(100);
            }

            int exitStatus = channel.getExitStatus();
            long elapsed = System.currentTimeMillis() - start;

            String output = result.toString().trim();
            log.info("命令执行完成，耗时：{}ms，退出状态：{}", elapsed, exitStatus);

            if (!errorResult.isEmpty()) {
                log.warn("命令错误输出：{}", errorResult);
            }

            return output;

        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
    }

    /**
     * 执行多条命令（使用 && 连接）
     */
    public String executeCommands(String... commands) throws Exception {
        log.info("执行多条命令，共{}条", commands.length);

        StringBuilder allResults = new StringBuilder();
        for (String cmd : commands) {
            allResults.append(cmd).append("\n");
            String result = executeCommand(cmd);
            allResults.append(result).append("\n\n");
        }

        log.info("多条命令执行完成");
        return allResults.toString();
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (session != null && session.isConnected()) {
            session.disconnect();
            isConnected = false;
            log.info("SSH连接已关闭：{}:{}", host, port);
        }
    }

    @Override
    public void close() {
        disconnect();
    }

    /**
     * 获取连接状态
     */
    public boolean isConnected() {
        return isConnected && session != null && session.isConnected();
    }
}
