package kg.banksystem.deliverybackend.enums;

public enum DeliveryType {
    PICKUP("Самовывоз"),
    COURIER_DELIVERY("Доставка курьером");

    private final String value;

    DeliveryType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}