package github.nonoas.jfx.flat.ui.stage;

import github.nonoas.jfx.flat.ui.control.UIFactory;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HeaderBar;
import javafx.scene.layout.HeaderButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Collection;

/**
 * App窗口，通常作为唯一窗口
 */
public class AppStage extends Stage {

    private double xOffset = 0;
    private double yOffset = 0;

    private final BorderPane rootPane = new BorderPane();
    private final Scene scene = new Scene(rootPane);

    private ObservableList<Node> sysButtons;


    /**
     * 根布局阴影半径
     */
    private static final double ROOT_PANE_SHADOW_RADIUS = 15.0;

    public AppStage() {

        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {

        HeaderBar headerBar = new HeaderBar();
        HeaderBar.setPrefButtonHeight(this, 0);

        Button minBtn = UIFactory.createMinimizeButton();
        Button maxBtn = UIFactory.createMaximizeButton(maximizedProperty());
        Button closeBtn = UIFactory.createCloseButton();

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        sysButtons = hBox.getChildren();
        hBox.getChildren().addAll(minBtn, maxBtn, closeBtn);

        HeaderBar.setButtonType(minBtn, HeaderButtonType.ICONIFY);
        HeaderBar.setButtonType(maxBtn, HeaderButtonType.MAXIMIZE);
        HeaderBar.setButtonType(closeBtn, HeaderButtonType.CLOSE);

        headerBar.setTrailing(hBox);
        HeaderBar.setAlignment(hBox, Pos.CENTER_RIGHT);

        initStyle(StageStyle.EXTENDED);

        rootPane.setTop(headerBar);

        setScene(scene);
    }

    /**
     * 获取标题栏
     *
     * @return 标题栏
     */
    public HeaderBar getHeaderBar() {
        return (HeaderBar) rootPane.getTop();
    }

    /**
     * 设置根布局
     *
     * @param parent 根布局
     */
    public void setContentView(Parent parent) {
        rootPane.setCenter(parent);
    }


    public ObservableList<Node> getSystemButtons() {
        return sysButtons;
    }

    /**
     * 按下监听，用于记录点击时的位置，便于计算窗口拖动距离
     */
    private final EventHandler<MouseEvent> pressHandler = event -> {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    };

    /**
     * 拖动监听，用于设置拖动后窗口的位置
     */
    private final EventHandler<MouseEvent> draggedHandler = event -> {
        if (!isMaximized()) {
            setX(event.getScreenX() - xOffset);
            setY(event.getScreenY() - yOffset);
        }
    };


    /**
     * 注册拖动节点到当前 AppStage:<br/>
     * 拖动注册节点时，窗口会随之移动
     *
     * @param parent 注册节点
     * @return 当前窗口
     */
    public AppStage registryDragger(Parent parent) {
        // 设置窗口拖动
        parent.setOnMousePressed(pressHandler);
        parent.setOnMouseDragged(draggedHandler);
        // 设置双击最大化/还原（仅左键）
        parent.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Stage stage = (Stage) parent.getScene().getWindow();
                setMaximized(!isMaximized());
            }
        });
        return this;
    }

    /**
     * 注销拖动节点
     *
     * @param parent 注销拖动节点
     * @return 当前窗口
     */
    public AppStage removeDragger(Parent parent) {
        // 取消组件的窗口拖动事件
        parent.setOnMousePressed(null);
        parent.setOnMouseDragged(null);
        parent.setOnMouseClicked(null);
        return this;
    }


    /**
     * 添加图标
     *
     * @param images 图标集合
     */
    public final void addIcons(Collection<Image> images) {
        getIcons().addAll(images);
    }

    /**
     * 设置窗口大小
     *
     * @param width  宽度
     * @param height 高度
     */
    public void setSize(double width, double height) {
        setWidth(width);
        setHeight(height);
    }

    public Stage getStage() {
        return this;
    }

    /**
     * 由于 [show] 方法不能重写，显示窗口时可能会做一些其他的操作，所以提供此方法。
     * 调用时，如果窗口处于最小化状态，也会显示出来
     */
    public void display() {
        if (isIconified()) {
            setIconified(false);
        }
        show();
    }

    /**
     * 判断窗口是否在显示在屏幕上，即没有最小化且没有隐藏
     */
    public boolean isInsight() {
        return isShowing() && !isIconified();
    }
}
