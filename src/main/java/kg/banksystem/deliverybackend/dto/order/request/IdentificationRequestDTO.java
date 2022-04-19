package kg.banksystem.deliverybackend.dto.order.request;

import lombok.Data;

@Data
public class IdentificationRequestDTO {
    private String pin;
    private Long external_id;
    private String msisdn;
    private String identify_type;
    private String photo;
}