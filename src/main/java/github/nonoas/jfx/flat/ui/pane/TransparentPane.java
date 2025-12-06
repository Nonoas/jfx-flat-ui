package github.nonoas.jfx.flat.ui.pane;

import github.nonoas.jfx.flat.ui.common.InsetConstant;
import github.nonoas.jfx.flat.ui.utils.UIUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * 透明面板（优化版，使用 StackPane 替代 VBox）
 *
 * @author Nonoas
 * @datetime 2021/12/4 15:42
 */
public class TransparentPane extends AnchorPane {

    /**
     * 内容布局, 实际显示节点的布局面板
     */
    private final AnchorPane contentPane = new AnchorPane();

    /**
     * 阴影布局，用于生成阴影
     */
    private final StackPane shadowPane = new StackPane();

    /**
     * 按钮布局
     */
    private final ObservableList<Node> sysButtons;

    private final Insets ROOT_PADDING = new Insets(InsetConstant.SHADOW_SIZE_1);

    public static final double CORNER_RADIUS = 10.0; // 设置圆角半径，需与CSS中的值保持一致

    private final DoubleProperty arcWidth = new SimpleDoubleProperty(CORNER_RADIUS * 2);
    private final DoubleProperty arcHeight = new SimpleDoubleProperty(CORNER_RADIUS * 2);

    public TransparentPane() {
        this.setPadding(ROOT_PADDING);
        setStyle("-fx-background-color: transparent !important;");

        initShadowPane();
        initContentPane();

        HBox sysBtnBox = new HBox();
        sysButtons = sysBtnBox.getChildren();

        initSysButton(sysBtnBox);

        // 给 shadowPane 添加剪裁区域，避免显示超出 padding 的内容
        Rectangle clip = new Rectangle();
        clip.arcWidthProperty().bind(arcWidth);
        clip.arcHeightProperty().bind(arcHeight);
        contentPane.setClip(clip);

        // 根据 shadowPane 尺寸动态更新 clip 大小
        contentPane.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            clip.setWidth(newBounds.getWidth());
            clip.setHeight(newBounds.getHeight());
        });
        sysBtnBox.setViewOrder(-Double.MAX_VALUE);
        contentPane.getChildren().add(sysBtnBox);
        getChildren().setAll(shadowPane);
    }

    private void initSysButton(HBox sysBtnBox) {
        sysBtnBox.getStyleClass().add("sys-btn-box");
        sysBtnBox.setAlignment(Pos.CENTER_RIGHT);
        AnchorPane.setTopAnchor(sysBtnBox, 0.0);
        AnchorPane.setRightAnchor(sysBtnBox, 0.0);
    }

    private void initShadowPane() {
        shadowPane.getStyleClass().add("jfu-shadow-pane");
        shadowPane.setStyle("-fx-background-color: white;");

        shadowPane.setEffect(getDropShadow());
        shadowPane.getChildren().setAll(contentPane);
        UIUtil.setAnchor(shadowPane, 0.0);

        // 🔥 动态圆角绑定到 CSS 变量
        shadowPane.styleProperty().bind(
                Bindings.concat(
                        "-fx-background-color: white;",
                        "-fx-background-radius: ", arcWidthProperty().divide(2).asString(), ";"
                )
        );
    }


    private void initContentPane() {
        // 不需要 VBox.setVgrow 了，StackPane 会自动让它填满
    }

    public ObservableList<Node> getSysButtons() {
        return sysButtons;
    }

    public AnchorPane getContentPane() {
        return this.contentPane;
    }

    /**
     * 设置根布局
     *
     * @param content 根布局
     */
    public void setContent(Node content) {
        if (content instanceof Region) {
            Region region = (Region) content;
            UIUtil.setAnchor(region, 0);
            region.setMinSize(0, 0);
            contentPane.getChildren().add(region);
        } else {
            contentPane.getChildren().add(content);
        }
    }

    private DropShadow getDropShadow() {
        DropShadow dropshadow = new DropShadow();
        dropshadow.setRadius(15);
        dropshadow.setSpread(0.15);
        dropshadow.setColor(Color.rgb(0, 0, 0, 0.3));
        return dropshadow;
    }

    public double getArcWidth() {
        return arcWidth.get();
    }

    public DoubleProperty arcWidthProperty() {
        return arcWidth;
    }

    public void setArcWidth(double arcWidth) {
        this.arcWidth.set(arcWidth);
    }

    public double getArcHeight() {
        return arcHeight.get();
    }

    public DoubleProperty arcHeightProperty() {
        return arcHeight;
    }

    public void setArcHeight(double arcHeight) {
        this.arcHeight.set(arcHeight);
    }
}
