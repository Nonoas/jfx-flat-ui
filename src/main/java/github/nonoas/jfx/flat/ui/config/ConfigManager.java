package github.nonoas.jfx.flat.ui.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 通用的配置管理工具类。
 * 配置文件的路径结构为：[Root Directory]/.[appName_camelCase]/[FileName].properties。
 * 支持线程安全，自动校正文件后缀。
 */
public class ConfigManager {

    private final Properties properties = new Properties();
    private final Path configPath;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    // --- 辅助方法：确保文件后缀为 .properties ---

    private static final String PROPERTIES_SUFFIX = ".properties";

    /**
     * 检查文件名是否以 .properties 结尾，如果不是，则添加。
     * 例如：输入 "config"，返回 "config.properties"
     */
    private String ensurePropertiesExtension(String fileName) {
        if (fileName.toLowerCase().endsWith(PROPERTIES_SUFFIX)) {
            return fileName;
        }
        return fileName + PROPERTIES_SUFFIX;
    }

    // --- 辅助方法：转换为隐藏的小驼峰命名（保持不变）---

    private String toHiddenCamelCase(String input) {
        if (input == null || input.isEmpty()) {
            return ".appConfig";
        }

        StringBuilder sb = new StringBuilder();
        boolean capitalizeNext = false;

        for (char c : input.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                if (sb.length() == 0) {
                    sb.append(Character.toLowerCase(c));
                } else if (capitalizeNext) {
                    sb.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    sb.append(c);
                }
            } else {
                capitalizeNext = true;
            }
        }

        String camelCaseName = sb.toString().isEmpty() ? "appConfig" : sb.toString();
        return "." + camelCaseName;
    }


    // --- 构造函数与初始化 ---

    /**
     * 构造函数（使用默认根目录：user.home）。
     */
    public ConfigManager(String fileName, String appName) {
        this(fileName, appName, System.getProperty("user.home"));
    }

    /**
     * 构造函数（指定自定义根目录）。
     */
    public ConfigManager(String fileName, String appName, String rootDirectory) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("配置文件名不能为空。");
        }
        if (appName == null || appName.trim().isEmpty()) {
            throw new IllegalArgumentException("应用目录名 (appName) 不能为空。");
        }
        if (rootDirectory == null || rootDirectory.trim().isEmpty()) {
            rootDirectory = System.getProperty("user.home");
        }

        // 1. **新增校正：** 确保文件名具有 .properties 后缀
        String correctedFileName = ensurePropertiesExtension(fileName);

        // 2. 转换应用名为隐藏小驼峰格式
        String processedAppName = toHiddenCamelCase(appName);

        // 3. 确定并创建完整的应用配置目录：[rootDirectory]/.[appName_camelCase]
        Path rootDir = Paths.get(rootDirectory);
        Path appDir = rootDir.resolve(processedAppName);

        try {
            if (Files.notExists(appDir)) {
                Files.createDirectories(appDir);
                System.out.println("创建应用配置目录: " + appDir);
            }
        } catch (IOException e) {
            System.err.println("错误：无法创建应用配置目录: " + appDir);
            throw new RuntimeException("无法初始化配置管理器，目录创建失败。", e);
        }

        // 4. 确定完整的配置路径：[appDir]/[correctedFileName]
        this.configPath = appDir.resolve(correctedFileName);

        // 5. 尝试加载配置
        loadConfig();
    }

    // --- 文件I/O方法（保持不变） ---

    private void loadConfig() {
        writeLock.lock();
        try {
            properties.clear();

            if (Files.exists(configPath)) {
                try (InputStream input = Files.newInputStream(configPath)) {
                    properties.load(input);
                    System.out.println("配置已从文件加载: " + configPath);
                }
            } else {
                System.out.println("配置文件不存在，已创建空的内存配置: " + configPath);
            }
        } catch (IOException e) {
            System.err.println("加载配置文件时发生IO错误: " + e.getMessage());
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }

    public void saveConfig() {
        writeLock.lock();
        try (OutputStream output = Files.newOutputStream(configPath)) {
            properties.store(output, "Configuration file saved at " + System.currentTimeMillis());
            System.out.println("配置已成功保存到文件: " + configPath);
        } catch (IOException e) {
            System.err.println("保存配置文件时发生IO错误: " + e.getMessage());
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }

    // --- 查 (Read) ---

    public String get(String key) {
        readLock.lock();
        try {
            return properties.getProperty(key);
        } finally {
            readLock.unlock();
        }
    }

    public String get(String key, String defaultValue) {
        readLock.lock();
        try {
            return properties.getProperty(key, defaultValue);
        } finally {
            readLock.unlock();
        }
    }

    // --- 增/改/删 (Write) ---

    public void set(String key, String value) {
        properties.setProperty(key, value);
    }

    public String remove(String key) {
        return (String) properties.remove(key);
    }
}