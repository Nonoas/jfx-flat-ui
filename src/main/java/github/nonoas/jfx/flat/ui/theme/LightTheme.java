package github.nonoas.jfx.flat.ui.theme;

/**
 * @author Nonoas
 * @date 2024/5/19
 * @since 1.0.1
 */
public class LightTheme implements Theme{
    @Override
    public String getName() {
        return "Light";
    }

    @Override
    public String getUserAgentStylesheet() {
        return "/css/style-light.css";
    }

    @Override
    public boolean isDarkMode() {
        return false;
    }
}
