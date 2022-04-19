package kg.banksystem.deliverybackend.rest;

import kg.banksystem.deliverybackend.dto.order.request.OrderIdRequestDTO;
import kg.banksystem.deliverybackend.dto.order.request.OrderStatusRequestDTO;
import kg.banksystem.deliverybackend.dto.order.response.OrdersResponseDTO;
import kg.banksystem.deliverybackend.entity.Order;
import kg.banksystem.deliverybackend.entity.response.BaseResponse;
import kg.banksystem.deliverybackend.enums.RestStatus;
import kg.banksystem.deliverybackend.security.jwt.JwtTokenDecoder;
import kg.banksystem.deliverybackend.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/branch/")
public class BranchEmployeeRestController {

    private final OrderService orderService;
    private final JwtTokenDecoder jwtTokenDecoder;

    @Autowired
    public BranchEmployeeRestController(OrderService orderService, JwtTokenDecoder jwtTokenDecoder) {
        this.orderService = orderService;
        this.jwtTokenDecoder = jwtTokenDecoder;
    }

    @PostMapping("orders")
    public ResponseEntity<BaseResponse> getAllOrdersForBranch(@RequestHeader(name = "Authorization") String token,
                                                              @RequestBody OrderStatusRequestDTO orderStatusRequestDTO) {
        log.info("Request status: {} for get orders for user branch.", orderStatusRequestDTO.getRequestStatus());

        if (orderStatusRequestDTO.getRequestStatus() == null) {
            log.error("Incorrect request status!");
            return new ResponseEntity<>(new BaseResponse("Неверный статус запроса!", null, RestStatus.ERROR), HttpStatus.OK);
        }

        if (!orderStatusRequestDTO.getRequestStatus().equals("new") &&
                !orderStatusRequestDTO.getRequestStatus().equals("active") &&
                !orderStatusRequestDTO.getRequestStatus().equals("destroyed")) {
            log.error("Incorrect request status!");
            return new ResponseEntity<>(new BaseResponse("Неверный статус запроса!", null, RestStatus.ERROR), HttpStatus.OK);
        }

        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User request ID: {} for get orders for user branch.", tokenData.get("user_id"));
        List<Order> orders = orderService.getOrdersForBranch(new Long(tokenData.get("user_id")), orderStatusRequestDTO.getRequestStatus());

        if (orders == null) {
            log.error("Orders data for branch not found.");
            return new ResponseEntity<>(new BaseResponse("Заказы, отправленные в филиал, не найдены.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            List<OrdersResponseDTO> ordersResponseDTOS = new ArrayList<>();
            orders.forEach(order -> ordersResponseDTOS.add(OrdersResponseDTO.orders(order)));
            log.info("Orders all data for branch successfully found.");
            return new ResponseEntity<>(new BaseResponse("Заказы, отправленные в филиал, успешно найдены.", ordersResponseDTOS, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }

    @PostMapping("orders/change/ready_from_delivery")
    public ResponseEntity<BaseResponse> changeReadyFromDelivery(@RequestHeader(name = "Authorization") String token,
                                                                @RequestBody OrderIdRequestDTO orderIdRequestDTO) {
        log.info("Order ID for change status: {}", orderIdRequestDTO.getId());

        if (orderIdRequestDTO.getId() == null) {
            log.error("Order ID is empty.");
            return new ResponseEntity<>(new BaseResponse("Заказ по указанному идентификатору не найден.", false, RestStatus.ERROR), HttpStatus.OK);
        } else {
            boolean response = orderService.setReadyFromDeliveryStatusForOrder(orderIdRequestDTO.getId());
            if (!response) {
                log.error("Error changing order status to 'Ready for pickup'");
                return new ResponseEntity<>(new BaseResponse("Ошибка смены статуса заказа на 'Готов к выдаче'.", false, RestStatus.ERROR), HttpStatus.OK);
            } else {
                log.info("The order has been successfully processed. Order status changed to 'Ready for Pickup'.");
                return new ResponseEntity<>(new BaseResponse("Заказ успешно обработан. Статус заказа изменён на 'Готов к выдаче'.", true, RestStatus.SUCCESS), HttpStatus.OK);
            }
        }
    }

    @PostMapping("orders/change/destroyed")
    public ResponseEntity<BaseResponse> changeDestroyed(@RequestHeader(name = "Authorization") String token,
                                                        @RequestBody OrderIdRequestDTO orderIdRequestDTO) {
        log.info("Order ID for change status: {}", orderIdRequestDTO.getId());

        if (orderIdRequestDTO.getId() == null) {
            log.error("Order ID is empty.");
            return new ResponseEntity<>(new BaseResponse("Заказ по указанному идентификатору не найден.", false, RestStatus.ERROR), HttpStatus.OK);
        } else {
            boolean response = orderService.setDestroyedStatusForOrder(orderIdRequestDTO.getId());
            if (!response) {
                log.error("Error changing order status to 'Card destroyed'");
                return new ResponseEntity<>(new BaseResponse("Ошибка смены статуса заказа на 'Карта уничтожена'.", false, RestStatus.ERROR), HttpStatus.OK);
            } else {
                log.info("The order has been successfully processed. Order status changed to 'Card destroyed'.");
                return new ResponseEntity<>(new BaseResponse("Заказ успешно обработан. Статус заказа изменён на 'Карта уничтожена'.", true, RestStatus.SUCCESS), HttpStatus.OK);
            }
        }
    }
}