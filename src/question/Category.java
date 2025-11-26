package question;

public enum Category {
    MATEMATIK("Matematik"),
    HISTORIA("Historia"),
    GEOGRAFI("Geografi"),
    SPORT("Sport"),
    KULTUR("Kultur"),
    VETENSKAP("Vetenskap");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}