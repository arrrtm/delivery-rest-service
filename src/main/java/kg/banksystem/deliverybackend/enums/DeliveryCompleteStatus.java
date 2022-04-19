package kg.banksystem.deliverybackend.enums;

public enum DeliveryCompleteStatus {
    IDENTIFICATION_CLIENT_ERROR("Ошибка идентификации\nклиента"),
    UNABLE_TO_FIND_CLIENT("Клиент не найден"),
    SUCCESSFUL_DELIVERY("Успешная доставка карты\nи передача клиенту");

    private final String value;

    DeliveryCompleteStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}