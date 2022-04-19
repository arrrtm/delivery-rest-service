package kg.banksystem.deliverybackend.dto.order.request;

import lombok.Data;

@Data
public class OrderRequestDTO {
    private Long id;
    private String requestStatus;
    private String comment;
}