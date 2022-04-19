package kg.banksystem.deliverybackend.dto.order.response.identification;

import lombok.Data;

@Data
public class IdentificationResponseMessageDTO {
    private String status;
    private String message;
    private IdentificationResponseDTO data;
}