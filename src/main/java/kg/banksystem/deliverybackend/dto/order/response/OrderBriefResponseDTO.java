package kg.banksystem.deliverybackend.dto.order.response;

import kg.banksystem.deliverybackend.entity.OrderEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderBriefResponseDTO {
    private Long id;
    private LocalDateTime createdDate;

    public static OrderBriefResponseDTO orderBriefDTO(OrderEntity orderEntity) {
        OrderBriefResponseDTO orderBriefResponseDTO = new OrderBriefResponseDTO();
        orderBriefResponseDTO.setId(orderEntity.getId());
        orderBriefResponseDTO.setCreatedDate(orderEntity.getCreatedDate());
        return orderBriefResponseDTO;
    }
}