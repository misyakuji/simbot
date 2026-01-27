package com.miko;

import com.miko.util.SSHExecUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SSH工具类测试（基于exec通道的稳定版本）
 * 注意：运行此测试前请确保SSH服务器可访问
 * 如不需要SSH测试，可使用@Disabled跳过
 */
@Slf4j
class SSHUtilTests {

    // 默认SSH连接配置（本地测试）
    private static final String SSH_HOST = "127.0.0.1";
    private static final int SSH_PORT = 22;
    private static final String SSH_USERNAME = "miko";
    private static final String SSH_PASSWORD = "awsl";
    private static final int CONNECT_TIMEOUT = 10000;

    private SSHExecUtil ssh;

    @BeforeEach
    void setUp() {
        log.info("=== 初始化SSH连接测试 ===");
    }

    @AfterEach
    void tearDown() {
        log.info("=== 清理资源 ===");
        if (ssh != null && ssh.isConnected()) {
            ssh.disconnect();
        }
    }

    /**
     * 测试基本连接和命令执行
     */
    @Test
    @Disabled("需要有效的SSH服务器配置才能运行")
    void testBasicConnection() throws Exception {
        ssh = SSHExecUtil.create(SSH_HOST, SSH_PORT, SSH_USERNAME, SSH_PASSWORD);
        ssh.connect();

        // 测试pwd
        String pwd = ssh.executeCommand("pwd");
        assertNotNull(pwd, "pwd命令应有输出");

        // 测试whoami
        String whoami = ssh.executeCommand("whoami");
        assertEquals(SSH_USERNAME, whoami.trim(), "用户名应匹配");

        // 测试ls
        String ls = ssh.executeCommand("ls -la");
        assertNotNull(ls, "ls命令应有输出");

        log.info("✓ 基本连接测试通过");
    }

    /**
     * 测试创建文件夹
     */
    @Test
    @Disabled("需要有效的SSH服务器配置才能运行")
    void testCreateFolders() throws Exception {
        ssh = SSHExecUtil.create(SSH_HOST, SSH_PORT, SSH_USERNAME, SSH_PASSWORD);
        ssh.connect();

        // 创建单个文件夹
        String folder1 = "test_folder_" + System.currentTimeMillis();
        String mkdir1 = ssh.executeCommand("mkdir " + folder1);
        assertTrue(mkdir1.isEmpty(), "mkdir应无错误输出");

        // 创建嵌套文件夹
        String nested = "nested_" + System.currentTimeMillis() + "/level2/level3";
        String mkdir2 = ssh.executeCommand("mkdir -p " + nested);
        assertTrue(mkdir2.isEmpty(), "mkdir -p应无错误输出");

        // 验证文件夹存在
        String ls = ssh.executeCommand("ls -la");
        assertTrue(ls.contains(folder1), "应包含创建的文件夹");

        // 清理
        ssh.executeCommand("rm -rf " + folder1);
        ssh.executeCommand("rm -rf nested_" + System.currentTimeMillis());

        log.info("✓ 创建文件夹测试通过");
    }

    /**
     * 测试路径切换
     */
    @Test
    @Disabled("需要有效的SSH服务器配置才能运行")
    void testPathSwitching() throws Exception {
        ssh = SSHExecUtil.create(SSH_HOST, SSH_PORT, SSH_USERNAME, SSH_PASSWORD);
        ssh.connect();

        // 获取初始路径
        String initialPath = ssh.executeCommand("pwd");

        // 创建测试文件夹
        String testDir = "test_dir_" + System.currentTimeMillis();
        ssh.executeCommand("mkdir " + testDir);

        // 切换到文件夹并验证
        String path1 = ssh.executeCommand("cd " + testDir + " && pwd");
        assertTrue(path1.contains(testDir), "路径应包含文件夹名");

        // 切换到子文件夹
        String subDir = "sub_" + System.currentTimeMillis();
        ssh.executeCommand("cd " + testDir + " && mkdir " + subDir);
        String path2 = ssh.executeCommand("cd " + testDir + " && cd " + subDir + " && pwd");
        assertTrue(path2.contains(testDir) && path2.contains(subDir), "路径应包含两级文件夹名");

        // 返回初始路径
        String backPath = ssh.executeCommand("cd ~ && pwd");
        assertEquals(initialPath.trim(), backPath.trim(), "应返回到初始路径");

        // 清理
        ssh.executeCommand("rm -rf " + testDir);

        log.info("✓ 路径切换测试通过");
    }

    /**
     * 测试文件创建和读写
     */
    @Test
    @Disabled("需要有效的SSH服务器配置才能运行")
    void testFileOperations() throws Exception {
        ssh = SSHExecUtil.create(SSH_HOST, SSH_PORT, SSH_USERNAME, SSH_PASSWORD);
        ssh.connect();

        // 创建文件夹
        String testDir = "file_test_" + System.currentTimeMillis();
        ssh.executeCommand("mkdir " + testDir);

        // 创建文件
        String createResult = ssh.executeCommand(
                "cd " + testDir + " && touch test.txt && echo 'Hello SSHExecUtil' > test.txt"
        );
        assertTrue(createResult.isEmpty(), "文件创建应无错误");

        // 读取文件
        String content = ssh.executeCommand("cd " + testDir + " && cat test.txt");
        assertTrue(content.contains("Hello SSHExecUtil"), "文件内容应匹配");

        // 验证文件存在
        String ls = ssh.executeCommand("cd " + testDir + " && ls -la");
        assertTrue(ls.contains("test.txt"), "应包含创建的文件");

        // 清理
        ssh.executeCommand("rm -rf " + testDir);

        log.info("✓ 文件操作测试通过");
    }

    /**
     * 综合测试：文件夹创建、路径切换、文件操作
     */
    @Test
    @Disabled("需要有效的SSH服务器配置才能运行")
    void testComprehensiveOperations() throws Exception {
        ssh = SSHExecUtil.create(SSH_HOST, SSH_PORT, SSH_USERNAME, SSH_PASSWORD);
        ssh.connect();

        log.info("========================================");
        log.info("开始综合功能测试");
        log.info("========================================");

        // 1. 获取初始状态
        String initialPath = ssh.executeCommand("pwd");
        String currentUser = ssh.executeCommand("whoami");
        log.info("初始路径：{}", initialPath);
        log.info("当前用户：{}", currentUser);
        assertEquals(SSH_USERNAME, currentUser.trim(), "用户名应匹配");

        // 2. 创建测试目录结构
        String rootDir = "root_" + System.currentTimeMillis();
        String sub1 = "sub1";
        String sub2 = "sub2";

        ssh.executeCommand("mkdir -p " + rootDir + "/" + sub1 + "/level3");
        ssh.executeCommand("mkdir -p " + rootDir + "/" + sub2);

        // 3. 验证目录结构
        String tree = ssh.executeCommand("ls -R " + rootDir);
        assertTrue(tree.contains(sub1), "应包含子目录1");
        assertTrue(tree.contains(sub2), "应包含子目录2");
        assertTrue(tree.contains("level3"), "应包含三级目录");

        // 4. 多级路径切换测试
        String path1 = ssh.executeCommand("cd " + rootDir + " && pwd");
        assertTrue(path1.contains(rootDir), "应在根目录");

        String path2 = ssh.executeCommand("cd " + rootDir + "/" + sub1 + "/level3 && pwd");
        assertTrue(path2.contains(rootDir) && path2.contains("level3"), "应在三级目录");

        String path3 = ssh.executeCommand("cd " + rootDir + "/" + sub2 + " && pwd");
        assertTrue(path3.contains(rootDir) && path3.contains(sub2), "应在子目录2");

        // 5. 在各级目录创建文件
        ssh.executeCommand("cd " + rootDir + " && echo 'root' > root.txt");
        ssh.executeCommand("cd " + rootDir + "/" + sub1 + " && echo 'sub1' > sub1.txt");
        ssh.executeCommand("cd " + rootDir + "/" + sub1 + "/level3 && echo 'level3' > level3.txt");
        ssh.executeCommand("cd " + rootDir + "/" + sub2 + " && echo 'sub2' > sub2.txt");

        // 6. 验证文件内容
        String rootFile = ssh.executeCommand("cd " + rootDir + " && cat root.txt");
        assertTrue(rootFile.contains("root"), "根目录文件内容应正确");

        String sub1File = ssh.executeCommand("cd " + rootDir + "/" + sub1 + " && cat sub1.txt");
        assertTrue(sub1File.contains("sub1"), "子目录1文件内容应正确");

        // 7. 返回初始目录
        String backPath = ssh.executeCommand("cd ~ && pwd");
        assertEquals(initialPath.trim(), backPath.trim(), "应返回到初始路径");

        // 8. 清理测试目录
        String cleanResult = ssh.executeCommand("rm -rf " + rootDir);
        assertTrue(cleanResult.isEmpty(), "清理应无错误");

        String verifyClean = ssh.executeCommand("ls -la | grep " + rootDir);
        assertTrue(verifyClean.isEmpty(), "测试目录应已被删除");

        log.info("========================================");
        log.info("✓ 综合功能测试全部通过！");
        log.info("========================================");
        log.info("已验证功能：");
        log.info("  ✓ SSH连接");
        log.info("  ✓ 基本命令执行（pwd, whoami, ls）");
        log.info("  ✓ 文件夹创建（单级、多级嵌套）");
        log.info("  ✓ 路径切换（cd）");
        log.info("  ✓ 多级路径导航");
        log.info("  ✓ 文件创建和写入");
        log.info("  ✓ 文件内容读取");
        log.info("  ✓ 返回初始路径");
        log.info("  ✓ 测试文件清理");
        log.info("========================================");
    }

    /**
     * 快速验证测试（不依赖实际SSH服务器）
     */
    @Test
    void testQuickValidation() {
        // 验证SSHExecUtil类可以正常实例化（不连接）
        SSHExecUtil ssh = SSHExecUtil.create("dummy", 22, "user", "pass");
        assertNotNull(ssh, "SSHExecUtil应该能正常创建");
        assertFalse(ssh.isConnected(), "未连接时状态应为false");

        // 测试disconnect的安全性
        assertDoesNotThrow(() -> ssh.disconnect(), "断开未连接的SSH不应抛出异常");
        log.info("快速验证测试通过");
    }
}
