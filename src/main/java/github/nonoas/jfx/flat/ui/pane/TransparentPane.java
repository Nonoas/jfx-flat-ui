package github.nonoas.jfx.flat.ui.pane;

import github.nonoas.jfx.flat.ui.common.InsetConstant;
import github.nonoas.jfx.flat.ui.utils.UIUtil;
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

    private static final double CORNER_RADIUS = 10.0; // 设置圆角半径，需与CSS中的值保持一致

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
        clip.setArcWidth(CORNER_RADIUS * 2);  // ArcWidth = 2 * Radius
        clip.setArcHeight(CORNER_RADIUS * 2); // ArcHeight = 2 * Radius
        contentPane.setClip(clip);

        // 根据 shadowPane 尺寸动态更新 clip 大小
        contentPane.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            clip.setWidth(newBounds.getWidth());
            clip.setHeight(newBounds.getHeight());
        });
        getChildren().setAll(shadowPane, sysBtnBox);
    }

    private void initSysButton(HBox sysBtnBox) {
        sysBtnBox.getStyleClass().add("sys-btn-box");
        sysBtnBox.setAlignment(Pos.CENTER_RIGHT);
        AnchorPane.setTopAnchor(sysBtnBox, 0.0);
        AnchorPane.setRightAnchor(sysBtnBox, 0.0);
    }

    private void initShadowPane() {
        shadowPane.setStyle(
                "-fx-background-color: white;" + // 必须设置背景色才能显示圆角和阴影
                        "-fx-background-radius: " + CORNER_RADIUS + ";" // 设置圆角半径
        );
        shadowPane.getStyleClass().add("jfu-shadow-pane");
        shadowPane.setEffect(getDropShadow());
        shadowPane.getChildren().setAll(contentPane);
        UIUtil.setAnchor(shadowPane, 0.0);
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
        if (content instanceof Region ) {
            Region region = (Region) content;
            UIUtil.setAnchor(region, 0);
            region.setMinSize(0, 0);
            contentPane.getChildren().setAll(region);
        } else {
            contentPane.getChildren().setAll(content);
        }
    }

    private DropShadow getDropShadow() {
        DropShadow dropshadow = new DropShadow();
        dropshadow.setRadius(15);
        dropshadow.setSpread(0.15);
        dropshadow.setColor(Color.rgb(0, 0, 0, 0.3));
        return dropshadow;
    }
}
