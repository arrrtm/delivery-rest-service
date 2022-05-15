package kg.banksystem.deliverybackend.rest;

import kg.banksystem.deliverybackend.dto.admin.response.BranchReportResponseDTO;
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
    @PostMapping("report")
    public ResponseEntity<BaseResponse> getBranchReport(@RequestHeader(name = "Authorization") String token, String branchName, String period) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response for get Branch Report.", tokenData.get("sub"));
        log.info("Branch for report: {}.", branchName);
        log.info("Period for report: {}.", period);
        List<Map<String, Object>> reportList = branchService.getReport(branchName, period);
        if (reportList == null) {
            log.error("Report generation by Branch {} error.", branchName);
            return new ResponseEntity<>(new BaseResponse(("Ошибка формирования отчёта по филиалу " + branchName + "."), null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            List<BranchReportResponseDTO> reportResponseDTOS = new ArrayList<>();
            reportList.forEach(report -> reportResponseDTOS.add(BranchReportResponseDTO.reportData(report)));
            log.info("Report successfully generation and upload!");
            return new ResponseEntity<>(new BaseResponse(("Отчёт по филиалу " + branchName + " успешно загружен!"), reportResponseDTOS, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }
}