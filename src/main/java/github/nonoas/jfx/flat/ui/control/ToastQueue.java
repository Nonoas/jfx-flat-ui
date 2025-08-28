package github.nonoas.jfx.flat.ui.control;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.Queue;

public class ToastQueue {

    // 内部类：存储每条消息和时长
    private static class ToastMessage {
        final String text;
        final int durationMillis;
        ToastMessage(String text, int durationMillis) {
            this.text = text;
            this.durationMillis = durationMillis;
        }
    }

    private static final Queue<ToastMessage> messageQueue = new LinkedList<>();
    private static boolean isShowing = false;
    private static Stage ownerStage;

    /**
     * 必须先初始化一次
     */
    public static void init(Stage stage) {
        ownerStage = stage;
    }

    /**
     * 显示一个 toast
     */
    public static void show(String message, int durationMillis) {
        Platform.runLater(() -> {
            messageQueue.offer(new ToastMessage(message, durationMillis));
            if (!isShowing) {
                showNext();
            }
        });
    }

    private static void showNext() {
        ToastMessage toast = messageQueue.poll();
        if (toast == null) {
            isShowing = false;
            return;
        }

        isShowing = true;

        Label label = new Label(toast.text);
        label.setStyle(
                "-fx-background-radius: 10; " +
                        "-fx-background-color: rgba(0,0,0,0.8); " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10px;" +
                        "-fx-font-size: 14px;"
        );

        StackPane root = new StackPane(label);
        root.setStyle("-fx-background-color: transparent;");

        Popup popup = new Popup();
        popup.getContent().add(root);
        popup.show(ownerStage);

        // 更新位置的函数
        Runnable updatePosition = () -> {
            popup.setX(ownerStage.getX() + ownerStage.getWidth() / 2 - root.getWidth() / 2);
            popup.setY(ownerStage.getY() + ownerStage.getHeight() - 100);
        };

        // 初始设置一次
        updatePosition.run();

        // 监听窗口移动/缩放
        ownerStage.xProperty().addListener((obs, oldV, newV) -> updatePosition.run());
        ownerStage.yProperty().addListener((obs, oldV, newV) -> updatePosition.run());
        ownerStage.widthProperty().addListener((obs, oldV, newV) -> updatePosition.run());
        ownerStage.heightProperty().addListener((obs, oldV, newV) -> updatePosition.run());

        // 渐入
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // 停留 + 渐出
        PauseTransition delay = new PauseTransition(Duration.millis(toast.durationMillis));
        delay.setOnFinished(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), root);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(ev -> {
                popup.hide();
                isShowing = false;
                showNext(); // 显示下一个
            });
            fadeOut.play();
        });
        delay.play();
    }

}
