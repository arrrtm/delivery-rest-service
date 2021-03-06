package kg.banksystem.deliverybackend.rest;

import kg.banksystem.deliverybackend.dto.admin.request.BranchRequestDTO;
import kg.banksystem.deliverybackend.dto.admin.response.BranchResponseDTO;
import kg.banksystem.deliverybackend.dto.user.request.UserRequestDTO;
import kg.banksystem.deliverybackend.dto.user.response.UserResponseDTO;
import kg.banksystem.deliverybackend.entity.BranchEntity;
import kg.banksystem.deliverybackend.entity.UserEntity;
import kg.banksystem.deliverybackend.entity.response.BaseResponse;
import kg.banksystem.deliverybackend.entity.response.PaginationResponse;
import kg.banksystem.deliverybackend.enums.RestStatus;
import kg.banksystem.deliverybackend.security.jwt.JwtTokenDecoder;
import kg.banksystem.deliverybackend.service.BranchService;
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
@RequestMapping("/api/admin/")
public class AdminRestController {

    private final UserService userService;
    private final BranchService branchService;
    private final JwtTokenDecoder jwtTokenDecoder;

    @Autowired
    public AdminRestController(UserService userService, BranchService branchService, JwtTokenDecoder jwtTokenDecoder) {
        this.userService = userService;
        this.branchService = branchService;
        this.jwtTokenDecoder = jwtTokenDecoder;
    }

    @PostMapping("users")
    public ResponseEntity<PaginationResponse> getAllUsers(@RequestHeader(name = "Authorization") String token, int page) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response get all Users.", tokenData.get("sub"));
        List<UserEntity> userEntities = userService.getAllUsers(page);
        if (userEntities.isEmpty()) {
            log.error("Users data not found.");
            return new ResponseEntity<>(new PaginationResponse("???????????????????????????????? ???????????? ???? ??????????????.", null, RestStatus.ERROR, userService.userPageCalculation(page)), HttpStatus.OK);
        } else {
            List<UserResponseDTO> userResponseDTOS = new ArrayList<>();
            userEntities.forEach(user -> userResponseDTOS.add(UserResponseDTO.userPersonalAccount(user)));
            log.info("Users all data successfully found.");
            return new ResponseEntity<>(new PaginationResponse("???????????????????????? ?????????????? ??????????????.", userResponseDTOS, RestStatus.SUCCESS, userService.userPageCalculation(page)), HttpStatus.OK);
        }
    }

    @PostMapping("users/remove")
    public ResponseEntity<BaseResponse> userRemove(@RequestHeader(name = "Authorization") String token, @RequestBody UserRequestDTO userRequestDTO) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response delete User.", tokenData.get("sub"));
        log.info("Request User Id for deleting: {}.", userRequestDTO.getId());
        if (userRequestDTO.getId() == null) {
            log.error("Request User Id can not be empty!");
            return new ResponseEntity<>(new BaseResponse("?????????????????????????? ???????????????????????? ???? ?????????? ???????? ????????????!", null, RestStatus.ERROR), HttpStatus.OK);
        }
        if (userService.removeUser(userRequestDTO)) {
            log.info("User deleted successfully!");
            return new ResponseEntity<>(new BaseResponse("???????????????????????? ?????? ?????????????? ????????????!", null, RestStatus.SUCCESS), HttpStatus.OK);
        } else {
            log.error("User has not been deleted.");
            return new ResponseEntity<>(new BaseResponse("???????????????????????? ???? ?????? ????????????. ?????????????????? ???????????? ?? ?????????????????? ?????????????? ?????? ??????!", null, RestStatus.ERROR), HttpStatus.OK);
        }
    }

    @PostMapping("branches")
    public ResponseEntity<PaginationResponse> getAllBranches(@RequestHeader(name = "Authorization") String token, int page) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response get all Branches.", tokenData.get("sub"));
        List<BranchEntity> branchEntities = branchService.getAllBranches(page);
        if (branchEntities.isEmpty()) {
            log.error("Branches data not found.");
            return new ResponseEntity<>(new PaginationResponse("?????????????? ???? ??????????????.", null, RestStatus.ERROR, branchService.branchPageCalculation(page)), HttpStatus.OK);
        } else {
            List<BranchResponseDTO> branchResponseDTOS = new ArrayList<>();
            branchEntities.forEach(branch -> branchResponseDTOS.add(BranchResponseDTO.branchData(branch)));
            log.info("Branches all data successfully found.");
            return new ResponseEntity<>(new PaginationResponse("?????????????? ?????????????? ??????????????.", branchResponseDTOS, RestStatus.SUCCESS, branchService.branchPageCalculation(page)), HttpStatus.OK);
        }
    }

    @PostMapping("branches/detail")
    public ResponseEntity<BaseResponse> getBranchById(@RequestHeader(name = "Authorization") String token, @RequestBody BranchRequestDTO branchRequestDTO) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response get Branch by Id.", tokenData.get("sub"));
        log.info("Request Branch Id: {}.", branchRequestDTO.getId());
        BranchEntity branchEntity = branchService.getBranchById(branchRequestDTO.getId());
        if (branchEntity == null) {
            log.error("Branch data for Branch with Id: {} not found.", branchRequestDTO.getId());
            return new ResponseEntity<>(new BaseResponse("???????????? ???? ????????????.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            BranchResponseDTO branchResponseDTO = BranchResponseDTO.branchData(branchEntity);
            log.info("Branch data for Branch with Id: {} successfully found.", branchRequestDTO.getId());
            return new ResponseEntity<>(new BaseResponse("???????????? ?????????????? ????????????.", branchResponseDTO, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }

    @PostMapping("branches/add")
    public ResponseEntity<BaseResponse> branchAdd(@RequestHeader(name = "Authorization") String token, @RequestBody BranchRequestDTO branchRequestDTO) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response add Branch.", tokenData.get("sub"));
        if (branchService.addBranch(branchRequestDTO)) {
            log.info("Branch data added successfully!");
            return new ResponseEntity<>(new BaseResponse(("?????????? ???????????? " + branchRequestDTO.getName() + " ?????? ?????????????? ????????????????!"), null, RestStatus.SUCCESS), HttpStatus.OK);
        } else {
            log.error("Branch data has not been added.");
            return new ResponseEntity<>(new BaseResponse("?????????? ???????????? ???? ?????? ????????????????, ???????????????? ???? ?????? ????????????????????. ?????????????????? ???????????? ?? ?????????????????? ?????????????? ?????? ??????!", null, RestStatus.ERROR), HttpStatus.OK);
        }
    }

    @PostMapping("branches/edit")
    public ResponseEntity<BaseResponse> branchEdit(@RequestHeader(name = "Authorization") String token, @RequestBody BranchRequestDTO branchRequestDTO) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response edit Branch.", tokenData.get("sub"));
        log.info("Request Branch Id for editing: {}.", branchRequestDTO.getId());
        if (branchRequestDTO.getId() == null) {
            log.error("Request Branch Id can not be empty!");
            return new ResponseEntity<>(new BaseResponse("?????????????????????????? ?????????????? ???? ?????????? ???????? ????????????!", null, RestStatus.ERROR), HttpStatus.OK);
        }
        if (branchService.editBranch(branchRequestDTO)) {
            log.info("Branch data updated successfully!");
            return new ResponseEntity<>(new BaseResponse(("???????????? " + branchRequestDTO.getName() + " ?????? ?????????????? ????????????????!"), null, RestStatus.SUCCESS), HttpStatus.OK);
        } else {
            log.error("Branch data has not been updated.");
            return new ResponseEntity<>(new BaseResponse("???????????? ???? ?????? ????????????????, ???????????????? ???? ?????? ????????????????????. ?????????????????? ???????????? ?? ?????????????????? ?????????????? ?????? ??????!", null, RestStatus.ERROR), HttpStatus.OK);
        }
    }

    @PostMapping("branches/delete")
    public ResponseEntity<BaseResponse> branchDelete(@RequestHeader(name = "Authorization") String token, @RequestBody BranchRequestDTO branchRequestDTO) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response delete Branch.", tokenData.get("sub"));
        log.info("Request Branch Id for deleting: {}.", branchRequestDTO.getId());
        if (branchRequestDTO.getId() == null) {
            log.error("Request Branch Id can not be empty!");
            return new ResponseEntity<>(new BaseResponse("?????????????????????????? ?????????????? ???? ?????????? ???????? ????????????!", null, RestStatus.ERROR), HttpStatus.OK);
        }
        if (branchService.deleteBranch(branchRequestDTO)) {
            log.info("Branch data deleted successfully!");
            return new ResponseEntity<>(new BaseResponse("???????????? ?????? ?????????????? ????????????!", null, RestStatus.SUCCESS), HttpStatus.OK);
        } else {
            log.error("Branch data has not been deleted.");
            return new ResponseEntity<>(new BaseResponse("???????????? ???? ?????? ????????????. ?????????????????? ???????????? ?? ?????????????????? ?????????????? ?????? ??????!", null, RestStatus.ERROR), HttpStatus.OK);
        }
    }
}