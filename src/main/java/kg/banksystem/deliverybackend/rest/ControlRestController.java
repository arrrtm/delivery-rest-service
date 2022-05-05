package kg.banksystem.deliverybackend.rest;

import kg.banksystem.deliverybackend.dto.user.request.UserRequestDTO;
import kg.banksystem.deliverybackend.entity.UserEntity;
import kg.banksystem.deliverybackend.entity.response.BaseResponse;
import kg.banksystem.deliverybackend.enums.RestStatus;
import kg.banksystem.deliverybackend.security.jwt.JwtTokenDecoder;
import kg.banksystem.deliverybackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/control/")
public class ControlRestController {

    private final UserService userService;
    private final JwtTokenDecoder jwtTokenDecoder;

    @Autowired
    public ControlRestController(UserService userService, JwtTokenDecoder jwtTokenDecoder) {
        this.userService = userService;
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
}