package kg.banksystem.deliverybackend.rest;

import kg.banksystem.deliverybackend.dto.admin.response.BranchCourierResponseDTO;
import kg.banksystem.deliverybackend.dto.admin.response.BranchStatisticResponseDTO;
import kg.banksystem.deliverybackend.dto.order.request.OrderIdRequestDTO;
import kg.banksystem.deliverybackend.dto.order.request.OrderStatusRequestDTO;
import kg.banksystem.deliverybackend.dto.order.response.OrderDetailResponseDTO;
import kg.banksystem.deliverybackend.dto.order.response.OrderStoryDetailResponseDTO;
import kg.banksystem.deliverybackend.dto.user.response.UserResponseDTO;
import kg.banksystem.deliverybackend.entity.BranchEntity;
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
    private final BranchService branchService;
    private final OrderStoryService orderStoryService;
    private final JwtTokenDecoder jwtTokenDecoder;

    @Autowired
    public BranchEmployeeRestController(OrderService orderService, BranchService branchService, OrderStoryService orderStoryService, JwtTokenDecoder jwtTokenDecoder) {
        this.orderService = orderService;
        this.branchService = branchService;
        this.orderStoryService = orderStoryService;
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
            return new ResponseEntity<>(new PaginationResponse("Заказы для филиала не найдены.", null, RestStatus.ERROR, 0), HttpStatus.OK);
        } else {
            List<OrderDetailResponseDTO> ordersResponseDTOS = new ArrayList<>();
            orderEntityEntities.forEach(order -> ordersResponseDTOS.add(OrderDetailResponseDTO.ordersForDetail(order)));
            log.info("Orders all data for Branch successfully found.");
            return new ResponseEntity<>(new PaginationResponse("Заказы для филиала успешно найдены.", ordersResponseDTOS, RestStatus.SUCCESS, orderService.orderPageCalculation(new Long(tokenData.get("user_id")), orderStatusRequestDTO.getRequestStatus(), page)), HttpStatus.OK);
        }
    }

    // DONE
    @PostMapping("story")
    public ResponseEntity<PaginationResponse> getAllOrdersStoryForBranch(@RequestHeader(name = "Authorization") String token, int page, Long orderNumber, Long courierId) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response get all Orders Story for Branch.", tokenData.get("sub"));
        List<OrderStoryEntity> orderStoryEntities = orderStoryService.getAllOrderStoryForBranch(new Long(tokenData.get("user_id")), page, orderNumber, courierId);
        if (orderStoryEntities == null) {
            log.error("Story data for Branch not found.");
            return new ResponseEntity<>(new PaginationResponse(("История заказа но номеру: " + orderNumber + " не найдена."), null, RestStatus.ERROR, orderStoryService.orderStoryPageCalculation(new Long(tokenData.get("user_id")), page, courierId)), HttpStatus.OK);
        }
        if (orderStoryEntities.isEmpty()) {
            log.error("Story data not found.");
            return new ResponseEntity<>(new PaginationResponse("Историй заказов для филиала не найдено.", null, RestStatus.ERROR, orderStoryService.orderStoryPageCalculation(new Long(tokenData.get("user_id")), page, courierId)), HttpStatus.OK);
        } else {
            try {
                List<OrderStoryDetailResponseDTO> ordersResponseDTOS = new ArrayList<>();
                orderStoryEntities.forEach(order -> ordersResponseDTOS.add(OrderStoryDetailResponseDTO.ordersStoryForDetail(order)));
                log.info("Story all data for Branch successfully found.");
                return new ResponseEntity<>(new PaginationResponse("Истории заказов для филиала успешно найдены.", ordersResponseDTOS, RestStatus.SUCCESS, orderStoryService.orderStoryPageCalculation(new Long(tokenData.get("user_id")), page, courierId)), HttpStatus.OK);
            } catch (NullPointerException npe) {
                return new ResponseEntity<>(new PaginationResponse(("История заказа но номеру: " + orderNumber + " не найдена."), null, RestStatus.ERROR, orderStoryService.orderStoryPageCalculation(new Long(tokenData.get("user_id")), page, courierId)), HttpStatus.OK);
            }
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

    // DONE
    @PostMapping("orders/qr")
    public ResponseEntity<BaseResponse> getQrForOrder(@RequestHeader(name = "Authorization") String token, @RequestBody OrderIdRequestDTO orderIdRequestDTO) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response for get QR for order.", tokenData.get("sub"));
        log.info("Order Id for get QR: {}.", orderIdRequestDTO.getId());
        if (orderIdRequestDTO.getId() == null) {
            log.error("Order Id is empty.");
            return new ResponseEntity<>(new BaseResponse("Для поиска QR кода необходимо передать номер заказа.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            String qrName = orderService.getQrUniqueName(orderIdRequestDTO.getId());
            if (qrName == null) {
                log.error("Order QR code not found.");
                return new ResponseEntity<>(new BaseResponse(("QR код для заказа с номером " + orderIdRequestDTO.getId() + " не найден!"), null, RestStatus.ERROR), HttpStatus.OK);
            } else {
                log.info("Order QR successfully found.");
                return new ResponseEntity<>(new BaseResponse(("QR код для заказа с номером " + orderIdRequestDTO.getId() + " успешно найден!"), qrName, RestStatus.SUCCESS), HttpStatus.OK);
            }
        }
    }

    // DONE
    @PostMapping("couriers")
    public ResponseEntity<BaseResponse> getCouriers(@RequestHeader(name = "Authorization") String token) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response for get Couriers.", tokenData.get("sub"));
        List<UserEntity> couriers = orderStoryService.getCouriersByBranch(new Long(tokenData.get("user_id")));
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
    public ResponseEntity<BaseResponse> getStatisticForBranch(@RequestHeader(name = "Authorization") String token) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response for get Branch Statistic.", tokenData.get("sub"));
        List<Map<String, Object>> statisticList = branchService.getStatisticByBranch(new Long(tokenData.get("user_id")));
        if (statisticList == null) {
            log.error("Statistic data for Branch not found.");
            return new ResponseEntity<>(new BaseResponse("Статистика по филиалу отсутствует.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            List<BranchStatisticResponseDTO> statisticResponseDTOS = new ArrayList<>();
            statisticList.forEach(statistics -> statisticResponseDTOS.add(BranchStatisticResponseDTO.statisticsData(statistics)));
            log.info("Statistic data for Branch successfully found!");
            return new ResponseEntity<>(new BaseResponse("Статистика по филиалу успешно найдена!", statisticResponseDTOS, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }

    // DONE
    @PostMapping("statistics/couriers")
    public ResponseEntity<BaseResponse> getStatisticForCouriers(@RequestHeader(name = "Authorization") String token) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response for get Courier Statistic.", tokenData.get("sub"));
        List<Map<String, Object>> courierStatisticList = branchService.getCourierStatisticByBranch(new Long(tokenData.get("user_id")));
        if (courierStatisticList == null) {
            log.error("Courier statistic data for Branch not found.");
            return new ResponseEntity<>(new BaseResponse("Статистика по курьерам филиала отсутствует.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            List<BranchCourierResponseDTO> courierStatisticResponseDTOS = new ArrayList<>();
            courierStatisticList.forEach(courier -> courierStatisticResponseDTOS.add(BranchCourierResponseDTO.courierData(courier)));
            log.info("Courier statistic data for Branch successfully found!");
            return new ResponseEntity<>(new BaseResponse("Статистика по курьерам филиала успешно найдена!", courierStatisticResponseDTOS, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }

    // DONE
    @PostMapping("get/branch")
    public ResponseEntity<BaseResponse> getBranchByToken(@RequestHeader(name = "Authorization") String token) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        BranchEntity branch = branchService.getBranchByUserId(new Long(tokenData.get("user_id")));
        if (branch.getName() == null) {
            return new ResponseEntity<>(new BaseResponse("Филиал пользователя не найден.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new BaseResponse("Филиал пользователя успешно найден.", branch.getName(), RestStatus.SUCCESS), HttpStatus.OK);
        }
    }
}