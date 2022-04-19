package kg.banksystem.deliverybackend.rest;

import kg.banksystem.deliverybackend.dto.auth.AuthenticationRequestDTO;
import kg.banksystem.deliverybackend.entity.User;
import kg.banksystem.deliverybackend.entity.response.BaseResponse;
import kg.banksystem.deliverybackend.enums.RestStatus;
import kg.banksystem.deliverybackend.security.jwt.JwtTokenProvider;
import kg.banksystem.deliverybackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth/")
public class AuthenticationRestController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Autowired
    public AuthenticationRestController(AuthenticationManager authenticationManager,
                                        JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PostMapping("login")
    public ResponseEntity<BaseResponse> login(@RequestBody AuthenticationRequestDTO authenticationDTO) {
        String username = authenticationDTO.getUsername();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, authenticationDTO.getPassword()));
            User user = userService.findByUsername(username);

            if (user == null) {
                log.error("User with username: {} not found.", username);
                return new ResponseEntity<>(new BaseResponse("Пользователь с логином: " + username + " не найден.", null, RestStatus.ERROR), HttpStatus.OK);
            } else {
                boolean authReset = userService.authAttemptReset(username);
                if (authReset) {
                    Map<Object, Object> response = new HashMap<>();
                    String token = jwtTokenProvider.createToken(username, user.getId(), user.getRole().getName());
                    response.put("token", token);
                    log.info("User with username: {} successfully login.", username);
                    return new ResponseEntity<>(new BaseResponse("Пользователь с логином: " + username + " успешно авторизован.", response, RestStatus.SUCCESS), HttpStatus.OK);
                } else {
                    log.error("Invalid username or password.");
                    return new ResponseEntity<>(new BaseResponse("Неверный логин или пароль.", null, RestStatus.ERROR), HttpStatus.OK);
                }
            }

        } catch (AuthenticationException e) {
            log.error("Invalid username or password.");
            String authResult = userService.authCheck(username);
            return new ResponseEntity<>(new BaseResponse(authResult, null, RestStatus.ERROR), HttpStatus.OK);
        }
    }
}