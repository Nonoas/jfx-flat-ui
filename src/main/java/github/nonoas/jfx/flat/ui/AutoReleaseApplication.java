package github.nonoas.jfx.flat.ui;


import javafx.application.Application;
import javafx.stage.Stage;

/**
 * AutoReleaseApplication 是一个抽象的 Application 基类，
 * 它保证在应用程序关闭时自动调用 ResourceManager 释放所有已注册的资源。
 *
 * @author Nonoas
 * @date 2025/9/27
 * @since 1.0.3
 */
public abstract class AutoReleaseApplication extends Application {

    public AutoReleaseApplication() {
        // 构造器通常用于框架设置
        System.out.println("AutoReleaseApplication 框架初始化...");
    }

    /**
     * JavaFX 应用程序的起点。子类必须实现此方法来构建UI。
     *
     * @param primaryStage 主舞台
     * @throws Exception 启动异常
     */
    @Override
    public abstract void start(Stage primaryStage) throws Exception;

    /**
     * 【关键】此方法在 Application.stop() 之前被调用。
     * 我们重写此方法，用于执行资源的释放操作。
     */
    @Override
    public void stop() throws Exception {
        // 调用资源管理器，释放所有已注册的资源
        ResourceManager.getInstance().releaseAll();
        // 调用父类的 stop 方法
        super.stop();
    }
}
