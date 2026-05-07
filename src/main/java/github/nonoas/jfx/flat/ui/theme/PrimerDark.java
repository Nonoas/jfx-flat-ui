/* SPDX-License-Identifier: MIT */

package github.nonoas.jfx.flat.ui.theme;

/**
 * A theme based on <a href="https://primer.style/">Github Primer</a> color palette.
 */
public final class PrimerDark implements Theme {

    public PrimerDark() {
        // Default constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Primer Dark";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return "/css/theme/primer-dark.css";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheetBSS() {
        return "/css/theme/primer-dark.bss";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDarkMode() {
        return true;
    }
}
