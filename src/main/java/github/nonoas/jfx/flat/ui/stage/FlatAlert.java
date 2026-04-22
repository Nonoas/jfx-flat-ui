package github.nonoas.jfx.flat.ui.stage;

import github.nonoas.jfx.flat.ui.control.UIFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * 自定义无边框 Alert，使用 HeaderBar 作为标题栏
 * <p>
 * 基于 JavaFX 25.0.2 的 StageStyle.EXTENDED 和 HeaderBar 实现
 * <p>
 * 注意：StageStyle.EXTENDED、HeaderBar 和 HeaderButtonType 是 JavaFX 23+ 的实验性 API
 *
 * @author Nonoas
 */
public class FlatAlert extends FlatDialog<ButtonType> {

    /**
     * 警告类型
     */
    public enum AlertType {
        INFORMATION,
        WARNING,
        ERROR,
        CONFIRMATION,
        NONE
    }

    private AlertType alertType = AlertType.INFORMATION;
    private String headerText;
    private String contentText;

    private final VBox contentBox = new VBox();
    private final Label headerLabel = new Label();
    private final Label contentLabel = new Label();
    private final HBox buttonBox = new HBox();

    private ButtonType result = ButtonType.CANCEL;

    /**
     * 创建 FlatAlert
     */
    public FlatAlert() {
        this(AlertType.INFORMATION);
    }

    /**
     * 创建 FlatAlert
     *
     * @param alertType 警告类型
     */
    public FlatAlert(AlertType alertType) {
        this(alertType, null, null, null);
    }

    /**
     * 创建 FlatAlert
     *
     * @param alertType   警告类型
     * @param contentText 内容文本
     */
    public FlatAlert(AlertType alertType, String contentText) {
        this(alertType, contentText, null, null);
    }

    /**
     * 创建 FlatAlert
     *
     * @param alertType   警告类型
     * @param contentText 内容文本
     * @param title       标题
     * @param headerText  头部文本
     */
    public FlatAlert(AlertType alertType, String contentText, String title, String headerText) {
        super();
        this.alertType = alertType;
        this.contentText = contentText;
        this.headerText = headerText;

        if (title != null) {
            setTitle(title);
        }

        initView();
        initButtons();
        applyAlertTypeStyle();
    }

    /**
     * 创建 FlatAlert（完整参数）
     *
     * @param owner       父窗口
     * @param alertType   警告类型
     * @param contentText 内容文本
     * @param title       标题
     * @param headerText  头部文本
     */
    public FlatAlert(Stage owner, AlertType alertType, String contentText, String title, String headerText) {
        super(owner);
        this.alertType = alertType;
        this.contentText = contentText;
        this.headerText = headerText;

        if (title != null) {
            setTitle(title);
        }

        initView();
        initButtons();
        applyAlertTypeStyle();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        // 禁用最大化和最小化按钮
        setMinimizable(false);
        setMaximizable(false);

        // 内容容器
        contentBox.setSpacing(15);
        contentBox.setPadding(new Insets(20));
        contentBox.setAlignment(Pos.CENTER_LEFT);

        // 头部标签
        headerLabel.setWrapText(true);
        headerLabel.getStyleClass().addAll("title-2", "text-bold");
        if (headerText != null && !headerText.isEmpty()) {
            headerLabel.setText(headerText);
        }

        // 内容标签
        contentLabel.setWrapText(true);
        contentLabel.getStyleClass().add("text");
        if (contentText != null) {
            contentLabel.setText(contentText);
        }

        // 按钮容器
        buttonBox.setSpacing(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));

        // 组装内容
        if (headerText != null && !headerText.isEmpty()) {
            contentBox.getChildren().add(headerLabel);
        }
        if (contentText != null && !contentText.isEmpty()) {
            contentBox.getChildren().add(contentLabel);
        }
        contentBox.getChildren().add(buttonBox);

        // 设置内容
        setDialogContent(contentBox);

        // 设置默认大小
        setDialogSize(400, 200);

        // 添加样式类
        addRootStyleClass("flat-alert");
    }

    /**
     * 根据警告类型初始化按钮
     */
    private void initButtons() {
        buttonBox.getChildren().clear();

        switch (alertType) {
            case INFORMATION, WARNING, ERROR -> {
                Button okButton = UIFactory.getAccentButton("确定");
                okButton.setOnAction(e -> {
                    result = ButtonType.OK;
                    setResult(ButtonType.OK);
                    close();
                });
                buttonBox.getChildren().add(okButton);
            }
            case CONFIRMATION -> {
                Button okButton = UIFactory.getAccentButton("确定");
                okButton.setOnAction(e -> {
                    result = ButtonType.OK;
                    setResult(ButtonType.OK);
                    close();
                });

                Button cancelButton = new Button("取消");
                cancelButton.setOnAction(e -> {
                    result = ButtonType.CANCEL;
                    setResult(ButtonType.CANCEL);
                    close();
                });

                buttonBox.getChildren().addAll(cancelButton, okButton);
            }
            case NONE -> {
                // 无按钮
            }
        }
    }

    /**
     * 应用警告类型样式
     */
    private void applyAlertTypeStyle() {
        switch (alertType) {
            case INFORMATION -> {
                headerLabel.getStyleClass().add("text-accent");
            }
            case WARNING -> {
                headerLabel.getStyleClass().add("text-warning");
            }
            case ERROR -> {
                headerLabel.getStyleClass().add("text-danger");
            }
            case CONFIRMATION -> {
                headerLabel.getStyleClass().add("text-accent");
            }
            case NONE -> {
                // 无特殊样式
            }
        }
    }

    // ==================== Setter 方法 ====================

    /**
     * 设置警告类型
     */
    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
        initButtons();
        applyAlertTypeStyle();
    }

    /**
     * 设置警告头部文本
     */
    public void setAlertHeaderText(String headerText) {
        this.headerText = headerText;
        if (headerLabel != null) {
            headerLabel.setText(headerText);
            if (!contentBox.getChildren().contains(headerLabel)) {
                contentBox.getChildren().add(0, headerLabel);
            }
        }
    }

    /**
     * 设置警告内容文本
     */
    public void setAlertContentText(String contentText) {
        this.contentText = contentText;
        if (contentLabel != null) {
            contentLabel.setText(contentText);
            if (!contentBox.getChildren().contains(contentLabel)) {
                int index = contentBox.getChildren().contains(headerLabel) ? 1 : 0;
                contentBox.getChildren().add(index, contentLabel);
            }
        }
    }

    /**
     * 获取警告头部文本
     */
    public String getAlertHeaderText() {
        return headerText;
    }

    /**
     * 获取警告内容文本
     */
    public String getAlertContentText() {
        return contentText;
    }

    /**
     * 获取警告类型
     */
    public AlertType getAlertType() {
        return alertType;
    }

    /**
     * 添加自定义按钮
     *
     * @param button     按钮
     * @param buttonType 按钮类型结果
     */
    public void addButton(Button button, ButtonType buttonType) {
        button.setOnAction(e -> {
            result = buttonType;
            setResult(buttonType);
            close();
        });
        buttonBox.getChildren().add(button);
    }

    /**
     * 清空按钮
     */
    public void clearButtons() {
        buttonBox.getChildren().clear();
    }

    /**
     * 获取按钮容器
     */
    public HBox getButtonBox() {
        return buttonBox;
    }

    /**
     * 获取内容容器
     */
    public VBox getContentBox() {
        return contentBox;
    }

    /**
     * 显示警告并等待结果
     *
     * @return 结果 ButtonType
     */
    public Optional<ButtonType> showAndWaitResult() {
        Optional<ButtonType> optional = showAndWait();
        return optional.isPresent() ? optional : Optional.ofNullable(result);
    }

    // ==================== 静态便捷方法 ====================

    /**
     * 显示信息警告
     */
    public static Optional<ButtonType> showInfo(Stage owner, String title, String header, String content) {
        FlatAlert alert = new FlatAlert(owner, AlertType.INFORMATION, content, title, header);
        return alert.showAndWaitResult();
    }

    /**
     * 显示信息警告
     */
    public static Optional<ButtonType> showInfo(String title, String header, String content) {
        return showInfo(null, title, header, content);
    }

    /**
     * 显示错误警告
     */
    public static Optional<ButtonType> showError(Stage owner, String title, String header, String content) {
        FlatAlert alert = new FlatAlert(owner, AlertType.ERROR, content, title, header);
        return alert.showAndWaitResult();
    }

    /**
     * 显示错误警告
     */
    public static Optional<ButtonType> showError(String title, String header, String content) {
        return showError(null, title, header, content);
    }

    /**
     * 显示警告
     */
    public static Optional<ButtonType> showWarning(Stage owner, String title, String header, String content) {
        FlatAlert alert = new FlatAlert(owner, AlertType.WARNING, content, title, header);
        return alert.showAndWaitResult();
    }

    /**
     * 显示警告
     */
    public static Optional<ButtonType> showWarning(String title, String header, String content) {
        return showWarning(null, title, header, content);
    }

    /**
     * 显示确认对话框
     */
    public static boolean showConfirm(Stage owner, String title, String header, String content) {
        FlatAlert alert = new FlatAlert(owner, AlertType.CONFIRMATION, content, title, header);
        Optional<ButtonType> result = alert.showAndWaitResult();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * 显示确认对话框
     */
    public static boolean showConfirm(String title, String header, String content) {
        return showConfirm(null, title, header, content);
    }
}
