package kg.banksystem.deliverybackend.dto.order.response;

import kg.banksystem.deliverybackend.dto.admin.response.BranchResponseDTO;
import kg.banksystem.deliverybackend.dto.bank.response.CardResponseDTO;
import kg.banksystem.deliverybackend.dto.bank.response.ClientResponseDTO;
import kg.banksystem.deliverybackend.dto.user.response.UserResponseDTO;
import kg.banksystem.deliverybackend.entity.OrderStoryEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderStoryDetailResponseDTO {
    private Long id;
    private String addressPickup;
    private String addressDelivery;
    private String status;
    private String comment;
    private Long orderNumber;
    private CardResponseDTO card;
    private ClientResponseDTO client;
    private BranchResponseDTO branch;
    private UserResponseDTO user;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public static OrderStoryDetailResponseDTO ordersStoryForDetail(OrderStoryEntity orderStoryEntity) {
        OrderStoryDetailResponseDTO orderStoryDetailResponseDTO = new OrderStoryDetailResponseDTO();
        orderStoryDetailResponseDTO.setId(orderStoryEntity.getId());
        orderStoryDetailResponseDTO.setAddressPickup(orderStoryEntity.getAddressPickup());
        orderStoryDetailResponseDTO.setAddressDelivery(orderStoryEntity.getAddressDelivery());
        orderStoryDetailResponseDTO.setStatus(orderStoryEntity.getStatus().getValue());
        orderStoryDetailResponseDTO.setComment(orderStoryEntity.getComment());
        orderStoryDetailResponseDTO.setOrderNumber(orderStoryEntity.getOrderNumber());
        orderStoryDetailResponseDTO.setCard(CardResponseDTO.cardData(orderStoryEntity.getCardEntity()));
        orderStoryDetailResponseDTO.setClient(ClientResponseDTO.clientData(orderStoryEntity.getClientEntity()));
        orderStoryDetailResponseDTO.setBranch(BranchResponseDTO.branchData(orderStoryEntity.getBranchEntity()));
        orderStoryDetailResponseDTO.setUser(UserResponseDTO.userPersonalAccount(orderStoryEntity.getUserEntity()));
        orderStoryDetailResponseDTO.setCreatedDate(orderStoryEntity.getCreatedDate());
        orderStoryDetailResponseDTO.setUpdatedDate(orderStoryEntity.getUpdatedDate());
        return orderStoryDetailResponseDTO;
    }
}