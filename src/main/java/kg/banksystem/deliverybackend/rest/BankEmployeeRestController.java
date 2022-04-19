package kg.banksystem.deliverybackend.rest;

import kg.banksystem.deliverybackend.dto.bank.request.UsersWithRoleRequestDTO;
import kg.banksystem.deliverybackend.dto.user.response.UserResponseDTO;
import kg.banksystem.deliverybackend.entity.User;
import kg.banksystem.deliverybackend.entity.response.BaseResponse;
import kg.banksystem.deliverybackend.enums.RestStatus;
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
@RequestMapping("/api/bank/")
public class BankEmployeeRestController {

    private final UserService userService;

    @Autowired
    public BankEmployeeRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("users")
    public ResponseEntity<BaseResponse> getAllUsers(@RequestHeader(name = "Authorization") String token, @RequestBody UsersWithRoleRequestDTO users) {
        log.info("Request Role: {}", users.getRequestRole());

        if (users.getRequestRole().isEmpty() ||
                (!users.getRequestRole().equals("courier") &&
                        !users.getRequestRole().equals("branch_employee"))) {
            log.error("Invalid request role.");
            return new ResponseEntity<>(new BaseResponse("Неверный статус запроса!", null, RestStatus.ERROR), HttpStatus.OK);
        }

        List<User> usersListByRole = userService.getAllUsers(users.getRequestRole());

        if (usersListByRole.isEmpty()) {
            log.error("Users data not found.");
            return new ResponseEntity<>(new BaseResponse("Пользовательские данные не найдены.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            List<UserResponseDTO> userResponseDTOS = new ArrayList<>();
            usersListByRole.forEach(user -> userResponseDTOS.add(UserResponseDTO.userPersonalAccount(user)));
            log.info("Users all data successfully found.");
            return new ResponseEntity<>(new BaseResponse("Пользователи успешно найдены.", userResponseDTOS, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }
}