package github.nonoas.jfx.flat.ui.stage;

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

/**
 * toast 消息提示
 */
public class ToastQueue {

    // 内部类：存储每条消息和时长
    private static class ToastMessage {
        private final Stage stage;
        private final String text;
        private final int durationMillis;

        public ToastMessage(Stage stage, String text, int durationMillis) {
            this.stage = stage;
            this.text = text;
            this.durationMillis = durationMillis;
        }

        public Stage getStage() {
            return stage;
        }

        public String getText() {
            return text;
        }

        public int getDurationMillis() {
            return durationMillis;
        }
    }

    private static final Queue<ToastMessage> messageQueue = new LinkedList<>();
    private static boolean isShowing = false;

    /**
     * 显示一个 toast
     */
    public static void show(Stage stage, String message, int durationMillis) {
        Platform.runLater(() -> {
            messageQueue.offer(new ToastMessage(stage, message, durationMillis));
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
        popup.show(toast.stage);

        // 更新位置
        Runnable updatePosition = () -> {
            popup.setX(toast.stage.getX() + toast.stage.getWidth() / 2 - root.getWidth() / 2);
            popup.setY(toast.stage.getY() + toast.stage.getHeight() - 100);
        };

        updatePosition.run();

        // 监听窗口移动/缩放
        toast.stage.xProperty().addListener((obs, oldV, newV) -> updatePosition.run());
        toast.stage.yProperty().addListener((obs, oldV, newV) -> updatePosition.run());
        toast.stage.widthProperty().addListener((obs, oldV, newV) -> updatePosition.run());
        toast.stage.heightProperty().addListener((obs, oldV, newV) -> updatePosition.run());

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
