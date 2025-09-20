package github.nonoas.jfx.flat.ui.control;

import github.nonoas.jfx.flat.ui.Colors;
import github.nonoas.jfx.flat.ui.pane.SVGImage;
import github.nonoas.jfx.flat.ui.theme.Styles;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class UIFactory {
    private UIFactory() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static Button createMinimizeButton() {
        SVGImage svgImage = new SVGImage(SVGPath.MINIMIZE_BUTTON.value(), Colors.COMMON_BTN_COLOR);
        svgImage.setSize(15, 1);
        return getBaseButton(svgImage);
    }

    public static Button createMaximizeButton(SimpleBooleanProperty maximizedProperty) {
        // 非最大化时图标
        SVGImage svgImage = new SVGImage(SVGPath.MAXIMIZE_BUTTON.value(), Colors.COMMON_BTN_COLOR);
        svgImage.setSize(15, 15);
        // 最大化时图标
        SVGImage svgImage0 = new SVGImage(SVGPath.MAXIMIZE_BUTTON_0.value(), Colors.COMMON_BTN_COLOR);
        svgImage0.setSize(15, 15);

        SVGButton btn = (SVGButton) getBaseButton(svgImage);
        maximizedProperty.addListener((observableValue, aBoolean, newVal) -> {
            if (!newVal) {
                btn.setGraphic(svgImage);
            } else {
                btn.setGraphic(svgImage0);
            }
        });
        return btn;
    }

    public static Button createCloseButton() {
        SVGImage svgImage = new SVGImage(SVGPath.CLOSE_BUTTON.value(), Colors.COMMON_BTN_COLOR);
        svgImage.setSize(15, 15);
        return new SVGButton.SvgButtonBuilder()
                .graphic(svgImage)
                .graphicColor(Colors.COMMON_BTN_COLOR)
                .graphicColorHover(Color.WHITE)
                .backgroundColor(Color.TRANSPARENT)
                .backgroundColorHover(Color.valueOf("#f55"))
                .build();
    }

    public static Button createPinButton(Stage stage) {
        SVGImage svgImage = new SVGImage(SVGPath.PIN_BUTTON.value(), Colors.COMMON_BTN_COLOR);
        svgImage.setSize(15, 15);

        SVGImage svgImageTop = new SVGImage(SVGPath.PIN_BUTTON.value(), Color.valueOf("#4bbf73"));
        svgImageTop.setSize(15, 15);

        SVGImage graphic = stage.isAlwaysOnTop() ? svgImageTop : svgImage;
        SVGButton btn = new SVGButton(graphic);
        btn.setStyle("-fx-background-color: transparent");
        btn.setBackGroundColor(Color.TRANSPARENT, Colors.HOVER_0);

        btn.setOnAction(e -> {
            boolean onTop = !stage.isAlwaysOnTop();
            stage.setAlwaysOnTop(onTop);
        });

        stage.alwaysOnTopProperty().addListener((o, n, v) -> {
            btn.setGraphic(v ? svgImageTop : svgImage);
        });

        return btn;
    }

    public static Button createMenuButton() {
        SVGImage svgImage = new SVGImage(SVGPath.SETTING_BUTTON.value(), Colors.COMMON_BTN_COLOR);
        svgImage.setSize(15, 15);
        return getBaseButton(svgImage);
    }

    public static Button getBaseButton(SVGImage svgImage) {
        return new SVGButton.SvgButtonBuilder()
                .graphic(svgImage)
                .backgroundColor(Color.TRANSPARENT)
                .backgroundColorHover(Colors.HOVER_0)
                .build();
    }

    /**
     * 创建基础样式的按钮
     * @param text 按钮文本
     * @return 基础样式的按钮
     */
    public static Button getAccentButton(String text) {
        Button button = new Button(text);
        button.setStyle(Styles.ACCENT);
        return button;
    }
}
