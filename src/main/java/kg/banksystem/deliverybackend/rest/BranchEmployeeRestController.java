package kg.banksystem.deliverybackend.rest;

import kg.banksystem.deliverybackend.dto.order.request.OrderIdRequestDTO;
import kg.banksystem.deliverybackend.dto.order.request.OrderStatusRequestDTO;
import kg.banksystem.deliverybackend.dto.order.response.OrderDetailResponseDTO;
import kg.banksystem.deliverybackend.entity.OrderEntity;
import kg.banksystem.deliverybackend.entity.response.BaseResponse;
import kg.banksystem.deliverybackend.entity.response.PaginationResponse;
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

    // DONE
    @PostMapping("orders")
    public ResponseEntity<PaginationResponse> getAllOrdersForBranch(@RequestHeader(name = "Authorization") String token, @RequestBody OrderStatusRequestDTO orderStatusRequestDTO, int page) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response get all Orders for Branch.", tokenData.get("sub"));
        log.info("Request status: {}.", orderStatusRequestDTO.getRequestStatus());
        if (orderStatusRequestDTO.getRequestStatus() == null) {
            log.error("Incorrect request status!");
            return new ResponseEntity<>(new PaginationResponse("Неверный статус запроса!", null, RestStatus.ERROR, 0), HttpStatus.OK);
        }
        if (!orderStatusRequestDTO.getRequestStatus().equals("new") &&
                !orderStatusRequestDTO.getRequestStatus().equals("active") &&
                !orderStatusRequestDTO.getRequestStatus().equals("destroyed")) {
            log.error("Incorrect request status!");
            return new ResponseEntity<>(new PaginationResponse("Неверный статус запроса!", null, RestStatus.ERROR, 0), HttpStatus.OK);
        }
        List<OrderEntity> orderEntityEntities = orderService.getOrdersForBranch(new Long(tokenData.get("user_id")), orderStatusRequestDTO.getRequestStatus(), page);
        if (orderEntityEntities == null) {
            log.error("Orders data for Branch not found.");
            return new ResponseEntity<>(new PaginationResponse("Заказы, отправленные в филиал, не найдены.", null, RestStatus.ERROR, 0), HttpStatus.OK);
        } else {
            List<OrderDetailResponseDTO> ordersResponseDTOS = new ArrayList<>();
            orderEntityEntities.forEach(order -> ordersResponseDTOS.add(OrderDetailResponseDTO.ordersForDetail(order)));
            log.info("Orders all data for Branch successfully found.");
            return new ResponseEntity<>(new PaginationResponse("Заказы, отправленные в филиал, успешно найдены.", ordersResponseDTOS, RestStatus.SUCCESS, orderService.orderPageCalculation(new Long(tokenData.get("user_id")), orderStatusRequestDTO.getRequestStatus(), page)), HttpStatus.OK);
        }
    }

    // DONE
    @PostMapping("orders/change/ready-from-delivery")
    public ResponseEntity<BaseResponse> changeReadyFromDelivery(@RequestHeader(name = "Authorization") String token, @RequestBody OrderIdRequestDTO orderIdRequestDTO) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response for change Order status.", tokenData.get("sub"));
        log.info("Order Id for change status: {}.", orderIdRequestDTO.getId());
        if (orderIdRequestDTO.getId() == null) {
            log.error("Order Id is empty.");
            return new ResponseEntity<>(new BaseResponse("Заказ по указанному идентификатору не найден.", false, RestStatus.ERROR), HttpStatus.OK);
        } else {
            boolean response = orderService.setReadyFromDeliveryStatusForOrder(orderIdRequestDTO.getId());
            if (!response) {
                log.error("Error changing order status to 'Ready From Delivery'.");
                return new ResponseEntity<>(new BaseResponse("Ошибка смены статуса заказа на 'Готов к выдаче'.", false, RestStatus.ERROR), HttpStatus.OK);
            } else {
                log.info("The order has been successfully processed. Order status changed to 'Ready From Delivery'.");
                return new ResponseEntity<>(new BaseResponse("Заказ успешно обработан. Статус заказа изменён на 'Готов к выдаче'.", true, RestStatus.SUCCESS), HttpStatus.OK);
            }
        }
    }

    // DONE
    @PostMapping("orders/change/destroyed")
    public ResponseEntity<BaseResponse> changeDestroyed(@RequestHeader(name = "Authorization") String token, @RequestBody OrderIdRequestDTO orderIdRequestDTO) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response for change Order status.", tokenData.get("sub"));
        log.info("Order Id for change status: {}.", orderIdRequestDTO.getId());
        if (orderIdRequestDTO.getId() == null) {
            log.error("Order Id is empty.");
            return new ResponseEntity<>(new BaseResponse("Заказ по указанному идентификатору не найден.", false, RestStatus.ERROR), HttpStatus.OK);
        } else {
            boolean response = orderService.setDestroyedStatusForOrder(orderIdRequestDTO.getId());
            if (!response) {
                log.error("Error changing order status to 'Destroyed'.");
                return new ResponseEntity<>(new BaseResponse("Ошибка смены статуса заказа на 'Карта уничтожена'.", false, RestStatus.ERROR), HttpStatus.OK);
            } else {
                log.info("The order has been successfully processed. Order status changed to 'Destroyed'.");
                return new ResponseEntity<>(new BaseResponse("Заказ успешно обработан. Статус заказа изменён на 'Карта уничтожена'.", true, RestStatus.SUCCESS), HttpStatus.OK);
            }
        }
    }

    // IN PROGRESS
    @PostMapping("orders/qr")
    public ResponseEntity<BaseResponse> getQrForOrder(@RequestHeader(name = "Authorization") String token, @RequestBody OrderIdRequestDTO orderIdRequestDTO) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response for get QR for order.", tokenData.get("sub"));
        log.info("Order Id for get QR: {}.", orderIdRequestDTO.getId());
        return null;
    }
}