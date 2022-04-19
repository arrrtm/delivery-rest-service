package kg.banksystem.deliverybackend.dto.order.response;

import kg.banksystem.deliverybackend.entity.Client;
import lombok.Data;

@Data
public class ClientResponseDTO {
    private Long id;
    private String clientPin;
    private String clientFullName;
    private String clientPhoneNumber;

    public static ClientResponseDTO clientData(Client client) {
        ClientResponseDTO clientResponseDTO = new ClientResponseDTO();
        clientResponseDTO.setId(client.getId());
        clientResponseDTO.setClientPin(client.getClientPin());
        clientResponseDTO.setClientFullName(client.getClientFullName());
        clientResponseDTO.setClientPhoneNumber(client.getClientPhoneNumber());
        return clientResponseDTO;
    }
}