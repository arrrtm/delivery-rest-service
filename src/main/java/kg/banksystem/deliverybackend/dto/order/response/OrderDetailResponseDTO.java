package kg.banksystem.deliverybackend.dto.order.response;

import kg.banksystem.deliverybackend.dto.user.response.UserResponseDTO;
import kg.banksystem.deliverybackend.entity.Order;
import kg.banksystem.deliverybackend.entity.User;
import lombok.Data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
public class OrderDetailResponseDTO {
    private Long id;
    private String addressPickup;
    private String addressDelivery;
    private CardResponseDTO card;
    private ClientResponseDTO client;
    private String typeDelivery;
    private String status;
    private BranchResponseDTO branch;
    private Date created;
    private Date updated;
    private Set<UserResponseDTO> users;

    public static OrderDetailResponseDTO ordersForDetail(Order order) {
        OrderDetailResponseDTO orderDetailResponseDTO = new OrderDetailResponseDTO();
        orderDetailResponseDTO.setId(order.getId());
        orderDetailResponseDTO.setAddressPickup(order.getAddressPickup());
        orderDetailResponseDTO.setAddressDelivery(order.getAddressDelivery());
        orderDetailResponseDTO.setCard(CardResponseDTO.cardData(order.getCard()));
        orderDetailResponseDTO.setClient(ClientResponseDTO.clientData(order.getClient()));
        orderDetailResponseDTO.setTypeDelivery(order.getTypeDelivery().getValue());
        orderDetailResponseDTO.setStatus(order.getStatus().getValue());
        orderDetailResponseDTO.setBranch(BranchResponseDTO.branchData(order.getBranch()));
        orderDetailResponseDTO.setCreated(order.getCreated());
        orderDetailResponseDTO.setUpdated(order.getUpdated());

        Set<UserResponseDTO> userResponseDTOS = new HashSet<>();
        for (User user : order.getUsers()) {
            userResponseDTOS.add(UserResponseDTO.userPersonalAccount(user));
        }
        orderDetailResponseDTO.setUsers(userResponseDTOS);

        return orderDetailResponseDTO;
    }
}