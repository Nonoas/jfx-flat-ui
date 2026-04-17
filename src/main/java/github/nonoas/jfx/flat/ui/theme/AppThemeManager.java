package github.nonoas.jfx.flat.ui.theme;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;

public class AppThemeManager {

    private final List<Theme> themes;
    private final ObjectProperty<Theme> currentTheme;

    public AppThemeManager() {
        this.themes = Theme.builtIns();
        this.currentTheme = new SimpleObjectProperty<>(this, "currentTheme", themes.isEmpty() ? null : themes.getFirst());
        currentTheme.addListener((obs, oldTheme, newTheme) -> applyTheme(newTheme));
    }


    /**
     * Returns all available themes.
     */
    public List<Theme> availableThemes() {
        return themes;
    }

    /**
     * Returns the current theme.
     */
    public Theme currentTheme() {
        return currentTheme.get();
    }

    /**
     * Returns the read-only property representing the current theme.
     */
    public ReadOnlyObjectProperty<Theme> currentThemeProperty() {
        return currentTheme;
    }

    /**
     * Switches to the given theme.
     *
     * @param theme the theme to switch to; if {@code null} the call is ignored
     */
    public void switchTheme(Theme theme) {
        if (theme != null) {
            currentTheme.set(theme);
        }
    }

    private void applyTheme(Theme theme) {
        Application.setUserAgentStylesheet(theme.getUserAgentStylesheet());
    }
}
