package github.nonoas.jfx.flat.ui.stage;

import github.nonoas.jfx.flat.ui.control.UIFactory;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HeaderBar;
import javafx.scene.layout.HeaderButtonType;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Optional;

/**
 * 自定义无边框 Dialog，使用 HeaderBar 作为标题栏
 * <p>
 * 基于 JavaFX 25.0.2 的 StageStyle.EXTENDED 和 HeaderBar 实现
 * <p>
 * 注意：StageStyle.EXTENDED、HeaderBar 和 HeaderButtonType 是 JavaFX 23+ 的实验性 API
 *
 * @param <T> 对话框返回类型
 * @author Nonoas
 */
public class FlatDialog<T> extends Dialog<T> {

    private double xOffset = 0;
    private double yOffset = 0;

    private final BorderPane rootPane = new BorderPane();
    private final HBox sysButtonBox = new HBox();

    private HeaderBar headerBar;
    private Label titleLabel;
    private Region contentContainer;

    private boolean closable = true;
    private boolean minimizable = false;
    private boolean maximizable = false;

    /**
     * 创建 FlatDialog，默认无所有者
     */
    public FlatDialog() {
        this(null);
    }

    /**
     * 创建 FlatDialog
     *
     * @param owner 父窗口
     */
    public FlatDialog(Stage owner) {
        initDialog(owner);
    }

    /**
     * 初始化对话框
     */
    private void initDialog(Stage owner) {
        // 设置所有者
        if (owner != null) {
            initOwner(owner);
        }

        // 使用 StageStyle.EXTENDED 创建无边框窗口（JavaFX 23+）
        initStyle(StageStyle.EXTENDED);

        // 初始化对话框面板
        DialogPane dialogPane = getDialogPane();
        dialogPane.setContent(rootPane);
        dialogPane.setPadding(Insets.EMPTY);
        dialogPane.setHeader(null);
        dialogPane.setGraphic(null);

        // 移除默认的样式类，使用自定义样式
        dialogPane.getStyleClass().remove("dialog-pane");
        dialogPane.getStyleClass().add("flat-dialog");

        // 初始化标题栏
        initHeaderBar();

        // 初始化系统按钮容器
        sysButtonBox.setAlignment(Pos.CENTER_RIGHT);
        sysButtonBox.getStyleClass().add("sys-btn-box");
        sysButtonBox.setSpacing(0);

        // 默认只有关闭按钮
        updateSystemButtons();

        // 设置模态
        initModality(Modality.APPLICATION_MODAL);

        // 设置尺寸
        setResizable(true);
    }

    /**
     * 初始化标题栏
     */
    private void initHeaderBar() {
        headerBar = new HeaderBar();
        headerBar.getStyleClass().add("flat-dialog-header");

        // 创建标题标签
        titleLabel = new Label();
        titleLabel.getStyleClass().add("flat-dialog-title");
        titleLabel.setAlignment(Pos.CENTER_LEFT);

        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.setPadding(new Insets(0, 0, 0, 15));

        headerBar.setLeading(titleBox);
        HeaderBar.setAlignment(titleBox, Pos.CENTER_LEFT);

        rootPane.setTop(headerBar);

        // 使标题栏可拖动
        registryDragger(headerBar);
    }

    /**
     * 更新系统按钮
     */
    private void updateSystemButtons() {
        sysButtonBox.getChildren().clear();

        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        if (stage == null) {
            // 场景尚未初始化，延迟更新
            getDialogPane().sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    updateSystemButtons();
                }
            });
            return;
        }

        // 最小化按钮
        if (minimizable) {
            Button minBtn = UIFactory.createMinimizeButton();
            HeaderBar.setButtonType(minBtn, HeaderButtonType.ICONIFY);
            minBtn.setOnAction(e -> stage.setIconified(true));
            sysButtonBox.getChildren().add(minBtn);
        }

        // 最大化按钮
        if (maximizable) {
            Button maxBtn = UIFactory.createMaximizeButton(stage.maximizedProperty());
            HeaderBar.setButtonType(maxBtn, HeaderButtonType.MAXIMIZE);
            maxBtn.setOnAction(e -> stage.setMaximized(!stage.isMaximized()));
            sysButtonBox.getChildren().add(maxBtn);
        }

        // 关闭按钮
        if (closable) {
            Button closeBtn = UIFactory.createCloseButton();
            HeaderBar.setButtonType(closeBtn, HeaderButtonType.CLOSE);
            closeBtn.setOnAction(e -> {
                setResult(null);
                close();
            });
            sysButtonBox.getChildren().add(closeBtn);
        }

        headerBar.setTrailing(sysButtonBox);
        HeaderBar.setAlignment(sysButtonBox, Pos.CENTER_RIGHT);
    }

    /**
     * 设置对话框内容
     *
     * @param content 内容节点
     */
    public void setDialogContent(Node content) {
        if (contentContainer != null) {
            rootPane.getChildren().remove(contentContainer);
        }
        this.contentContainer = (Region) content;
        rootPane.setCenter(content);
        BorderPane.setMargin(content, new Insets(15));
    }

    /**
     * 设置是否显示关闭按钮
     */
    public void setClosable(boolean closable) {
        this.closable = closable;
        updateSystemButtons();
    }

    /**
     * 设置是否显示最小化按钮
     */
    public void setMinimizable(boolean minimizable) {
        this.minimizable = minimizable;
        updateSystemButtons();
    }

    /**
     * 设置是否显示最大化按钮
     */
    public void setMaximizable(boolean maximizable) {
        this.maximizable = maximizable;
        updateSystemButtons();
    }

    /**
     * 获取标题栏
     *
     * @return HeaderBar 实例
     */
    public HeaderBar getHeaderBar() {
        return headerBar;
    }

    /**
     * 获取系统按钮容器
     *
     * @return 系统按钮容器
     */
    public HBox getSystemButtonBox() {
        return sysButtonBox;
    }

    /**
     * 注册拖动节点
     *
     * @param node 可拖动节点
     * @return 当前对话框
     */
    public FlatDialog<T> registryDragger(Node node) {
        node.setOnMousePressed(pressHandler);
        node.setOnMouseDragged(draggedHandler);
        node.setOnMouseClicked(clickHandler);
        return this;
    }

    /**
     * 注销拖动节点
     *
     * @param node 节点
     * @return 当前对话框
     */
    public FlatDialog<T> removeDragger(Node node) {
        node.setOnMousePressed(null);
        node.setOnMouseDragged(null);
        node.setOnMouseClicked(null);
        return this;
    }

    /**
     * 按下监听
     */
    private final EventHandler<MouseEvent> pressHandler = event -> {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    };

    /**
     * 拖动监听
     */
    private final EventHandler<MouseEvent> draggedHandler = event -> {
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        if (stage != null && !stage.isMaximized()) {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        }
    };

    /**
     * 双击最大化/还原
     */
    private final EventHandler<MouseEvent> clickHandler = event -> {
        if (maximizable && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            Stage stage = (Stage) getDialogPane().getScene().getWindow();
            if (stage != null) {
                stage.setMaximized(!stage.isMaximized());
            }
        }
    };

    /**
     * 添加样式类到根面板
     *
     * @param styleClass 样式类名
     */
    public void addRootStyleClass(String styleClass) {
        rootPane.getStyleClass().add(styleClass);
    }

    /**
     * 设置对话框大小
     *
     * @param width  宽度
     * @param height 高度
     */
    public void setDialogSize(double width, double height) {
        getDialogPane().setPrefSize(width, height);
        getDialogPane().setMinSize(width, height);
    }

    /**
     * 显示对话框并等待结果（便捷方法）
     *
     * @return 结果 Optional
     */
    public Optional<T> showAndWaitResult() {
        return showAndWait();
    }
}
