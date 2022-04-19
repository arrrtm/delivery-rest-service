package kg.banksystem.deliverybackend.dto.order.response;

import kg.banksystem.deliverybackend.entity.OrderStory;
import lombok.Data;

import java.util.Date;

@Data
public class OrderStoryBriefResponseDTO {
    private Long id;
    private Date created;

    public static OrderStoryBriefResponseDTO orderStoryBriefDTO(OrderStory orderStory) {
        OrderStoryBriefResponseDTO orderStoryBriefResponseDTO = new OrderStoryBriefResponseDTO();
        orderStoryBriefResponseDTO.setId(orderStory.getId());
        orderStoryBriefResponseDTO.setCreated(orderStory.getCreated());
        return orderStoryBriefResponseDTO;
    }
}