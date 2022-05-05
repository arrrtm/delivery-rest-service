package kg.banksystem.deliverybackend.dto.bank.request;

import lombok.Data;

@Data
public class ClientRequestDTO {
    private Long id;
    private String clientPin;
    private String clientFullName;
    private String clientPhoneNumber;
}