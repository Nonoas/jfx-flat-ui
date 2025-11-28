package github.nonoas.jfx.flat.ui.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class Card extends Control {

    // --- Properties ---

    // 1. 标题
    private final StringProperty title = new SimpleStringProperty(this, "title");
    public final StringProperty titleProperty() { return title; }
    public final String getTitle() { return title.get(); }
    public final void setTitle(String value) { title.set(value); }

    // 2. 描述
    private final StringProperty description = new SimpleStringProperty(this, "description");
    public final StringProperty descriptionProperty() { return description; }
    public final String getDescription() { return description.get(); }
    public final void setDescription(String value) { description.set(value); }

    // 3. 图标 (Graphic)
    private final ObjectProperty<Node> graphic = new SimpleObjectProperty<>(this, "graphic");
    public final ObjectProperty<Node> graphicProperty() { return graphic; }
    public final Node getGraphic() { return graphic.get(); }
    public final void setGraphic(Node value) { graphic.set(value); }

    // --- Constructor ---

    public Card() {
        // 设置默认的 StyleClass，方便 CSS 选择器定位 (例如 .card)
        getStyleClass().setAll("card");
    }

    public Card(String title, String description, Node graphic) {
        this();
        setTitle(title);
        setDescription(description);
        setGraphic(graphic);
    }

    // --- Skinning ---

    @Override
    protected Skin<?> createDefaultSkin() {
        return new CardSkin(this);
    }
}