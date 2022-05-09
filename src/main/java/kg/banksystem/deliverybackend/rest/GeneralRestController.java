package kg.banksystem.deliverybackend.rest;

import kg.banksystem.deliverybackend.dto.admin.response.BranchStatisticResponseDTO;
import kg.banksystem.deliverybackend.dto.order.request.OrderRequestDTO;
import kg.banksystem.deliverybackend.dto.order.response.OrderDetailResponseDTO;
import kg.banksystem.deliverybackend.dto.order.response.OrderStoryDetailResponseDTO;
import kg.banksystem.deliverybackend.dto.user.response.RoleResponseDTO;
import kg.banksystem.deliverybackend.dto.user.response.UserResponseDTO;
import kg.banksystem.deliverybackend.entity.OrderEntity;
import kg.banksystem.deliverybackend.entity.OrderStoryEntity;
import kg.banksystem.deliverybackend.entity.RoleEntity;
import kg.banksystem.deliverybackend.entity.UserEntity;
import kg.banksystem.deliverybackend.entity.response.BaseResponse;
import kg.banksystem.deliverybackend.entity.response.PaginationResponse;
import kg.banksystem.deliverybackend.enums.RestStatus;
import kg.banksystem.deliverybackend.security.jwt.JwtTokenDecoder;
import kg.banksystem.deliverybackend.service.BranchService;
import kg.banksystem.deliverybackend.service.OrderService;
import kg.banksystem.deliverybackend.service.OrderStoryService;
import kg.banksystem.deliverybackend.service.UserService;
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
@RequestMapping("/api/all/")
public class GeneralRestController {

    private final JwtTokenDecoder jwtTokenDecoder;
    private final UserService userService;
    private final OrderService orderService;
    private final OrderStoryService orderStoryService;
    private final BranchService branchService;

    @Autowired
    public GeneralRestController(JwtTokenDecoder jwtTokenDecoder, UserService userService, OrderService orderService, OrderStoryService orderStoryService, BranchService branchService) {
        this.jwtTokenDecoder = jwtTokenDecoder;
        this.userService = userService;
        this.orderService = orderService;
        this.orderStoryService = orderStoryService;
        this.branchService = branchService;
    }

    // DONE
    @PostMapping("get/role")
    public ResponseEntity<BaseResponse> getRoleByToken(@RequestHeader(name = "Authorization") String token) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        UserEntity userEntity = userService.findByUsername(tokenData.get("sub"));
        RoleEntity roleEntity = userEntity.getRoleEntity();
        RoleResponseDTO roleData = RoleResponseDTO.roleData(roleEntity);
        if (roleData == null) {
            return new ResponseEntity<>(new BaseResponse("Роль пользователя не найдена.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new BaseResponse("Роль пользователя найдена.", roleData, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }

    // DONE
    @PostMapping("get/name")
    public ResponseEntity<BaseResponse> getNameByToken(@RequestHeader(name = "Authorization") String token) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        UserEntity userEntity = userService.findByUsername(tokenData.get("sub"));
        if (userEntity.getUserFullName() == null) {
            return new ResponseEntity<>(new BaseResponse("Полное имя пользователя не найдено.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new BaseResponse("Полное имя пользователя найдено.", userEntity.getUserFullName(), RestStatus.SUCCESS), HttpStatus.OK);
        }
    }

    // DONE
    @PostMapping("orders")
    public ResponseEntity<PaginationResponse> getAllOrders(@RequestHeader(name = "Authorization") String token, int page, Long orderNumber, String branchName) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response get all Orders.", tokenData.get("sub"));
        List<OrderEntity> orderEntities = orderService.getAllActiveOrders(page, orderNumber, branchName);
        if (orderEntities == null) {
            log.error("Order data not found.");
            return new ResponseEntity<>(new PaginationResponse(("Заказ но номеру: " + orderNumber + " не найден."), null, RestStatus.ERROR, orderService.orderPageCalculation(new Long(tokenData.get("user_id")), "", page)), HttpStatus.OK);
        }
        if (orderEntities.isEmpty()) {
            log.error("Orders data not found.");
            return new ResponseEntity<>(new PaginationResponse("Заказы не найдены.", null, RestStatus.ERROR, orderService.orderPageCalculation(new Long(tokenData.get("user_id")), "", page)), HttpStatus.OK);
        } else {
            List<OrderDetailResponseDTO> ordersResponseDTOS = new ArrayList<>();
            orderEntities.forEach(order -> ordersResponseDTOS.add(OrderDetailResponseDTO.ordersForDetail(order)));
            log.info("Orders all data successfully found.");
            if (branchName != null) {
                return new ResponseEntity<>(new PaginationResponse("Заказы успешно найдены.", ordersResponseDTOS, RestStatus.SUCCESS, orderService.orderWithBranchPageCalculation(page, branchName)), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new PaginationResponse("Заказы успешно найдены.", ordersResponseDTOS, RestStatus.SUCCESS, orderService.orderPageCalculation(new Long(tokenData.get("user_id")), "", page)), HttpStatus.OK);
            }
        }
    }

    // ALMOST DONE
    @PostMapping("story")
    public ResponseEntity<PaginationResponse> getAllOrdersStory(@RequestHeader(name = "Authorization") String token, int page, Long orderNumber, String branchName, Long courierId) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response get all Orders Story.", tokenData.get("sub"));
        List<OrderStoryEntity> orderStoryEntities = orderStoryService.getAllOrderStory(page, orderNumber, branchName, courierId);
        if (orderStoryEntities == null) {
            log.error("Story data not found.");
            return new ResponseEntity<>(new PaginationResponse(("История заказа но номеру: " + orderNumber + " не найдена."), null, RestStatus.ERROR, orderStoryService.orderStoryPageCalculation(page, branchName, courierId)), HttpStatus.OK);
        }
        if (orderStoryEntities.isEmpty()) {
            log.error("Story data not found.");
            return new ResponseEntity<>(new PaginationResponse("Историй заказов не найдено.", null, RestStatus.ERROR, orderStoryService.orderStoryPageCalculation(page, branchName, courierId)), HttpStatus.OK);
        } else {
            try {
                List<OrderStoryDetailResponseDTO> ordersResponseDTOS = new ArrayList<>();
                orderStoryEntities.forEach(order -> ordersResponseDTOS.add(OrderStoryDetailResponseDTO.ordersStoryForDetail(order)));
                log.info("Story all data successfully found.");
                return new ResponseEntity<>(new PaginationResponse("Истории заказов успешно найдены.", ordersResponseDTOS, RestStatus.SUCCESS, orderStoryService.orderStoryPageCalculation(page, branchName, courierId)), HttpStatus.OK);
            } catch (NullPointerException npe) {
                return new ResponseEntity<>(new PaginationResponse(("История заказа но номеру: " + orderNumber + " не найдена."), null, RestStatus.ERROR, orderStoryService.orderStoryPageCalculation(page, branchName, courierId)), HttpStatus.OK);
            }
        }
    }

    // DONE
    @PostMapping("orders/detail")
    public ResponseEntity<BaseResponse> getOrderById(@RequestHeader(name = "Authorization") String token, @RequestBody OrderRequestDTO orderRequestDTO) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response for get Orders Detail.", tokenData.get("sub"));
        log.info("Request Order Id: {}.", orderRequestDTO.getId());
        log.info("Request detail status: {}.", orderRequestDTO.getRequestStatus());
        if (orderRequestDTO.getRequestStatus() == null) {
            log.error("Incorrect status!");
            return new ResponseEntity<>(new BaseResponse("Неверный статус запроса!", null, RestStatus.ERROR), HttpStatus.OK);
        }
        switch (orderRequestDTO.getRequestStatus()) {
            case "new_accepted":
                OrderEntity orderEntity = orderService.findOrderById(orderRequestDTO.getId());
                if (orderEntity == null) {
                    log.error("Order with Id: {} not found.", orderRequestDTO.getId());
                    return new ResponseEntity<>(new BaseResponse("Заказ не найден.", null, RestStatus.ERROR), HttpStatus.OK);
                } else {
                    log.info("Order data for Order with Id: {} successfully found.", orderRequestDTO.getId());
                    OrderDetailResponseDTO orderDetailResponseDTO = OrderDetailResponseDTO.ordersForDetail(orderEntity);
                    return new ResponseEntity<>(new BaseResponse("Заказ успешно найден.", orderDetailResponseDTO, RestStatus.SUCCESS), HttpStatus.OK);
                }
            case "story":
                OrderStoryEntity orderStoryEntity = orderStoryService.findOrderStoryById(orderRequestDTO.getId());
                if (orderStoryEntity == null) {
                    log.error("Order Story with Id: {} not found.", orderRequestDTO.getId());
                    return new ResponseEntity<>(new BaseResponse("История заказов не найдена.", null, RestStatus.ERROR), HttpStatus.OK);
                } else {
                    log.info("Order Story data for Order with Id: {} successfully found.", orderRequestDTO.getId());
                    OrderStoryDetailResponseDTO orderStoryDetailResponseDTO = OrderStoryDetailResponseDTO.ordersStoryForDetail(orderStoryEntity);
                    return new ResponseEntity<>(new BaseResponse("История заказов успешно найдена.", orderStoryDetailResponseDTO, RestStatus.SUCCESS), HttpStatus.OK);
                }
            default:
                log.error("Incorrect status!");
                return new ResponseEntity<>(new BaseResponse("Неверный статус запроса!", null, RestStatus.ERROR), HttpStatus.OK);
        }
    }

    // DONE
    @PostMapping("branches")
    public ResponseEntity<BaseResponse> getBranchNames(@RequestHeader(name = "Authorization") String token) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response for get Branch Names.", tokenData.get("sub"));
        List<String> branchNames = branchService.getBranchNames();
        if (branchNames.isEmpty()) {
            log.error("Branches data not found.");
            return new ResponseEntity<>(new BaseResponse("Филиалы не найдены.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            log.info("Branches data successfully found!");
            return new ResponseEntity<>(new BaseResponse("Наименования филиалов успешно найдены!", branchNames, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }

    // DONE
    @PostMapping("couriers")
    public ResponseEntity<BaseResponse> getCouriers(@RequestHeader(name = "Authorization") String token) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response for get Couriers.", tokenData.get("sub"));
        List<UserEntity> couriers = userService.getCouriers();
        if (couriers.isEmpty()) {
            log.error("Couriers data not found.");
            return new ResponseEntity<>(new BaseResponse("Курьеры не найдены.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            List<UserResponseDTO> userResponseDTOS = new ArrayList<>();
            couriers.forEach(user -> userResponseDTOS.add(UserResponseDTO.userPersonalAccount(user)));
            log.info("Couriers data successfully found!");
            return new ResponseEntity<>(new BaseResponse("Курьеры успешно найдены!", userResponseDTOS, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }

    // DONE
    @PostMapping("statistic")
    public ResponseEntity<BaseResponse> getBranchStatistic(@RequestHeader(name = "Authorization") String token) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response for get Branch Statistic.", tokenData.get("sub"));
        List<Map<String, Object>> statisticsList = branchService.getStatistics();
        if (statisticsList == null) {
            log.error("Statistics data not found.");
            return new ResponseEntity<>(new BaseResponse("Статистика по филиалам отсутствует.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            List<BranchStatisticResponseDTO> statisticResponseDTOS = new ArrayList<>();
            statisticsList.forEach(statistics -> statisticResponseDTOS.add(BranchStatisticResponseDTO.statisticsData(statistics)));
            log.info("Statistics data successfully found!");
            return new ResponseEntity<>(new BaseResponse("Статистика по филиалам успешно найдена!", statisticResponseDTOS, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }
}