package kg.banksystem.deliverybackend.rest;

import com.google.zxing.WriterException;
import kg.banksystem.deliverybackend.dto.order.request.OrderRequestDTO;
import kg.banksystem.deliverybackend.dto.order.request.ParseQrRequestDTO;
import kg.banksystem.deliverybackend.dto.order.response.OrderBriefResponseDTO;
import kg.banksystem.deliverybackend.dto.order.response.OrderStoryBriefResponseDTO;
import kg.banksystem.deliverybackend.entity.Order;
import kg.banksystem.deliverybackend.entity.OrderStory;
import kg.banksystem.deliverybackend.entity.response.BaseResponse;
import kg.banksystem.deliverybackend.enums.RestStatus;
import kg.banksystem.deliverybackend.security.jwt.JwtTokenDecoder;
import kg.banksystem.deliverybackend.service.OrderService;
import kg.banksystem.deliverybackend.service.OrderStoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/courier/")
public class CourierRestController {

    private final OrderService orderService;
    private final OrderStoryService orderStoryService;
    private final JwtTokenDecoder jwtTokenDecoder;

    @Autowired
    public CourierRestController(OrderService orderService, OrderStoryService orderStoryService, JwtTokenDecoder jwtTokenDecoder) {
        this.orderService = orderService;
        this.orderStoryService = orderStoryService;
        this.jwtTokenDecoder = jwtTokenDecoder;
    }

    @PostMapping("orders")
    public ResponseEntity<BaseResponse> getAllOrdersBrief(@RequestHeader(name = "Authorization") String token, @RequestBody OrderRequestDTO orderRequestDTO) {
        log.info("Request order status: {}", orderRequestDTO.getRequestStatus());

        if (orderRequestDTO.getRequestStatus() == null) {
            log.error("Incorrect status!");
            return new ResponseEntity<>(new BaseResponse("Неверный статус запроса!", null, RestStatus.ERROR), HttpStatus.OK);
        }

        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        List<Order> orders;
        switch (orderRequestDTO.getRequestStatus()) {
            case "new":
                orders = orderService.getAllNewOrdersByUserID(new Long(tokenData.get("user_id")));
                break;
            case "accepted":
                orders = orderService.getAllAcceptedOrdersByUserID(new Long(tokenData.get("user_id")));
                break;
            default:
                log.error("Incorrect status!");
                return new ResponseEntity<>(new BaseResponse("Неверный статус запроса!", null, RestStatus.ERROR), HttpStatus.OK);
        }

        if (orders.isEmpty()) {
            log.error("Orders data not found.");
            return new ResponseEntity<>(new BaseResponse("Заказы не найдены.", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            List<OrderBriefResponseDTO> orderBriefResponseDTOS = new ArrayList<>();
            orders.forEach(order -> orderBriefResponseDTOS.add(OrderBriefResponseDTO.orderBriefDTO(order)));
            log.info("Orders data successfully found.");
            return new ResponseEntity<>(new BaseResponse("Заказы успешно найдены.", orderBriefResponseDTOS, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }

    @PostMapping("orders/story")
    public ResponseEntity<BaseResponse> storyOrder(@RequestHeader(name = "Authorization") String token) {
        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        List<OrderStory> orderStories = orderStoryService.getStoryOrderByCourierID(new Long(tokenData.get("user_id")));

        if (orderStories == null) {
            log.error("Orders story data not found.");
            return new ResponseEntity<>(new BaseResponse("История заказов не найдена.", null, RestStatus.ERROR), HttpStatus.OK);
        }

        List<OrderStoryBriefResponseDTO> orderStoryBriefResponseDTOS = new ArrayList<>();
        orderStories.forEach(story -> orderStoryBriefResponseDTOS.add(OrderStoryBriefResponseDTO.orderStoryBriefDTO(story)));
        log.info("Orders story data successfully found.");
        return new ResponseEntity<>(new BaseResponse("История заказов успешно найдена.", orderStoryBriefResponseDTOS, RestStatus.SUCCESS), HttpStatus.OK);
    }

    @PostMapping("orders/accept")
    public ResponseEntity<BaseResponse> acceptOrder(@RequestHeader(name = "Authorization") String token, @RequestBody OrderRequestDTO orderRequestDTO) throws IOException, WriterException {
        log.info("Request order ID: {}", orderRequestDTO.getId());

        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        boolean order = orderService.acceptOrder(new Long(tokenData.get("user_id")), orderRequestDTO.getId());
        if (!order) {
            log.error("Order with ID: {} not accepted.", orderRequestDTO.getId());
            return new ResponseEntity<>(new BaseResponse("Заказ недоступен для принятия.", false, RestStatus.ERROR), HttpStatus.OK);
        } else {
            log.info("Order with ID: {} successfully accepted.", orderRequestDTO.getId());
            return new ResponseEntity<>(new BaseResponse("Заказ успешно принят.", true, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }

    @PostMapping("orders/checkQR")
    public ResponseEntity<BaseResponse> checkQrCode(@RequestHeader(name = "Authorization") String token,
                                                    @RequestBody ParseQrRequestDTO parseQrRequestDTO) {
        log.info("Request order ID: {} for checking QR code.", parseQrRequestDTO.getOrder_id());
        log.info("Request user ID: {} for checking QR code.", parseQrRequestDTO.getUser_id());

        if (parseQrRequestDTO.getOrder_id().equals(null) || parseQrRequestDTO.getUser_id().equals(null)) {
            log.error("QR code is incorrect!");
            return new ResponseEntity<>(new BaseResponse("Неверное представление QR кода!", null, RestStatus.ERROR), HttpStatus.OK);
        } else {
            boolean result = orderService.validQrCodeOrder(parseQrRequestDTO.getOrder_id(), parseQrRequestDTO.getUser_id());
            if (!result) {
                log.error("QR code is invalid!");
                return new ResponseEntity<>(new BaseResponse("Невалидный QR код.", result, RestStatus.ERROR), HttpStatus.OK);
            } else {
                log.info("QR code is valid! Success check.");
                return new ResponseEntity<>(new BaseResponse("QR код является валидным.", result, RestStatus.SUCCESS), HttpStatus.OK);
            }
        }
    }

    @PostMapping("orders/identification")
    public ResponseEntity<BaseResponse> identification(@RequestHeader(name = "Authorization") String token,
                                                       @RequestParam("photo") MultipartFile photo,
                                                       @RequestParam("client_id") Long id) {
        log.info("Name client photo: {}", photo.getOriginalFilename());
        log.info("Client id for identification: {}", id);
        try {
            boolean response = orderService.identificationClient(photo.getBytes(), photo.getOriginalFilename(), id);
            log.info("Success identification.");
            return new ResponseEntity<>(new BaseResponse("Идентификация завершена.", response, RestStatus.SUCCESS), HttpStatus.OK);
        } catch (IOException ioe) {
            log.error("Error identification.");
            return new ResponseEntity<>(new BaseResponse("Идентификация не была завершена.", null, RestStatus.ERROR), HttpStatus.OK);
        }
    }

    @PostMapping("orders/complete")
    public ResponseEntity<BaseResponse> completeOrder(@RequestHeader(name = "Authorization") String token,
                                                      @RequestBody OrderRequestDTO orderRequestDTO) {
        log.info("Request order ID: {}", orderRequestDTO.getId());
        log.info("Request complete status: {}", orderRequestDTO.getRequestStatus());
        log.info("Request complete comment: {}", orderRequestDTO.getComment());

        if (orderRequestDTO.getRequestStatus() == null) {
            log.error("Incorrect request status!");
            return new ResponseEntity<>(new BaseResponse("Неверный статус запроса!", null, RestStatus.ERROR), HttpStatus.OK);
        }

        if (!orderRequestDTO.getRequestStatus().equals("identification_client_error") &&
                !orderRequestDTO.getRequestStatus().equals("unable_to_find_client") &&
                !orderRequestDTO.getRequestStatus().equals("successful_delivery")) {
            log.error("Incorrect request status!");
            return new ResponseEntity<>(new BaseResponse("Неверный статус запроса!", null, RestStatus.ERROR), HttpStatus.OK);
        }

        Map<String, String> tokenData = jwtTokenDecoder.parseToken(token.substring(7));
        boolean order = orderService.completeOrder(new Long(tokenData.get("user_id")), orderRequestDTO.getId(), orderRequestDTO.getRequestStatus(), orderRequestDTO.getComment());
        if (!order) {
            log.error("Order was not complete by user with ID: {}", tokenData.get("user_id"));
            return new ResponseEntity<>(new BaseResponse("Заказ не был завершён успешно.", false, RestStatus.ERROR), HttpStatus.OK);
        } else {
            log.error("Order was successfully complete by user with ID: {}", tokenData.get("user_id"));
            return new ResponseEntity<>(new BaseResponse("Заказ был успешно завершён курьером.", true, RestStatus.SUCCESS), HttpStatus.OK);
        }
    }
}