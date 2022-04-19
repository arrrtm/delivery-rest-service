package kg.banksystem.deliverybackend.rest;

import kg.banksystem.deliverybackend.dto.user.request.EditAccountRequestDTO;
import kg.banksystem.deliverybackend.dto.user.request.ResetPasswordRequestDTO;
import kg.banksystem.deliverybackend.dto.user.response.UserResponseDTO;
import kg.banksystem.deliverybackend.entity.User;
import kg.banksystem.deliverybackend.entity.response.BaseResponse;
import kg.banksystem.deliverybackend.enums.RestStatus;
import kg.banksystem.deliverybackend.security.jwt.JwtTokenDecoder;
import kg.banksystem.deliverybackend.service.AccountService;
import kg.banksystem.deliverybackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/account/")
public class AccountRestController {

    private final UserService userService;
    private final AccountService accountService;
    private final JwtTokenDecoder jwtTokenDecoder;

    @Autowired
    public AccountRestController(UserService userService, AccountService accountService, JwtTokenDecoder jwtTokenDecoder) {
        this.userService = userService;
        this.accountService = accountService;
        this.jwtTokenDecoder = jwtTokenDecoder;
    }

    @PostMapping("view")
    public ResponseEntity<BaseResponse> getPersonalAccount(@RequestHeader(name = "Authorization") String token) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User ID for get personal account: {}", tokenData.get("user_id"));
        User user = userService.findById(new Long(tokenData.get("user_id")));
        if (user == null) {
            log.error("User data with user ID: {} not found.", tokenData.get("user_id"));
            return new ResponseEntity<>(new BaseResponse("Персональные данные пользователя не найдены.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            Map<String, Object> userPersonalAccount = new HashMap<>();
            userPersonalAccount.put("userData", UserResponseDTO.userPersonalAccount(user));
            userPersonalAccount.put("countCompleteDelivery", userService.completeDeliveryByUserId(new Long(tokenData.get("user_id"))));
            log.info("User data with user ID: {} successfully found.", tokenData.get("user_id"));
            return new ResponseEntity<>(new BaseResponse("Персональные данные успешно найдены.", userPersonalAccount, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }

    @PostMapping("edit")
    public ResponseEntity<BaseResponse> editPersonalAccount(@RequestHeader(name = "Authorization") String token,
                                                            @RequestBody EditAccountRequestDTO editAccountRequestDTO) {
        log.info("Request status: {}", editAccountRequestDTO.getStatus());
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User ID for editing: {}", tokenData.get("user_id"));

        if (editAccountRequestDTO.getStatus() == null) {
            log.error("Error request status.");
            return new ResponseEntity<>(new BaseResponse("Неверный статус запроса!", null, RestStatus.ERROR), HttpStatus.OK);
        }

        if (!editAccountRequestDTO.getStatus().equals("personal_data") &&
                !editAccountRequestDTO.getStatus().equals("personal_password")) {
            log.error("Error request status.");
            return new ResponseEntity<>(new BaseResponse("Неверный статус запроса!", null, RestStatus.ERROR), HttpStatus.OK);
        }

        switch (editAccountRequestDTO.getStatus()) {
            case "personal_data":
                boolean editPersonalData = accountService.editAccount(new Long(tokenData.get("user_id")), editAccountRequestDTO);
                if (!editPersonalData) {
                    log.error("Error edit user.");
                    return new ResponseEntity<>(new BaseResponse("Авторизованный пользователь не может быть обновлён!", false, RestStatus.ERROR), HttpStatus.OK);
                } else {
                    log.info("Success edit user.");
                    return new ResponseEntity<>(new BaseResponse("Ваши персональные данные успешно обновлены!", true, RestStatus.SUCCESS), HttpStatus.OK);
                }
            case "personal_password":
                boolean editPersonalPassword = accountService.editPassword(new Long(tokenData.get("user_id")), editAccountRequestDTO);
                if (!editPersonalPassword) {
                    log.error("Error edit user password.");
                    return new ResponseEntity<>(new BaseResponse("Пароль авторизованного пользователя не может быть обновлён!", false, RestStatus.ERROR), HttpStatus.OK);
                } else {
                    log.info("Success edit user password.");
                    return new ResponseEntity<>(new BaseResponse("Ваш пароль успешно обновлён!", true, RestStatus.SUCCESS), HttpStatus.OK);
                }
            default:
                log.error("Error request status.");
                return new ResponseEntity<>(new BaseResponse("Неверный статус запроса!", null, RestStatus.ERROR), HttpStatus.OK);
        }
    }

    @PostMapping("password/reset")
    public ResponseEntity<BaseResponse> resetPersonalPassword(@RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO) {
        log.info("Username for reset password: {}", resetPasswordRequestDTO.getUsername());

        boolean resetStatus = accountService.resetPassword(resetPasswordRequestDTO);
        if (!resetStatus) {
            log.error("Error reset password.");
            return new ResponseEntity<>(new BaseResponse("Ошибка сброса пароля. Пользователь с указанным логином не найден в системе.", false, RestStatus.ERROR), HttpStatus.OK);
        } else {
            log.info("Success reset password.");
            return new ResponseEntity<>(new BaseResponse("Пароль успешно сброшен. Проверьте электронную почту.", true, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }
}