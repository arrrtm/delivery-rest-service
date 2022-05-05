package kg.banksystem.deliverybackend.dto.order.response;

import kg.banksystem.deliverybackend.dto.admin.response.BranchResponseDTO;
import kg.banksystem.deliverybackend.dto.bank.response.CardResponseDTO;
import kg.banksystem.deliverybackend.dto.bank.response.ClientResponseDTO;
import kg.banksystem.deliverybackend.entity.OrderStoryEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderStoryDetailResponseDTO {
    private LocalDateTime createdDate;
    private String addressPickup;
    private String addressDelivery;
    private String status;
    private ClientResponseDTO client;
    private BranchResponseDTO branch;
    private CardResponseDTO card;

    public static OrderStoryDetailResponseDTO ordersStoryForDetail(OrderStoryEntity orderStoryEntity) {
        OrderStoryDetailResponseDTO orderStoryDetailResponseDTO = new OrderStoryDetailResponseDTO();
        orderStoryDetailResponseDTO.setCreatedDate(orderStoryEntity.getCreatedDate());
        orderStoryDetailResponseDTO.setAddressPickup(orderStoryEntity.getAddressPickup());
        orderStoryDetailResponseDTO.setAddressDelivery(orderStoryEntity.getAddressDelivery());
        orderStoryDetailResponseDTO.setStatus(orderStoryEntity.getStatus().getValue());
        orderStoryDetailResponseDTO.setClient(ClientResponseDTO.clientData(orderStoryEntity.getClientEntity()));
        orderStoryDetailResponseDTO.setBranch(BranchResponseDTO.branchData(orderStoryEntity.getBranchEntity()));
        orderStoryDetailResponseDTO.setCard(CardResponseDTO.cardData(orderStoryEntity.getCardEntity()));
        return orderStoryDetailResponseDTO;
    }
}