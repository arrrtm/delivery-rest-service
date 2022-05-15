package kg.banksystem.deliverybackend.rest;

import kg.banksystem.deliverybackend.dto.admin.response.BranchStatisticResponseDTO;
import kg.banksystem.deliverybackend.dto.order.response.OrderDetailResponseDTO;
import kg.banksystem.deliverybackend.dto.order.response.OrderStoryDetailResponseDTO;
import kg.banksystem.deliverybackend.dto.user.request.UserRequestDTO;
import kg.banksystem.deliverybackend.entity.OrderEntity;
import kg.banksystem.deliverybackend.entity.OrderStoryEntity;
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
@RequestMapping("/api/control/")
public class ControlRestController {

    private final UserService userService;
    private final OrderService orderService;
    private final OrderStoryService orderStoryService;
    private final BranchService branchService;
    private final JwtTokenDecoder jwtTokenDecoder;

    @Autowired
    public ControlRestController(UserService userService, OrderService orderService, OrderStoryService orderStoryService, BranchService branchService, JwtTokenDecoder jwtTokenDecoder) {
        this.userService = userService;
        this.orderService = orderService;
        this.orderStoryService = orderStoryService;
        this.branchService = branchService;
        this.jwtTokenDecoder = jwtTokenDecoder;
    }

    // DONE
    @PostMapping("user/block")
    public ResponseEntity<BaseResponse> blockUser(@RequestHeader(name = "Authorization") String token, @RequestBody UserRequestDTO userRequestDTO) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response to block User.", tokenData.get("sub"));
        log.info("User Id for blocking: {}.", userRequestDTO.getId());
        boolean lockResult = userService.blockUser(userRequestDTO.getId());
        UserEntity userEntity = userService.findUserById(userRequestDTO.getId());
        if (!lockResult) {
            log.error("User with Id {} already blocked.", userRequestDTO.getId());
            return new ResponseEntity<>(new BaseResponse("Пользователь " + userEntity.getUsername() + " уже заблокирован.", false, RestStatus.ERROR), HttpStatus.OK);
        } else {
            log.info("User with Id {} successfully blocked.", userRequestDTO.getId());
            return new ResponseEntity<>(new BaseResponse("Пользователь " + userEntity.getUsername() + " успешно заблокирован.", true, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }

    // DONE
    @PostMapping("user/unblock")
    public ResponseEntity<BaseResponse> unblockUser(@RequestHeader(name = "Authorization") String token, @RequestBody UserRequestDTO userRequestDTO) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response to unblock User.", tokenData.get("sub"));
        log.info("User Id for unblocking: {}.", userRequestDTO.getId());
        boolean unlockResult = userService.unblockUser(userRequestDTO.getId());
        UserEntity userEntity = userService.findUserById(userRequestDTO.getId());
        if (!unlockResult) {
            log.error("User with Id {} not blocked.", userRequestDTO.getId());
            return new ResponseEntity<>(new BaseResponse("Пользователь " + userEntity.getUsername() + " не заблокирован.", false, RestStatus.ERROR), HttpStatus.OK);
        } else {
            log.info("User with Id {} successfully unblocked.", userRequestDTO.getId());
            return new ResponseEntity<>(new BaseResponse("Пользователь " + userEntity.getUsername() + " успешно разблокирован.", true, RestStatus.SUCCESS), HttpStatus.OK);
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

    // DONE
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