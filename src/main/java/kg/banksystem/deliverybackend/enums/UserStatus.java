package kg.banksystem.deliverybackend.enums;

public enum UserStatus {
    ACTIVE("Активен"),
    BANNED("Забанен");

    private final String value;

    UserStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}