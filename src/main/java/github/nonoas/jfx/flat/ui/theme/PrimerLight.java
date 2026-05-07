/* SPDX-License-Identifier: MIT */

package github.nonoas.jfx.flat.ui.theme;

/**
 * A theme based on <a href="https://primer.style/">Github Primer</a> color palette.
 */
public final class PrimerLight implements Theme {

    public PrimerLight() {
        // Default constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Primer Light";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return "/css/theme/primer-light.css";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheetBSS() {
        return "/css/theme/primer-light.bss";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDarkMode() {
        return false;
    }
}
