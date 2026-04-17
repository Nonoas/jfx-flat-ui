
package github.nonoas.jfx.flat.ui.theme;


public final class ClaudeTheme implements Theme {

    public ClaudeTheme() {
        // Default constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Claude";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return "/css/style-claude.css";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDarkMode() {
        return false;
    }
}
