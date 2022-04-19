package kg.banksystem.deliverybackend.dto.order.response;

import kg.banksystem.deliverybackend.entity.OrderStory;
import lombok.Data;

import java.util.Date;

@Data
public class OrderStoryDetailResponseDTO {
    private Date create;
    private String addressPickup;
    private String addressDelivery;
    private ClientResponseDTO client;
    private String status;
    private BranchResponseDTO branch;
    private CardResponseDTO card;

    public static OrderStoryDetailResponseDTO ordersStoryForDetail(OrderStory orderStory) {
        OrderStoryDetailResponseDTO orderStoryDetailResponseDTO = new OrderStoryDetailResponseDTO();
        orderStoryDetailResponseDTO.setCreate(orderStory.getCreated());
        orderStoryDetailResponseDTO.setAddressPickup(orderStory.getAddressPickup());
        orderStoryDetailResponseDTO.setAddressDelivery(orderStory.getAddressDelivery());
        orderStoryDetailResponseDTO.setClient(ClientResponseDTO.clientData(orderStory.getClient()));
        orderStoryDetailResponseDTO.setStatus(orderStory.getStatus().getValue());
        orderStoryDetailResponseDTO.setBranch(BranchResponseDTO.branchData(orderStory.getBranch()));
        orderStoryDetailResponseDTO.setCard(CardResponseDTO.cardData(orderStory.getCard()));
        return orderStoryDetailResponseDTO;
    }
}