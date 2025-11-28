package github.nonoas.jfx.flat.ui.control;

import github.nonoas.jfx.flat.ui.theme.Styles;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CardSkin extends SkinBase<Card> {

    private final HBox container;
    private final StackPane graphicContainer;
    private final Label titleLabel;
    private final Label descLabel;

    public CardSkin(Card control) {
        super(control);

        // 1. 初始化内部组件
        container = new HBox();
        graphicContainer = new StackPane(); // 用 StackPane 包裹图标以便于控制大小和居中
        titleLabel = new Label();
        descLabel = new Label();

        // 2. 组装结构
        VBox textContainer = new VBox(5); // 文字垂直排列，间距 5
        textContainer.getChildren().addAll(titleLabel, descLabel);
        textContainer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // 让文字区域占据剩余空间
        HBox.setHgrow(textContainer, Priority.ALWAYS);

        container.getChildren().addAll(graphicContainer, textContainer);
        container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        container.setSpacing(15); // 图标与文字的间距

        // 3. 绑定数据 (单向绑定：Control -> Skin)
        titleLabel.textProperty().bind(control.titleProperty());
        descLabel.textProperty().bind(control.descriptionProperty());

        // 监听 Graphic 属性变化
        control.graphicProperty().addListener((obs, oldVal, newVal) -> {
            updateGraphic(newVal);
        });
        updateGraphic(control.getGraphic()); // 初始化加载

        // 4. 应用样式类 (整合 AtlantaFX)
        // 赋予样式类名，以便 CSS 可以单独控制内部组件
        graphicContainer.getStyleClass().add("card-graphic-container");
        titleLabel.getStyleClass().add("card-title");
        descLabel.getStyleClass().add("card-description");

        // --- AtlantaFX 核心集成 ---
        // 使用 AtlantaFX 提供的通用样式来美化字体
        titleLabel.getStyleClass().add(Styles.TITLE_4);
        descLabel.getStyleClass().add(Styles.TEXT_MUTED);

        // 使整个 Skin 成为内容
        getChildren().add(container);
    }

    private void updateGraphic(javafx.scene.Node graphic) {
        graphicContainer.getChildren().clear();
        if (graphic != null) {
            graphicContainer.getChildren().add(graphic);
        }
    }
}