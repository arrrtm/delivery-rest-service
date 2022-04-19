package kg.banksystem.deliverybackend.dto.order.response.identification;

import lombok.Data;

@Data
public class IdentificationResponseDTO {
    private String message;
    private boolean identical;
    private Double confidence;
    private Long verificationId;
}