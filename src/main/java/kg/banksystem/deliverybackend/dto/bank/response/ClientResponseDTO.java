package kg.banksystem.deliverybackend.dto.bank.response;

import kg.banksystem.deliverybackend.entity.ClientEntity;
import lombok.Data;

@Data
public class ClientResponseDTO {
    private Long id;
    private String clientPin;
    private String clientFullName;
    private String clientPhoneNumber;

    public static ClientResponseDTO clientData(ClientEntity clientEntity) {
        ClientResponseDTO clientResponseDTO = new ClientResponseDTO();
        clientResponseDTO.setId(clientEntity.getId());
        clientResponseDTO.setClientPin(clientEntity.getClientPin());
        clientResponseDTO.setClientFullName(clientEntity.getClientFullName());
        clientResponseDTO.setClientPhoneNumber(clientEntity.getClientPhoneNumber());
        return clientResponseDTO;
    }
}