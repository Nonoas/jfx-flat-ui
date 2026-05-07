/* SPDX-License-Identifier: MIT */

package github.nonoas.jfx.flat.ui.theme;

/**
 * A theme inspired by Claude Docs' soft neutral interface palette.
 */
public final class Claude implements Theme {

    public Claude() {
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
        return "/css/theme/claude.css";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheetBSS() {
        return "/css/theme/claude.bss";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDarkMode() {
        return false;
    }
}
