package kg.banksystem.deliverybackend.rest;

import kg.banksystem.deliverybackend.dto.order.request.OrderRequestDTO;
import kg.banksystem.deliverybackend.dto.order.response.OrderDetailResponseDTO;
import kg.banksystem.deliverybackend.dto.order.response.OrderStoryDetailResponseDTO;
import kg.banksystem.deliverybackend.dto.user.response.RoleResponseDTO;
import kg.banksystem.deliverybackend.entity.Order;
import kg.banksystem.deliverybackend.entity.OrderStory;
import kg.banksystem.deliverybackend.entity.Role;
import kg.banksystem.deliverybackend.entity.User;
import kg.banksystem.deliverybackend.entity.response.BaseResponse;
import kg.banksystem.deliverybackend.enums.RestStatus;
import kg.banksystem.deliverybackend.security.jwt.JwtTokenDecoder;
import kg.banksystem.deliverybackend.service.OrderService;
import kg.banksystem.deliverybackend.service.OrderStoryService;
import kg.banksystem.deliverybackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/all/")
public class GeneralRestController {

    private final JwtTokenDecoder jwtTokenDecoder;
    private final UserService userService;
    private final OrderService orderService;
    private final OrderStoryService orderStoryService;

    @Autowired
    public GeneralRestController(JwtTokenDecoder jwtTokenDecoder, UserService userService, OrderService orderService, OrderStoryService orderStoryService) {
        this.jwtTokenDecoder = jwtTokenDecoder;
        this.userService = userService;
        this.orderService = orderService;
        this.orderStoryService = orderStoryService;
    }

    @PostMapping("get/role")
    public ResponseEntity<BaseResponse> getRoleByToken(@RequestHeader(name = "Authorization") String token) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        User user = userService.findByUsername(tokenData.get("sub"));
        Role role = user.getRole();
        log.info("User Login: {} for get User Role.", tokenData.get("sub"));
        RoleResponseDTO roleData = RoleResponseDTO.roleData(role);
        if (roleData == null) {
            return new ResponseEntity<>(new BaseResponse("Роль пользователя не найдена.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new BaseResponse("Роль пользователя найдена.", roleData, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }

    @PostMapping("get/name")
    public ResponseEntity<BaseResponse> getNameByToken(@RequestHeader(name = "Authorization") String token) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        User user = userService.findByUsername(tokenData.get("sub"));
        log.info("User Login: {} for get Full Name.", tokenData.get("sub"));
        if (user.getUserFullName() == null) {
            return new ResponseEntity<>(new BaseResponse("Полное имя пользователя не найдено.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new BaseResponse("Полное имя пользователя найдено.", user.getUserFullName(), RestStatus.SUCCESS), HttpStatus.OK);
        }
    }

    @PostMapping("orders/detail")
    public ResponseEntity<BaseResponse> getAllOrdersDetail(@RequestHeader(name = "Authorization") String token, @RequestBody OrderRequestDTO orderRequestDTO) {
        log.info("Request order ID: {}", orderRequestDTO.getId());
        log.info("Request detail status: {}", orderRequestDTO.getRequestStatus());

        if (orderRequestDTO.getRequestStatus() == null) {
            log.error("Incorrect status!");
            return new ResponseEntity<>(new BaseResponse("Неверный статус запроса!", null, RestStatus.ERROR), HttpStatus.OK);
        }

        switch (orderRequestDTO.getRequestStatus()) {
            case "new_accepted":
                Order order = orderService.findById(orderRequestDTO.getId());
                if (order == null) {
                    log.error("Order with ID: {} not found.", orderRequestDTO.getId());
                    return new ResponseEntity<>(new BaseResponse("Заказ не найден.", null, RestStatus.ERROR), HttpStatus.OK);
                } else {
                    log.info("Order data for order with ID: {} successfully found.", orderRequestDTO.getId());
                    OrderDetailResponseDTO orderDetailResponseDTO = OrderDetailResponseDTO.ordersForDetail(order);
                    return new ResponseEntity<>(new BaseResponse("Заказ успешно найден.", orderDetailResponseDTO, RestStatus.SUCCESS), HttpStatus.OK);
                }
            case "story":
                OrderStory orderStory = orderStoryService.findById(orderRequestDTO.getId());
                if (orderStory == null) {
                    log.error("Order with ID: {} not found.", orderRequestDTO.getId());
                    return new ResponseEntity<>(new BaseResponse("История заказов не найдена.", null, RestStatus.ERROR), HttpStatus.OK);
                } else {
                    log.info("Order story for order with ID: {} successfully found.", orderRequestDTO.getId());
                    OrderStoryDetailResponseDTO orderStoryDetailResponseDTO = OrderStoryDetailResponseDTO.ordersStoryForDetail(orderStory);
                    return new ResponseEntity<>(new BaseResponse("История заказов успешно найдена.", orderStoryDetailResponseDTO, RestStatus.SUCCESS), HttpStatus.OK);
                }
            default:
                log.error("Incorrect status!");
                return new ResponseEntity<>(new BaseResponse("Неверный статус запроса!", null, RestStatus.ERROR), HttpStatus.OK);
        }
    }
}