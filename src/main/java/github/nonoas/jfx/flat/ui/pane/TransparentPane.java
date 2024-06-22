package github.nonoas.jfx.flat.ui.pane;

import github.nonoas.jfx.flat.ui.common.InsetConstant;
import github.nonoas.jfx.flat.ui.utils.UIUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * 透明面板
 *
 * @author Nonoas
 * @datetime 2021/12/4 15:42
 */
public class TransparentPane extends AnchorPane {

    /**
     * 内容布局,实际显示节点的布局面板
     */
    private final AnchorPane contentPane = new AnchorPane();

    /**
     * 阴影布局，用于生成阴影
     */
    private final VBox shadowPane = new VBox();

    /**
     * 按钮布局
     */
    private final ObservableList<Node> sysButtons;

    private final Insets ROOT_PADDING = new Insets(InsetConstant.SHADOW_SIZE_1);

    public TransparentPane() {

        this.setPadding(ROOT_PADDING);

        setStyle("-fx-background-color: transparent !important;");

        initShadowPane();
        initContentPane();

        HBox sysBtnBox = new HBox();
        sysButtons = sysBtnBox.getChildren();

        initSysButton(sysBtnBox);

        getChildren().setAll(shadowPane, sysBtnBox);
    }

    private void initSysButton(HBox sysBtnBox) {
        sysBtnBox.getStyleClass().add("sys-btn-box");
        sysBtnBox.setAlignment(Pos.CENTER_RIGHT);
        AnchorPane.setTopAnchor(sysBtnBox, 0.0);
        AnchorPane.setRightAnchor(sysBtnBox, 0.0);
    }

    private void initShadowPane() {
        shadowPane.setStyle("-fx-background-color: white;");
        shadowPane.setEffect(getDropShadow());
        shadowPane.getChildren().setAll(contentPane);
        UIUtil.setAnchor(shadowPane, 0.0);
    }

    private void initContentPane() {
        VBox.setVgrow(contentPane, Priority.ALWAYS);
        Rectangle clip = new Rectangle();
        widthProperty().addListener((ov, old, newVal) -> clip.setWidth((Double) newVal - getPadding().getLeft() - 1));
        heightProperty().addListener((ov, old, newVal) -> clip.setHeight((Double) newVal - getPadding().getTop() - 1));
        clip.setArcHeight(60);
        clip.setArcWidth(60);
        contentPane.setClip(clip);
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
        VBox.setVgrow(content, Priority.ALWAYS);
        if (content instanceof Region region) {
            UIUtil.setAnchor(region, 0);
            contentPane.getChildren().setAll(region);
        } else {
            contentPane.getChildren().setAll(content);
        }
    }

    private DropShadow getDropShadow() {
        // 阴影向外
        DropShadow dropshadow = new DropShadow();
        // 颜色蔓延的距离
        dropshadow.setRadius(15);
        // 颜色变淡的程度
        dropshadow.setSpread(0.15);
        // 设置颜色
        dropshadow.setColor(Color.rgb(0, 0, 0, 0.3));
        return dropshadow;
    }

}
