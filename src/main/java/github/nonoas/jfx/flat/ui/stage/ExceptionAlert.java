package github.nonoas.jfx.flat.ui.stage;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

/**
 * 程序异常弹窗提醒
 * <p>
 * 更稳健的实现：
 * 1. 避免在 scene/window 未初始化时直接取 Stage
 * 2. 对资源文件和异常消息做空值保护
 * 3. 堆栈信息展示区域尺寸更合理
 */
public class ExceptionAlert extends Alert {

    private static final String DEFAULT_TITLE = "程序异常";
    private static final String DEFAULT_HEADER = "ლ(ٱ٥ٱლ)，程序出现了一些问题";
    private static final String DEFAULT_CONTENT = "发生了未知异常，请查看详细堆栈信息。";

    public ExceptionAlert(Throwable e) {
        super(AlertType.ERROR);
        initView(e);
    }

    private void initView(Throwable e) {
        Throwable safeThrowable = (e != null) ? e : new RuntimeException("未知异常");

        initAlertMeta(safeThrowable);
        initExpandableStackTrace(safeThrowable);
        initDialogStyle();
        initStageWhenReady();
    }

    /**
     * 初始化弹窗基础文本
     */
    private void initAlertMeta(Throwable e) {
        setTitle(DEFAULT_TITLE);
        setHeaderText(DEFAULT_HEADER);
        setContentText(safeMessage(e));
    }

    /**
     * 初始化可展开的异常堆栈区域
     */
    private void initExpandableStackTrace(Throwable e) {
        String exceptionText = getStackTraceAsString(e);

        Label label = new Label("异常堆栈跟踪如下:");
        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        textArea.setPrefRowCount(15);
        textArea.setPrefColumnCount(80);

        VBox.setVgrow(textArea, Priority.ALWAYS);

        VBox expContent = new VBox(8);
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.getChildren().addAll(label, textArea);

        DialogPane pane = getDialogPane();
        pane.setExpandableContent(expContent);
        pane.setExpanded(false);
    }

    /**
     * 初始化样式
     */
    private void initDialogStyle() {
        DialogPane pane = getDialogPane();

        URL cssUrl = getClass().getResource(getStyleSheets());
        if (cssUrl != null) {
            pane.getStylesheets().add(cssUrl.toExternalForm());
        }

        pane.setMinWidth(300);
        pane.setPrefWidth(640);
        pane.setPrefHeight(300);
    }

    protected String getStyleSheets() {
        return "/css/platform.css";
    }

    /**
     * 在窗口真正创建后再设置 Stage 属性，避免 NPE
     */
    private void initStageWhenReady() {
        setOnShown(event -> {
            DialogPane pane = getDialogPane();
            if (pane.getScene() == null || !(pane.getScene().getWindow() instanceof Stage stage)) {
                return;
            }

            stage.setAlwaysOnTop(true);
            stage.setMinWidth(300.0);

            URL logoUrl = getClass().getResource("/image/logo.png");
            if (logoUrl != null) {
                try {
                    stage.getIcons().add(new Image(logoUrl.toExternalForm()));
                } catch (Exception ignored) {
                    // 图标加载失败不影响主流程
                }
            }
        });
    }

    private String safeMessage(Throwable e) {
        if (e == null) {
            return DEFAULT_CONTENT;
        }
        String message = e.getMessage();
        return (message == null || message.trim().isEmpty()) ? DEFAULT_CONTENT : message;
    }

    private String getStackTraceAsString(Throwable e) {
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            e.printStackTrace(pw);
            pw.flush();
            return sw.toString();
        }
    }

    public static void error(Throwable e) {
        new ExceptionAlert(e).showAndWait();
    }
}