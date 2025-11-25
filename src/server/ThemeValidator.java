package server;

import java.util.Set;

public class ThemeValidator {
    private final Set<String> allowedThemes;
    public ThemeValidator(Set<String> allowedThemes) {
        this.allowedThemes = allowedThemes;
    }

    public boolean isValidTheme(String theme) {
        return allowedThemes.contains(theme);
    }
}
