package kg.banksystem.deliverybackend.dto.order.response;

import kg.banksystem.deliverybackend.dto.admin.response.BranchResponseDTO;
import kg.banksystem.deliverybackend.dto.bank.response.CardResponseDTO;
import kg.banksystem.deliverybackend.dto.bank.response.ClientResponseDTO;
import kg.banksystem.deliverybackend.dto.user.response.UserResponseDTO;
import kg.banksystem.deliverybackend.entity.OrderEntity;
import kg.banksystem.deliverybackend.entity.UserEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class OrderDetailResponseDTO {
    private Long id;
    private String addressPickup;
    private String addressDelivery;
    private String typeDelivery;
    private String status;
    private CardResponseDTO card;
    private ClientResponseDTO client;
    private BranchResponseDTO branch;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Set<UserResponseDTO> users;

    public static OrderDetailResponseDTO ordersForDetail(OrderEntity orderEntity) {
        OrderDetailResponseDTO orderDetailResponseDTO = new OrderDetailResponseDTO();
        orderDetailResponseDTO.setId(orderEntity.getId());
        orderDetailResponseDTO.setAddressPickup(orderEntity.getAddressPickup());
        orderDetailResponseDTO.setAddressDelivery(orderEntity.getAddressDelivery());
        orderDetailResponseDTO.setTypeDelivery(orderEntity.getTypeDelivery().getValue());
        orderDetailResponseDTO.setStatus(orderEntity.getStatus().getValue());
        orderDetailResponseDTO.setCard(CardResponseDTO.cardData(orderEntity.getCardEntity()));
        orderDetailResponseDTO.setClient(ClientResponseDTO.clientData(orderEntity.getClientEntity()));
        orderDetailResponseDTO.setBranch(BranchResponseDTO.branchData(orderEntity.getBranchEntity()));
        orderDetailResponseDTO.setCreatedDate(orderEntity.getCreatedDate());
        orderDetailResponseDTO.setUpdatedDate(orderEntity.getUpdatedDate());
        Set<UserResponseDTO> userResponseDTOS = new HashSet<>();
        for (UserEntity userEntity : orderEntity.getUserEntities()) {
            userResponseDTOS.add(UserResponseDTO.userPersonalAccount(userEntity));
        }
        orderDetailResponseDTO.setUsers(userResponseDTOS);
        return orderDetailResponseDTO;
    }
}