package github.nonoas.jfx.flat.ui.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.geometry.HorizontalDirection;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * The default skin for the {@link Switch} control.
 *
 * @author Nonoas
 * @since 1.0
 */

public class SwitchSkin extends SkinBase<Switch> {

    protected static final Duration DEFAULT_ANIMATION_TIME = Duration.millis(200);

    protected final StackPane thumb;
    protected final StackPane thumbArea;
    protected final Label label;
    protected final StackPane labelContainer;
    protected final TranslateTransition transition;

    public SwitchSkin(Switch control) {
        super(control);

        thumb = new StackPane();
        thumb.getStyleClass().setAll("thumb");

        thumbArea = new StackPane();
        thumbArea.getStyleClass().setAll("thumb-area");

        label = new Label();

        labelContainer = new StackPane();
        labelContainer.getStyleClass().add("label-container");

        transition = new TranslateTransition(DEFAULT_ANIMATION_TIME, thumb);
        transition.setFromX(0.0);

        label.textProperty().bind(control.textProperty());
        StackPane.setAlignment(label, Pos.CENTER_LEFT);

        labelContainer.getChildren().addAll(label);
        getChildren().addAll(labelContainer, thumbArea, thumb);

        thumbArea.setOnMouseReleased(event -> mousePressedOnToggleSwitch(control));
        thumb.setOnMouseReleased(event -> mousePressedOnToggleSwitch(control));
        control.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.booleanValue() != oldValue.booleanValue()) {
                selectedStateChanged();
            }
        });
    }

    protected void selectedStateChanged() {
        // stop the transition if it was already running, has no effect otherwise
        transition.stop();
        if (getSkinnable().isSelected()) {
            transition.setRate(1.0);
            transition.jumpTo(Duration.ZERO);
        } else {
            // if we are not selected, we need to go from right to left
            transition.setRate(-1.0);
            transition.jumpTo(transition.getDuration());
        }
        transition.play();
    }

    private void mousePressedOnToggleSwitch(Switch aSwitch) {
        aSwitch.setSelected(!aSwitch.isSelected());
    }

    /**
     * How many milliseconds it should take for the thumb to go from
     * one edge to the other.
     */
    private DoubleProperty thumbMoveAnimationTime = null;

    private DoubleProperty thumbMoveAnimationTimeProperty() {
        if (thumbMoveAnimationTime == null) {
            thumbMoveAnimationTime = new StyleableDoubleProperty(DEFAULT_ANIMATION_TIME.toMillis()) {

                @Override
                public Object getBean() {
                    return SwitchSkin.this;
                }

                @Override
                public String getName() {
                    return "thumbMoveAnimationTime";
                }

                @Override
                protected void invalidated() {
                    // update duration value
                    transition.setDuration(Duration.millis(getValue()));
                }

                @Override
                public CssMetaData<Switch, Number> getCssMetaData() {
                    return StyleableProperties.THUMB_MOVE_ANIMATION_TIME;
                }
            };
        }
        return thumbMoveAnimationTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        Switch c = getSkinnable();

        double thumbWidth = snapSizeX(thumb.prefWidth(-1));
        double thumbHeight = snapSizeX(thumb.prefHeight(-1));
        thumb.resize(thumbWidth, thumbHeight);

        double thumbAreaWidth = snapSizeX(thumbArea.prefWidth(-1));
        double thumbAreaHeight = snapSizeX(thumbArea.prefHeight(-1));
        double thumbAreaY = snapPositionX(contentY + (contentHeight / 2) - (thumbAreaHeight / 2));

        double labelWidth = label.getText() != null && !label.getText().isEmpty() ? contentWidth - thumbAreaWidth : 0;
        double labelX = c.getLabelPosition() == HorizontalDirection.RIGHT ? thumbAreaWidth : 0;

        double thumbAreaX = c.getLabelPosition() == HorizontalDirection.RIGHT ? 0 : labelWidth;

        thumbArea.resize(thumbAreaWidth, thumbAreaHeight);
        thumbArea.setLayoutX(thumbAreaX);
        thumbArea.setLayoutY(thumbAreaY);

        labelContainer.resize(labelWidth, thumbAreaHeight);
        labelContainer.setLayoutX(labelX);
        labelContainer.setLayoutY(thumbAreaY);

        // layout the thumb on the "unselected" position
        thumb.setLayoutX(thumbArea.getLayoutX());
        thumb.setLayoutY(thumbAreaY + (thumbAreaHeight - thumbHeight) / 2);

        // each time the layout is done, recompute the thumb "selected" position and apply it to the transition target
        final double thumbTarget = thumbAreaWidth - thumbWidth;
        transition.setToX(thumbTarget);

        if (transition.getStatus() == Animation.Status.RUNNING) {
            // if the transition is running, it must be restarted for the value to be properly updated
            final Duration currentTime = transition.getCurrentTime();
            transition.stop();
            transition.playFrom(currentTime);
        } else {
            // if the transition is not running, simply apply the translateX value
            thumb.setTranslateX(c.isSelected() ? thumbTarget : 0.0);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset,
                                     double bottomInset, double leftInset) {
        return leftInset + label.prefWidth(-1) + thumbArea.prefWidth(-1) + rightInset;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset,
                                      double bottomInset, double leftInset) {
        return topInset + Math.max(thumb.prefHeight(-1), label.prefHeight(-1)) + bottomInset;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset,
                                      double bottomInset, double leftInset) {
        return leftInset + label.prefWidth(-1) + 1 + thumbArea.prefWidth(-1) + rightInset;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset,
                                       double bottomInset, double leftInset) {
        return computeMinHeight(width, topInset, rightInset, bottomInset, leftInset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset,
                                     double bottomInset, double leftInset) {
        return getSkinnable().prefWidth(height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset,
                                      double bottomInset, double leftInset) {
        return getSkinnable().prefHeight(width);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return SwitchSkin.StyleableProperties.STYLEABLES;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Styleable Properties                                                  //
    ///////////////////////////////////////////////////////////////////////////

    static class StyleableProperties {

        private static final CssMetaData<Switch, Number> THUMB_MOVE_ANIMATION_TIME = new CssMetaData<>(
                "-fx-thumb-move-animation-time", SizeConverter.getInstance(), DEFAULT_ANIMATION_TIME.toMillis()
        ) {

            @Override
            public boolean isSettable(Switch aSwitch) {
                final var skin = (SwitchSkin) aSwitch.getSkin();
                return skin.thumbMoveAnimationTime == null || !skin.thumbMoveAnimationTime.isBound();
            }

            @Override
            @SuppressWarnings("RedundantCast")
            public StyleableProperty<Number> getStyleableProperty(Switch aSwitch) {
                final var skin = (SwitchSkin) aSwitch.getSkin();
                return (StyleableProperty<Number>) (WritableValue<Number>) skin.thumbMoveAnimationTimeProperty();
            }
        };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(
                    SkinBase.getClassCssMetaData()
            );
            styleables.add(THUMB_MOVE_ANIMATION_TIME);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }
}
