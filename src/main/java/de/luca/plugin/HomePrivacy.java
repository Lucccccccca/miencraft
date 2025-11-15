package de.luca.plugin;

public enum HomePrivacy {
    PRIVATE,
    FRIENDS,
    PUBLIC;

    public HomePrivacy next() {
        switch (this) {
            case PRIVATE:
                return FRIENDS;
            case FRIENDS:
                return PUBLIC;
            default:
                return PRIVATE;
        }
    }

    public String getDisplayName() {
        switch (this) {
            case PRIVATE:
                return "§cPrivat";
            case FRIENDS:
                return "§aFreunde";
            case PUBLIC:
                return "§bÖffentlich";
            default:
                return "§7Unbekannt";
        }
    }
}
