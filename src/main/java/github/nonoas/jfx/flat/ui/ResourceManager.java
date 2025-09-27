package github.nonoas.jfx.flat.ui;

/**
 * @author Nonoas
 * @version 1.0.0
 * @date 2025/9/27
 * @since 1.0.0
 */
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javafx.application.Platform;

public final class ResourceManager {

    private static final ResourceManager INSTANCE = new ResourceManager();
    private final Set<Resource> resources = Collections.synchronizedSet(new HashSet<>());

    private ResourceManager() {
        // 在应用程序关闭时自动释放所有资源
        Platform.runLater(() -> {
            // 当主舞台关闭时，也可以在这里添加逻辑
        });

        // 注册JVM关闭钩子，确保在任何情况下都能释放资源
        Runtime.getRuntime().addShutdownHook(new Thread(this::releaseAll));
    }

    public static ResourceManager getInstance() {
        return INSTANCE;
    }

    /**
     * 注册一个需要管理的资源。
     *
     * @param resource 需要释放的资源实例
     */
    public void register(Resource resource) {
        if (resource != null) {
            resources.add(resource);
        }
    }

    /**
     * 释放所有已注册的资源。
     */
    public void releaseAll() {
        for (Resource resource : new HashSet<>(resources)) {
            try {
                resource.release();
                resources.remove(resource);
            } catch (Exception e) {
                System.err.println("释放资源时发生错误：" + resource.getClass().getName());
                e.printStackTrace();
            }
        }
        resources.clear();
    }
}