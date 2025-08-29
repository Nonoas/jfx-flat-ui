package github.nonoas.jfx.flat.ui.control;

import github.nonoas.jfx.flat.ui.Colors;
import github.nonoas.jfx.flat.ui.utils.UIUtil;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Callback;

/**
 * @author Nonoas
 * @date 2022/7/18
 */
public class PopupTextField extends TextField {
    private Callback<String, Node> popupContentFactory;
    private Popup popup;

    private static final double SHADOW_SIZE = 10.0;

    public PopupTextField() {
        initListener();
    }

    private void initListener() {
        textProperty().addListener((observable, oldValue, newValue) -> {
            if (popup == null) {
                popup = new Popup();
                popup.setAutoHide(true);
            }
            Node node = updatePopupContent(newValue);
            popup.getContent().setAll(node);
            Bounds bounds = UIUtil.getScreeBounds(this);

            if (!popup.isShowing()) {
                popup.show(this, bounds.getMinX() - SHADOW_SIZE, bounds.getMaxY() - SHADOW_SIZE / 2);
            }
            if (newValue.isEmpty()) {
                popup.hide();
            }
        });
    }

    private Node updatePopupContent(String newValue) {
        VBox vBox = new VBox();
        vBox.setEffect(new DropShadow(SHADOW_SIZE, 0, SHADOW_SIZE / 4, Colors.DROP_SHADOW_0));
        vBox.setPrefWidth(PopupTextField.this.getWidth());
        vBox.setStyle("-fx-background-color: white");

        if (popupContentFactory != null) {
            vBox.getChildren().setAll(popupContentFactory.call(newValue));
        } else {
            vBox.getChildren().setAll(new Label("你在找「" + newValue + "」吗"));
        }
        return vBox;
    }

    public void setPopupContentFactory(Callback<String, Node> popupContentFactory) {
        this.popupContentFactory = popupContentFactory;
    }

    public void hidePopup() {
        if (popup == null) {
            return;
        }
        popup.hide();
    }
}
