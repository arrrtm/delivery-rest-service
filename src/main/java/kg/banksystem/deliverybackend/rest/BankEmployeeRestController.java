package kg.banksystem.deliverybackend.rest;

import kg.banksystem.deliverybackend.dto.bank.request.ClientRequestDTO;
import kg.banksystem.deliverybackend.dto.bank.request.UsersWithRoleRequestDTO;
import kg.banksystem.deliverybackend.dto.bank.response.ClientResponseDTO;
import kg.banksystem.deliverybackend.dto.order.request.OrderIdRequestDTO;
import kg.banksystem.deliverybackend.dto.order.request.OrderOperationsRequestDTO;
import kg.banksystem.deliverybackend.dto.user.response.UserResponseDTO;
import kg.banksystem.deliverybackend.entity.ClientEntity;
import kg.banksystem.deliverybackend.entity.UserEntity;
import kg.banksystem.deliverybackend.entity.response.BaseResponse;
import kg.banksystem.deliverybackend.entity.response.PaginationResponse;
import kg.banksystem.deliverybackend.enums.RestStatus;
import kg.banksystem.deliverybackend.security.jwt.JwtTokenDecoder;
import kg.banksystem.deliverybackend.service.ClientService;
import kg.banksystem.deliverybackend.service.OrderService;
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
@RequestMapping("/api/bank/")
public class BankEmployeeRestController {

    private final UserService userService;
    private final ClientService clientService;
    private final OrderService orderService;
    private final JwtTokenDecoder jwtTokenDecoder;

    @Autowired
    public BankEmployeeRestController(UserService userService, ClientService clientService, OrderService orderService, JwtTokenDecoder jwtTokenDecoder) {
        this.userService = userService;
        this.clientService = clientService;
        this.orderService = orderService;
        this.jwtTokenDecoder = jwtTokenDecoder;
    }

    @PostMapping("users")
    public ResponseEntity<PaginationResponse> getAllUsers(@RequestHeader(name = "Authorization") String token, @RequestBody UsersWithRoleRequestDTO users, int page) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response get all Users with Roles.", tokenData.get("sub"));
        log.info("Request Role: {}.", users.getRequestRole());
        if (users.getRequestRole().isEmpty() || (!users.getRequestRole().equals("courier") && !users.getRequestRole().equals("branch_employee"))) {
            log.error("Invalid request role.");
            return new ResponseEntity<>(new PaginationResponse("Неверная роль запроса!", null, RestStatus.ERROR, userService.userPageCalculation(page)), HttpStatus.OK);
        }
        List<UserEntity> usersListByRole = userService.getAllUsers(users.getRequestRole(), page);
        if (usersListByRole.isEmpty()) {
            log.error("Users data not found.");
            return new ResponseEntity<>(new PaginationResponse("Пользовательские данные не найдены.", null, RestStatus.ERROR, userService.userPageCalculation(page)), HttpStatus.OK);
        } else {
            List<UserResponseDTO> userResponseDTOS = new ArrayList<>();
            usersListByRole.forEach(user -> userResponseDTOS.add(UserResponseDTO.userPersonalAccount(user)));
            log.info("Users all data successfully found.");
            return new ResponseEntity<>(new PaginationResponse("Пользователи успешно найдены.", userResponseDTOS, RestStatus.SUCCESS, userService.userPageCalculation(page)), HttpStatus.OK);
        }
    }

    @PostMapping("clients")
    public ResponseEntity<PaginationResponse> getAllClients(@RequestHeader(name = "Authorization") String token, int page) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response get all Clients.", tokenData.get("sub"));
        List<ClientEntity> clientEntities = clientService.getAllClients(page);
        if (clientEntities.isEmpty()) {
            log.error("Clients data not found.");
            return new ResponseEntity<>(new PaginationResponse("Клиенты банка не найдены.", null, RestStatus.ERROR, clientService.clientPageCalculation(page)), HttpStatus.OK);
        } else {
            List<ClientResponseDTO> clientResponseDTOS = new ArrayList<>();
            clientEntities.forEach(client -> clientResponseDTOS.add(ClientResponseDTO.clientData(client)));
            log.info("Clients all data successfully found.");
            return new ResponseEntity<>(new PaginationResponse("Клиенты банка успешно найдены.", clientResponseDTOS, RestStatus.SUCCESS, clientService.clientPageCalculation(page)), HttpStatus.OK);
        }
    }

    @PostMapping("clients/detail")
    public ResponseEntity<BaseResponse> getClientById(@RequestHeader(name = "Authorization") String token, @RequestBody ClientRequestDTO clientRequestDTO) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response get Client by Id.", tokenData.get("sub"));
        log.info("Request Client Id: {}.", clientRequestDTO.getId());
        ClientEntity clientEntity = clientService.getClientById(clientRequestDTO.getId());
        if (clientEntity == null) {
            log.error("Client data for Client with Id: {} not found.", clientRequestDTO.getId());
            return new ResponseEntity<>(new BaseResponse("Клиент банка не найден.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            ClientResponseDTO clientResponseDTO = ClientResponseDTO.clientData(clientEntity);
            log.info("Client data for Client with Id: {} successfully found.", clientRequestDTO.getId());
            return new ResponseEntity<>(new BaseResponse("Клиент банка успешно найден.", clientResponseDTO, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }

    @PostMapping("clients/add")
    public ResponseEntity<BaseResponse> clientAdd(@RequestHeader(name = "Authorization") String token, @RequestBody ClientRequestDTO clientRequestDTO) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response add Client.", tokenData.get("sub"));
        if (clientService.addClient(clientRequestDTO)) {
            log.info("Client data added successfully!");
            return new ResponseEntity<>(new BaseResponse(("Новый клиент банка " + clientRequestDTO.getClientFullName() + " был успешно добавлен!"), null, RestStatus.SUCCESS), HttpStatus.OK);
        } else {
            log.error("Client data has not been added.");
            return new ResponseEntity<>(new BaseResponse(("Новый клиент банка " + clientRequestDTO.getClientFullName() + " не был добавлен, возможно он уже существует. Проверьте данные и повторите попытку ещё раз!"), null, RestStatus.ERROR), HttpStatus.OK);
        }
    }

    @PostMapping("clients/edit")
    public ResponseEntity<BaseResponse> clientEdit(@RequestHeader(name = "Authorization") String token, @RequestBody ClientRequestDTO clientRequestDTO) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response edit Client.", tokenData.get("sub"));
        log.info("Request Client Id for editing: {}.", clientRequestDTO.getId());
        if (clientRequestDTO.getId() == null) {
            log.error("Request Client Id can not be empty!");
            return new ResponseEntity<>(new BaseResponse("Идентификатор клиента банка не может быть пустым!", null, RestStatus.ERROR), HttpStatus.OK);
        }
        if (clientService.editClient(clientRequestDTO)) {
            log.info("Client data updated successfully!");
            return new ResponseEntity<>(new BaseResponse(("Клиент банка " + clientRequestDTO.getClientFullName() + " был успешно обновлён!"), null, RestStatus.SUCCESS), HttpStatus.OK);
        } else {
            log.error("Client data has not been updated.");
            return new ResponseEntity<>(new BaseResponse(("Клиент банка " + clientRequestDTO.getClientFullName() + " не был обновлён, возможно он уже существует. Проверьте данные и повторите попытку ещё раз!"), null, RestStatus.ERROR), HttpStatus.OK);
        }
    }

    @PostMapping("clients/delete")
    public ResponseEntity<BaseResponse> clientDelete(@RequestHeader(name = "Authorization") String token, @RequestBody ClientRequestDTO clientRequestDTO) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response delete Client.", tokenData.get("sub"));
        log.info("Request Client Id for deleting: {}.", clientRequestDTO.getId());
        if (clientRequestDTO.getId() == null) {
            log.error("Request Client Id can not be empty!");
            return new ResponseEntity<>(new BaseResponse("Идентификатор клиента банка не может быть пустым!", null, RestStatus.ERROR), HttpStatus.OK);
        }
        if (clientService.deleteClient(clientRequestDTO)) {
            log.info("Client data deleted successfully!");
            return new ResponseEntity<>(new BaseResponse("Клиент банка был успешно удалён!", null, RestStatus.SUCCESS), HttpStatus.OK);
        } else {
            log.error("Client data has not been deleted.");
            return new ResponseEntity<>(new BaseResponse("Клиент банка не был удалён. Проверьте данные и повторите попытку ещё раз!", null, RestStatus.ERROR), HttpStatus.OK);
        }
    }

    @PostMapping("orders/add")
    public ResponseEntity<BaseResponse> orderAdd(@RequestHeader(name = "Authorization") String token, @RequestBody OrderOperationsRequestDTO orderOperationsRequestDTO) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response add Order.", tokenData.get("sub"));
        if (orderService.addOrder(orderOperationsRequestDTO)) {
            log.info("Order added successfully!");
            return new ResponseEntity<>(new BaseResponse(("Новый заказ был успешно добавлен!"), null, RestStatus.SUCCESS), HttpStatus.OK);
        } else {
            log.error("Order has not been added.");
            return new ResponseEntity<>(new BaseResponse("Новый заказ не был добавлен, возможно он уже существует. Проверьте данные и повторите попытку ещё раз!", null, RestStatus.ERROR), HttpStatus.OK);
        }
    }

    @PostMapping("orders/edit")
    public ResponseEntity<BaseResponse> orderEdit(@RequestHeader(name = "Authorization") String token, @RequestBody OrderOperationsRequestDTO orderOperationsRequestDTO) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response edit Order.", tokenData.get("sub"));
        log.info("Request Order Id for editing: {}.", orderOperationsRequestDTO.getId());
        if (orderOperationsRequestDTO.getId() == null) {
            log.error("Request Order Id can not be empty!");
            return new ResponseEntity<>(new BaseResponse("Номер заказа не может быть пустым!", null, RestStatus.ERROR), HttpStatus.OK);
        }
        if (orderService.editOrder(orderOperationsRequestDTO)) {
            log.info("Order updated successfully!");
            return new ResponseEntity<>(new BaseResponse(("Заказ с номером " + orderOperationsRequestDTO.getId() + " был успешно обновлён!"), null, RestStatus.SUCCESS), HttpStatus.OK);
        } else {
            log.error("Order has not been updated.");
            return new ResponseEntity<>(new BaseResponse("Заказ не был обновлён, возможно он уже существует. Проверьте данные и повторите попытку ещё раз!", null, RestStatus.ERROR), HttpStatus.OK);
        }
    }

    @PostMapping("orders/delete")
    public ResponseEntity<BaseResponse> orderDelete(@RequestHeader(name = "Authorization") String token, @RequestBody OrderOperationsRequestDTO orderOperationsRequestDTO) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response delete Order.", tokenData.get("sub"));
        log.info("Request Order Id for deleting: {}.", orderOperationsRequestDTO.getId());
        if (orderOperationsRequestDTO.getId() == null) {
            log.error("Request Order Id can not be empty!");
            return new ResponseEntity<>(new BaseResponse("Номер заказа не может быть пустым!", null, RestStatus.ERROR), HttpStatus.OK);
        }
        if (orderService.deleteOrder(orderOperationsRequestDTO)) {
            log.info("Order deleted successfully!");
            return new ResponseEntity<>(new BaseResponse("Заказ был успешно удалён!", null, RestStatus.SUCCESS), HttpStatus.OK);
        } else {
            log.error("Order has not been deleted.");
            return new ResponseEntity<>(new BaseResponse("Заказ не был удалён. Проверьте данные и повторите попытку ещё раз!", null, RestStatus.ERROR), HttpStatus.OK);
        }
    }

    @PostMapping("orders/change/sent-to-branch")
    public ResponseEntity<BaseResponse> changeSentToBranch(@RequestHeader(name = "Authorization") String token, @RequestBody OrderIdRequestDTO orderIdRequestDTO) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        log.info("User with username: {} caused a response for change Order status.", tokenData.get("sub"));
        log.info("Order Id for change status: {}.", orderIdRequestDTO.getId());
        if (orderIdRequestDTO.getId() == null) {
            log.error("Order Id is empty.");
            return new ResponseEntity<>(new BaseResponse("Заказ по указанному идентификатору не найден.", false, RestStatus.ERROR), HttpStatus.OK);
        } else {
            boolean response = orderService.setSentToBranchStatusForOrder(orderIdRequestDTO.getId());
            if (!response) {
                log.error("Error changing order status to 'Sent to filial'.");
                return new ResponseEntity<>(new BaseResponse("Ошибка смены статуса заказа на 'Отправлен в филиал'.", false, RestStatus.ERROR), HttpStatus.OK);
            } else {
                log.info("The order has been successfully processed. Order status changed to 'Sent to filial'.");
                return new ResponseEntity<>(new BaseResponse("Заказ успешно обработан. Статус заказа изменён на 'Отправлен в филиал'.", true, RestStatus.SUCCESS), HttpStatus.OK);
            }
        }
    }
}