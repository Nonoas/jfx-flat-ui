/* SPDX-License-Identifier: MIT */

package github.nonoas.jfx.flat.ui.theme;

/**
 * A theme based on <a href="https://developer.apple.com/design/">IOS</a> color palette.
 */
public class CupertinoDark implements Theme {

    public CupertinoDark() {
        // Default constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Cupertino Dark";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return "/css/theme/cupertino-dark.css";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheetBSS() {
        return "/css/theme/cupertino-dark.bss";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDarkMode() {
        return true;
    }
}
