package kg.banksystem.deliverybackend.dto.order.response;

import kg.banksystem.deliverybackend.dto.user.response.UserResponseDTO;
import kg.banksystem.deliverybackend.entity.Order;
import kg.banksystem.deliverybackend.entity.User;
import lombok.Data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
public class OrdersResponseDTO {
    private Long id;
    private String addressPickup;
    private String addressDelivery;
    private CardResponseDTO card;
    private BranchResponseDTO branch;
    private ClientResponseDTO client;
    private String status;
    private String typeDelivery;
    private Date created;
    private Date updated;
    private Set<UserResponseDTO> users;

    public static OrdersResponseDTO orders(Order order) {
        OrdersResponseDTO ordersResponseDTO = new OrdersResponseDTO();
        ordersResponseDTO.setId(order.getId());
        ordersResponseDTO.setAddressPickup(order.getAddressPickup());
        ordersResponseDTO.setAddressDelivery(order.getAddressDelivery());
        ordersResponseDTO.setCard(CardResponseDTO.cardData(order.getCard()));
        ordersResponseDTO.setBranch(BranchResponseDTO.branchData(order.getBranch()));
        ordersResponseDTO.setClient(ClientResponseDTO.clientData(order.getClient()));
        ordersResponseDTO.setStatus(order.getStatus().getValue());
        ordersResponseDTO.setTypeDelivery(order.getTypeDelivery().getValue());
        ordersResponseDTO.setCreated(order.getCreated());
        ordersResponseDTO.setUpdated(order.getUpdated());

        Set<UserResponseDTO> userResponseDTOS = new HashSet<>();
        for (User user : order.getUsers()) {
            userResponseDTOS.add(UserResponseDTO.userPersonalAccount(user));
        }
        ordersResponseDTO.setUsers(userResponseDTOS);
        return ordersResponseDTO;
    }
}