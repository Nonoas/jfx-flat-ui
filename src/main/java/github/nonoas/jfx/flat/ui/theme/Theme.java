package github.nonoas.jfx.flat.ui.theme;


import static javafx.application.Application.STYLESHEET_CASPIAN;
import static javafx.application.Application.STYLESHEET_MODENA;

import javafx.application.Application;

/**
 * @author Nonoas
 * @date 2024/5/19
 * @since
 */
public interface Theme {

    /**
     * Returns theme name.
     */
    String getName();

    /**
     * Returns the path to the theme user-agent stylesheet.
     * See {@link Application#setUserAgentStylesheet(String)} for more info.
     */
    String getUserAgentStylesheet();


    /**
     * Signifies whether the theme uses a light font on a dark background
     * or vise versa.
     */
    boolean isDarkMode();

    /**
     * A simple factory method for instantiating a new theme.
     */
    static Theme of(final String name, final String userAgentStylesheet, final boolean darkMode) {
        if (name == null) {
            throw new NullPointerException("Name cannot be null!");
        }
        if (userAgentStylesheet == null) {
            throw new NullPointerException("User agent stylesheet cannot be null!");
        }

        return new Theme() {

            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getUserAgentStylesheet() {
                return userAgentStylesheet;
            }

            @Override
            public boolean isDarkMode() {
                return darkMode;
            }
        };
    }

    /**
     * Returns whether the theme is a standard theme provided by the OpenJFX or a custom theme.
     */
    default boolean isDefault() {
        return STYLESHEET_MODENA.equals(getUserAgentStylesheet())
                || STYLESHEET_CASPIAN.equals(getUserAgentStylesheet());
    }
}
