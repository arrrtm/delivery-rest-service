package kg.banksystem.deliverybackend.dto.order.response;

import kg.banksystem.deliverybackend.entity.Order;
import lombok.Data;

import java.util.Date;

@Data
public class OrderBriefResponseDTO {
    private Long id;
    private Date created;

    public static OrderBriefResponseDTO orderBriefDTO(Order order) {
        OrderBriefResponseDTO orderBriefResponseDTO = new OrderBriefResponseDTO();
        orderBriefResponseDTO.setId(order.getId());
        orderBriefResponseDTO.setCreated(order.getCreated());
        return orderBriefResponseDTO;
    }
}