package kg.banksystem.deliverybackend.rest;

import kg.banksystem.deliverybackend.dto.admin.request.BranchRequestDTO;
import kg.banksystem.deliverybackend.dto.order.request.OrderRequestDTO;
import kg.banksystem.deliverybackend.dto.order.response.BranchResponseDTO;
import kg.banksystem.deliverybackend.dto.order.response.OrdersResponseDTO;
import kg.banksystem.deliverybackend.dto.user.request.UserRequestDTO;
import kg.banksystem.deliverybackend.dto.user.response.UserResponseDTO;
import kg.banksystem.deliverybackend.entity.Branch;
import kg.banksystem.deliverybackend.entity.Order;
import kg.banksystem.deliverybackend.entity.User;
import kg.banksystem.deliverybackend.entity.response.BaseResponse;
import kg.banksystem.deliverybackend.enums.RestStatus;
import kg.banksystem.deliverybackend.service.BranchService;
import kg.banksystem.deliverybackend.service.OrderService;
import kg.banksystem.deliverybackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/")
public class AdminRestController {

    private final UserService userService;
    private final OrderService orderService;
    private final BranchService branchService;

    @Autowired
    public AdminRestController(UserService userService, OrderService orderService, BranchService branchService) {
        this.userService = userService;
        this.orderService = orderService;
        this.branchService = branchService;
    }

    @PostMapping("users")
    public ResponseEntity<BaseResponse> getAllUsers(@RequestHeader(name = "Authorization") String token) {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            log.error("User data not found.");
            return new ResponseEntity<>(new BaseResponse("Пользовательские данные не найдены.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            List<UserResponseDTO> userResponseDTOS = new ArrayList<>();
            users.forEach(user -> userResponseDTOS.add(UserResponseDTO.userPersonalAccount(user)));
            log.info("User all data successfully found.");
            return new ResponseEntity<>(new BaseResponse("Пользователи успешно найдены.", userResponseDTOS, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }

    @PostMapping("users/detail")
    public ResponseEntity<BaseResponse> getUserById(@RequestHeader(name = "Authorization") String token, @RequestBody UserRequestDTO userRequestDTO) {
        log.info("Request user ID: {}", userRequestDTO.getId());

        User user = userService.findById(userRequestDTO.getId());
        if (user == null) {
            log.error("User data for user with ID: {} not found.", userRequestDTO.getId());
            return new ResponseEntity<>(new BaseResponse("Пользователь не найден.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            UserResponseDTO userResponseDTO = UserResponseDTO.userPersonalAccount(user);
            log.info("User data for user with ID: {} successfully found.", userRequestDTO.getId());
            return new ResponseEntity<>(new BaseResponse("Пользватель успешно найден.", userResponseDTO, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }

    @PostMapping("branches")
    public ResponseEntity<BaseResponse> getAllBranches(@RequestHeader(name = "Authorization") String token) {
        List<Branch> branches = branchService.getAllBranches();
        if (branches.isEmpty()) {
            log.error("Branch data not found.");
            return new ResponseEntity<>(new BaseResponse("Филиалы не найдены.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            List<BranchResponseDTO> branchResponseDTOS = new ArrayList<>();
            branches.forEach(branch -> branchResponseDTOS.add(BranchResponseDTO.branchData(branch)));
            log.info("Branch all data successfully found.");
            return new ResponseEntity<>(new BaseResponse("Филиалы успешно найдены.", branchResponseDTOS, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }

    @PostMapping("branches/detail")
    public ResponseEntity<BaseResponse> getBranchById(@RequestHeader(name = "Authorization") String token, @RequestBody BranchRequestDTO branchRequestDTO) {
        log.info("Request branch ID: {}", branchRequestDTO.getId());

        Branch branch = branchService.findById(branchRequestDTO.getId());
        if (branch == null) {
            log.error("Branch data for branch with ID: {} not found.", branchRequestDTO.getId());
            return new ResponseEntity<>(new BaseResponse("Филиал не найден.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            BranchResponseDTO branchResponseDTO = BranchResponseDTO.branchData(branch);
            log.info("Branch data for branch with ID: {} successfully found.", branchRequestDTO.getId());
            return new ResponseEntity<>(new BaseResponse("Филиал успешно найден.", branchResponseDTO, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }

    @PostMapping("orders")
    public ResponseEntity<BaseResponse> getAllOrders(@RequestHeader(name = "Authorization") String token) {
        List<Order> orders = orderService.getAllActiveOrders();
        if (orders.isEmpty()) {
            log.error("Orders data not found.");
            return new ResponseEntity<>(new BaseResponse("Заказы не найдены.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            List<OrdersResponseDTO> ordersResponseDTOS = new ArrayList<>();
            orders.forEach(order -> ordersResponseDTOS.add(OrdersResponseDTO.orders(order)));
            log.info("Orders all data successfully found.");
            return new ResponseEntity<>(new BaseResponse("Заказы успешно найдены.", ordersResponseDTOS, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }

    @PostMapping("orders/detail")
    public ResponseEntity<BaseResponse> getOrderById(@RequestHeader(name = "Authorization") String token, @RequestBody OrderRequestDTO orderRequestDTO) {
        log.info("Request order ID: {}", orderRequestDTO.getId());

        Order order = orderService.findById(orderRequestDTO.getId());
        if (order == null) {
            log.error("Order data for order with ID: {} not found.", orderRequestDTO.getId());
            return new ResponseEntity<>(new BaseResponse("Заказ не найден.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            OrdersResponseDTO adminResponseDTO = OrdersResponseDTO.orders(order);
            log.info("Order data for order with ID: {} successfully found.", orderRequestDTO.getId());
            return new ResponseEntity<>(new BaseResponse("Заказ успешно найден.", adminResponseDTO, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }
}