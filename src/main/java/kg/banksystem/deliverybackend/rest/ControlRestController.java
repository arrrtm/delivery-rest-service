package kg.banksystem.deliverybackend.rest;

import kg.banksystem.deliverybackend.dto.admin.response.BranchStatisticResponseDTO;
import kg.banksystem.deliverybackend.dto.bank.response.CardResponseDTO;
import kg.banksystem.deliverybackend.dto.bank.response.ClientResponseDTO;
import kg.banksystem.deliverybackend.dto.order.response.OrderDetailResponseDTO;
import kg.banksystem.deliverybackend.dto.order.response.OrderStoryDetailResponseDTO;
import kg.banksystem.deliverybackend.dto.user.request.UserRequestDTO;
import kg.banksystem.deliverybackend.dto.user.response.UserResponseDTO;
import kg.banksystem.deliverybackend.entity.*;
import kg.banksystem.deliverybackend.entity.response.BaseResponse;
import kg.banksystem.deliverybackend.entity.response.PaginationResponse;
import kg.banksystem.deliverybackend.enums.RestStatus;
import kg.banksystem.deliverybackend.security.jwt.JwtTokenDecoder;
import kg.banksystem.deliverybackend.service.*;
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
    private final ClientService clientService;
    private final JwtTokenDecoder jwtTokenDecoder;

    @Autowired
    public ControlRestController(UserService userService, OrderService orderService, OrderStoryService orderStoryService, BranchService branchService, ClientService clientService, JwtTokenDecoder jwtTokenDecoder) {
        this.userService = userService;
        this.orderService = orderService;
        this.orderStoryService = orderStoryService;
        this.branchService = branchService;
        this.clientService = clientService;
        this.jwtTokenDecoder = jwtTokenDecoder;
    }

    @PostMapping("user/block")
    public ResponseEntity<BaseResponse> blockUser(@RequestHeader(name = "Authorization") String token, @RequestBody UserRequestDTO userRequestDTO) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response to block User.", tokenData.get("sub"));
        log.info("User Id for blocking: {}.", userRequestDTO.getId());
        boolean lockResult = userService.blockUser(userRequestDTO.getId());
        UserEntity userEntity = userService.findUserById(userRequestDTO.getId());
        if (!lockResult) {
            log.error("User with Id {} already blocked.", userRequestDTO.getId());
            return new ResponseEntity<>(new BaseResponse("???????????????????????? " + userEntity.getUsername() + " ?????? ????????????????????????.", false, RestStatus.ERROR), HttpStatus.OK);
        } else {
            log.info("User with Id {} successfully blocked.", userRequestDTO.getId());
            return new ResponseEntity<>(new BaseResponse("???????????????????????? " + userEntity.getUsername() + " ?????????????? ????????????????????????.", true, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }

    @PostMapping("user/unblock")
    public ResponseEntity<BaseResponse> unblockUser(@RequestHeader(name = "Authorization") String token, @RequestBody UserRequestDTO userRequestDTO) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response to unblock User.", tokenData.get("sub"));
        log.info("User Id for unblocking: {}.", userRequestDTO.getId());
        boolean unlockResult = userService.unblockUser(userRequestDTO.getId());
        UserEntity userEntity = userService.findUserById(userRequestDTO.getId());
        if (!unlockResult) {
            log.error("User with Id {} not blocked.", userRequestDTO.getId());
            return new ResponseEntity<>(new BaseResponse("???????????????????????? " + userEntity.getUsername() + " ???? ????????????????????????.", false, RestStatus.ERROR), HttpStatus.OK);
        } else {
            log.info("User with Id {} successfully unblocked.", userRequestDTO.getId());
            return new ResponseEntity<>(new BaseResponse("???????????????????????? " + userEntity.getUsername() + " ?????????????? ??????????????????????????.", true, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }

    @PostMapping("users/detail")
    public ResponseEntity<BaseResponse> getUserById(@RequestHeader(name = "Authorization") String token, @RequestBody UserRequestDTO userRequestDTO) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response get User by Id.", tokenData.get("sub"));
        log.info("Request User Id: {}.", userRequestDTO.getId());
        UserEntity userEntity = userService.findUserById(userRequestDTO.getId());
        if (userEntity == null) {
            log.error("User data for User with Id: {} not found.", userRequestDTO.getId());
            return new ResponseEntity<>(new BaseResponse("???????????????????????? ???? ????????????.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            UserResponseDTO userResponseDTO = UserResponseDTO.userPersonalAccount(userEntity);
            log.info("User data for User with Id: {} successfully found.", userRequestDTO.getId());
            return new ResponseEntity<>(new BaseResponse("???????????????????????? ?????????????? ????????????.", userResponseDTO, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }

    @PostMapping("users/register")
    public ResponseEntity<BaseResponse> userRegister(@RequestHeader(name = "Authorization") String token, @RequestBody UserRequestDTO userRequestDTO) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response add User.", tokenData.get("sub"));
        if (userService.registerUser(userRequestDTO)) {
            log.info("User registered successfully!");
            return new ResponseEntity<>(new BaseResponse(("?????????? ???????????????????????? " + userRequestDTO.getUserFullName() + " ?????? ?????????????? ??????????????????????????????!"), null, RestStatus.SUCCESS), HttpStatus.OK);
        } else {
            log.error("User has not been registered.");
            return new ResponseEntity<>(new BaseResponse("?????????? ???????????????????????? ???? ?????? ??????????????????????????????, ???????????????? ???? ?????? ????????????????????. ?????????????????? ???????????? ?? ?????????????????? ?????????????? ?????? ??????!", null, RestStatus.ERROR), HttpStatus.OK);
        }
    }

    @PostMapping("users/update")
    public ResponseEntity<BaseResponse> userUpdate(@RequestHeader(name = "Authorization") String token, @RequestBody UserRequestDTO userRequestDTO) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response update User.", tokenData.get("sub"));
        log.info("Request User Id for updating: {}.", userRequestDTO.getId());
        if (userRequestDTO.getId() == null) {
            log.error("Request User Id can not be empty!");
            return new ResponseEntity<>(new BaseResponse("?????????????????????????? ???????????????????????? ???? ?????????? ???????? ????????????!", null, RestStatus.ERROR), HttpStatus.OK);
        }
        if (userService.updateUser(userRequestDTO)) {
            log.info("User updated successfully!");
            return new ResponseEntity<>(new BaseResponse(("???????????????????????? " + userRequestDTO.getUserFullName() + " ?????? ?????????????? ????????????????!"), null, RestStatus.SUCCESS), HttpStatus.OK);
        } else {
            log.error("User has not been updated.");
            return new ResponseEntity<>(new BaseResponse("???????????????????????? ???? ?????? ????????????????, ???????????????? ???? ?????? ????????????????????. ?????????????????? ???????????? ?? ?????????????????? ?????????????? ?????? ??????!", null, RestStatus.ERROR), HttpStatus.OK);
        }
    }

    @PostMapping("orders")
    public ResponseEntity<PaginationResponse> getAllOrders(@RequestHeader(name = "Authorization") String token, int page, Long order, Long branch) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response get all Orders.", tokenData.get("sub"));
        List<OrderEntity> orderEntities = orderService.getAllActiveOrders(page, order, branch);
        if (orderEntities == null) {
            log.error("Order data not found.");
            return new ResponseEntity<>(new PaginationResponse(("?????????? ???? ????????????: " + order + " ???? ????????????."), null, RestStatus.ERROR, orderService.orderPageCalculation(new Long(tokenData.get("user_id")), "", page)), HttpStatus.OK);
        }
        if (orderEntities.isEmpty()) {
            log.error("Orders data not found.");
            return new ResponseEntity<>(new PaginationResponse("???????????? ???? ??????????????.", null, RestStatus.ERROR, orderService.orderPageCalculation(new Long(tokenData.get("user_id")), "", page)), HttpStatus.OK);
        } else {
            List<OrderDetailResponseDTO> ordersResponseDTOS = new ArrayList<>();
            orderEntities.forEach(orders -> ordersResponseDTOS.add(OrderDetailResponseDTO.ordersForDetail(orders)));
            log.info("Orders all data successfully found.");
            if (branch != null) {
                return new ResponseEntity<>(new PaginationResponse("???????????? ?????????????? ??????????????.", ordersResponseDTOS, RestStatus.SUCCESS, orderService.orderWithBranchPageCalculation(page, branch)), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new PaginationResponse("???????????? ?????????????? ??????????????.", ordersResponseDTOS, RestStatus.SUCCESS, orderService.orderPageCalculation(new Long(tokenData.get("user_id")), "", page)), HttpStatus.OK);
            }
        }
    }

    @PostMapping("story")
    public ResponseEntity<PaginationResponse> getAllOrdersStory(@RequestHeader(name = "Authorization") String token, int page, Long order, Long branch, Long courier) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response get all Orders Story.", tokenData.get("sub"));
        List<OrderStoryEntity> orderStoryEntities = orderStoryService.getAllOrderStory(page, order, branch, courier);
        if (orderStoryEntities == null) {
            log.error("Story data not found.");
            return new ResponseEntity<>(new PaginationResponse(("?????????????? ???????????? ???? ????????????: " + order + " ???? ??????????????."), null, RestStatus.ERROR, orderStoryService.orderStoryPageCalculation(page, new Long(tokenData.get("user_id")), courier)), HttpStatus.OK);
        }
        if (orderStoryEntities.isEmpty()) {
            log.error("Story data not found.");
            return new ResponseEntity<>(new PaginationResponse("?????????????? ?????????????? ???? ??????????????.", null, RestStatus.ERROR, orderStoryService.orderStoryPageCalculation(page, new Long(tokenData.get("user_id")), courier)), HttpStatus.OK);
        } else {
            try {
                List<OrderStoryDetailResponseDTO> ordersResponseDTOS = new ArrayList<>();
                orderStoryEntities.forEach(orders -> ordersResponseDTOS.add(OrderStoryDetailResponseDTO.ordersStoryForDetail(orders)));
                log.info("Story all data successfully found.");
                return new ResponseEntity<>(new PaginationResponse("?????????????? ?????????????? ?????????????? ??????????????.", ordersResponseDTOS, RestStatus.SUCCESS, orderStoryService.orderStoryPageCalculation(page, new Long(tokenData.get("user_id")), courier)), HttpStatus.OK);
            } catch (NullPointerException npe) {
                return new ResponseEntity<>(new PaginationResponse(("?????????????? ???????????? ???? ????????????: " + order + " ???? ??????????????."), null, RestStatus.ERROR, orderStoryService.orderStoryPageCalculation(page, new Long(tokenData.get("user_id")), courier)), HttpStatus.OK);
            }
        }
    }

    @PostMapping("statistic")
    public ResponseEntity<BaseResponse> getBranchStatistic(@RequestHeader(name = "Authorization") String token) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response for get Branch Statistic.", tokenData.get("sub"));
        List<Map<String, Object>> statisticsList = branchService.getStatistics();
        if (statisticsList == null) {
            log.error("Statistics data not found.");
            return new ResponseEntity<>(new BaseResponse("???????????????????? ???? ???????????????? ??????????????????????.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            List<BranchStatisticResponseDTO> statisticResponseDTOS = new ArrayList<>();
            statisticsList.forEach(statistics -> statisticResponseDTOS.add(BranchStatisticResponseDTO.statisticsData(statistics)));
            log.info("Statistics data successfully found!");
            return new ResponseEntity<>(new BaseResponse("???????????????????? ???? ???????????????? ?????????????? ??????????????!", statisticResponseDTOS, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }

    @PostMapping("cards")
    public ResponseEntity<BaseResponse> getCards(@RequestHeader(name = "Authorization") String token) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response for get all Cards.", tokenData.get("sub"));
        List<CardEntity> cardEntities = orderService.getCards();
        if (cardEntities.isEmpty()) {
            log.error("Cards data not found.");
            return new ResponseEntity<>(new BaseResponse("?????????? ???? ??????????????.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            List<CardResponseDTO> cardResponseDTOS = new ArrayList<>();
            cardEntities.forEach(card -> cardResponseDTOS.add(CardResponseDTO.cardData(card)));
            log.info("Cards data successfully found!");
            return new ResponseEntity<>(new BaseResponse("?????????? ?????????????? ??????????????!", cardResponseDTOS, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }

    @PostMapping("bank-clients")
    public ResponseEntity<BaseResponse> getClients(@RequestHeader(name = "Authorization") String token) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response for get all Clients.", tokenData.get("sub"));
        List<ClientEntity> clientEntities = clientService.getClients();
        if (clientEntities.isEmpty()) {
            log.error("Clients data not found.");
            return new ResponseEntity<>(new BaseResponse("?????????????? ???? ??????????????.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            List<ClientResponseDTO> clientResponseDTOS = new ArrayList<>();
            clientEntities.forEach(client -> clientResponseDTOS.add(ClientResponseDTO.clientData(client)));
            log.info("Clients data successfully found!");
            return new ResponseEntity<>(new BaseResponse("?????????????? ?????????????? ??????????????!", clientResponseDTOS, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }
}