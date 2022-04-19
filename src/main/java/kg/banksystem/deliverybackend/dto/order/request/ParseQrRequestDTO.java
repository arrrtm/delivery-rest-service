package kg.banksystem.deliverybackend.dto.order.request;

import lombok.Data;

@Data
public class ParseQrRequestDTO {
    private Long order_id;
    private Long user_id;
}