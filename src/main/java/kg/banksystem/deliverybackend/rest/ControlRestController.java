package kg.banksystem.deliverybackend.rest;

import kg.banksystem.deliverybackend.dto.user.request.UserRequestDTO;
import kg.banksystem.deliverybackend.entity.User;
import kg.banksystem.deliverybackend.entity.response.BaseResponse;
import kg.banksystem.deliverybackend.enums.RestStatus;
import kg.banksystem.deliverybackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/control/")
public class ControlRestController {

    private final UserService userService;

    @Autowired
    public ControlRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("user/block")
    public ResponseEntity<BaseResponse> blockUser(@RequestHeader(name = "Authorization") String token, @RequestBody UserRequestDTO userRequestDTO) {
        log.info("User ID for blocking: {}", userRequestDTO.getId());

        boolean lockResult = userService.blockUser(userRequestDTO.getId());
        User user = userService.findById(userRequestDTO.getId());
        if (!lockResult) {
            log.error("User with ID {} already blocked.", userRequestDTO.getId());
            return new ResponseEntity<>(new BaseResponse("Пользователь " + user.getUsername() + " уже заблокирован.", false, RestStatus.ERROR), HttpStatus.OK);
        } else {
            log.info("User with ID {} successfully blocked.", userRequestDTO.getId());
            return new ResponseEntity<>(new BaseResponse("Пользователь " + user.getUsername() + " успешно заблокирован.", true, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }

    @PostMapping("user/unblock")
    public ResponseEntity<BaseResponse> unblockUser(@RequestHeader(name = "Authorization") String token, @RequestBody UserRequestDTO userRequestDTO) {
        log.info("User ID for unblocking: {}", userRequestDTO.getId());

        boolean unlockResult = userService.unblockUser(userRequestDTO.getId());
        User user = userService.findById(userRequestDTO.getId());
        if (!unlockResult) {
            log.error("User with ID {} not blocked.", userRequestDTO.getId());
            return new ResponseEntity<>(new BaseResponse("Пользователь " + user.getUsername() + " не заблокирован.", false, RestStatus.ERROR), HttpStatus.OK);
        } else {
            log.info("User with ID {} successfully unblocked.", userRequestDTO.getId());
            return new ResponseEntity<>(new BaseResponse("Пользователь " + user.getUsername() + " успешно разблокирован.", true, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }
}