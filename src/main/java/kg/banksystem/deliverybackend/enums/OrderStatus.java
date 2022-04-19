package kg.banksystem.deliverybackend.enums;

public enum OrderStatus {
    NEW_ORDER("Новый заказ"),
    IN_PROCESS("В обработке"),

    SENT_TO_FILIAL("Отправлен в филиал"),
    READY_FROM_DELIVERY("Готов к выдаче"),
    TAKEN_BY_COURIER("Заказ взят курьером"),
    HANDED_OVER_TO_THE_COURIER("Заказ передан курьеру"),
    RECEIVED_BY_CLIENT("Получен клиентом"),
    DESTROYED("Карта уничтожена");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}