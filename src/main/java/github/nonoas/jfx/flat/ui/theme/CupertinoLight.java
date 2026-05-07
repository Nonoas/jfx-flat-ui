/* SPDX-License-Identifier: MIT */

package github.nonoas.jfx.flat.ui.theme;

/**
 * A theme based on <a href="https://developer.apple.com/design/">IOS</a> color palette.
 */
public class CupertinoLight implements Theme {

    public CupertinoLight() {
        // Default constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Cupertino Light";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return "/css/theme/cupertino-light.css";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheetBSS() {
        return "/css/theme/cupertino-light.bss";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDarkMode() {
        return false;
    }
}
