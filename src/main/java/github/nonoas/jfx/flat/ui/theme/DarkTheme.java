
package github.nonoas.jfx.flat.ui.theme;

/**
 * A theme based on <a href="https://primer.style/">Github Primer</a> color palette.
 */
public final class DarkTheme implements Theme {

    public DarkTheme() {
        // Default constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Dark";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return "/css/style-light.css";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDarkMode() {
        return true;
    }
}
