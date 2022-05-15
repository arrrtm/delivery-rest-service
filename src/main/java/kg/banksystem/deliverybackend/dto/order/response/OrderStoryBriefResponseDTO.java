package kg.banksystem.deliverybackend.dto.order.response;

import kg.banksystem.deliverybackend.entity.OrderStoryEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderStoryBriefResponseDTO {
    private Long id;
    private Long orderNumber;
    private LocalDateTime createdDate;

    public static OrderStoryBriefResponseDTO orderStoryBriefDTO(OrderStoryEntity orderStoryEntity) {
        OrderStoryBriefResponseDTO orderStoryBriefResponseDTO = new OrderStoryBriefResponseDTO();
        orderStoryBriefResponseDTO.setId(orderStoryEntity.getId());
        orderStoryBriefResponseDTO.setOrderNumber(orderStoryEntity.getOrderNumber());
        orderStoryBriefResponseDTO.setCreatedDate(orderStoryEntity.getCreatedDate());
        return orderStoryBriefResponseDTO;
    }
}