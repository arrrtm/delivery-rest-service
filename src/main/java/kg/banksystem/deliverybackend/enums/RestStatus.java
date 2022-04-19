package kg.banksystem.deliverybackend.enums;

public enum RestStatus {
    SUCCESS("Успех"),
    ERROR("Ошибка");

    private final String value;

    RestStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}