package kg.banksystem.deliverybackend.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import kg.banksystem.deliverybackend.dto.order.request.IdentificationRequestDTO;
import kg.banksystem.deliverybackend.dto.order.response.identification.IdentificationResponseMessageDTO;
import kg.banksystem.deliverybackend.entity.Branch;
import kg.banksystem.deliverybackend.entity.Client;
import kg.banksystem.deliverybackend.entity.Order;
import kg.banksystem.deliverybackend.entity.OrderStory;
import kg.banksystem.deliverybackend.enums.DeliveryCompleteStatus;
import kg.banksystem.deliverybackend.enums.OrderStatus;
import kg.banksystem.deliverybackend.repository.ClientRepository;
import kg.banksystem.deliverybackend.repository.OrderRepository;
import kg.banksystem.deliverybackend.repository.OrderStoryRepository;
import kg.banksystem.deliverybackend.repository.UserRepository;
import kg.banksystem.deliverybackend.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderStoryRepository orderStoryRepository;
    private final BranchServiceImpl branchService;
    private final ClientRepository clientRepository;
    String date;

    @Value("${qr-code.path}")
    private String pathQR;
    @Value("${identification.path}")
    private String pathPhoto;
    //  @Value("${identification.service.url}")
    private String urlIdentificationService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository, OrderStoryRepository orderStoryRepository, BranchServiceImpl branchService, ClientRepository clientRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderStoryRepository = orderStoryRepository;
        this.branchService = branchService;
        this.clientRepository = clientRepository;
    }

    @Override
    public Order findById(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            log.error("No order found by ID: {}.", id);
            return null;
        } else {
            log.info("Order: {} found by ID: {}.", order, id);
            return order;
        }
    }

    @Override
    public Order addOrder(Order order, Branch branch) {
        return null;
    }

    @Override
    public Order editOrder(Order order, Branch branch) {
        return null;
    }

    @Override
    public void deleteOrder(Long id) {
    }

    @Override
    public List<Order> getAllActiveOrders() {
        List<Order> orders = orderRepository.getAllActiveOrders();
        if (orders == null) {
            log.error("No active orders found is system.");
            return null;
        } else {
            log.info("{} orders successfully found.", orders.size());
            return orders;
        }
    }

    @Override
    public List<Order> getOrdersForBranch(Long user_id, String status) {
        List<Order> ordersForBranch;
        if (status.equals("new")) {
            ordersForBranch = orderRepository.getNewOrdersForBranch(user_id);
        } else if (status.equals("active")) {
            ordersForBranch = orderRepository.getActiveOrdersForBranch(user_id);
        } else if (status.equals("destroyed")) {
            ordersForBranch = orderRepository.getDestroyedOrdersForBranch(user_id);
        } else {
            log.error("No orders for Branch found by user with ID: {}.", user_id);
            return null;
        }
        if (ordersForBranch.isEmpty()) {
            log.error("No orders for Branch found by user with ID: {}.", user_id);
            return null;
        } else {
            log.info("Orders for Branch successfully found by User with ID: {}.", user_id);
            return ordersForBranch;
        }
    }

    @Override
    public List<Order> getAllNewOrdersByUserID(Long id) {
        Long branch = branchService.findBranchIdByUserId(id);
        List<Order> orders = orderRepository.getAllNewOrdersByBranch(branch);
        if (orders == null) {
            log.error("No new orders found by ID: {}.", id);
            return null;
        } else {
            log.info("New order: {} found by ID: {}.", orders, id);
            return orders;
        }
    }

    @Override
    public List<Order> getAllAcceptedOrdersByUserID(Long id) {
        List<Order> orders = orderRepository.getAllAcceptedOrdersByUserID(userRepository.getById(id));
        if (orders == null) {
            log.error("No accept orders found by ID: {}.", id);
            return null;
        } else {
            log.info("Accept order: {} found by ID: {}.", orders, id);
            return orders;
        }
    }

    @Override
    public boolean acceptOrder(Long id_user, Long id_order) throws IOException, WriterException {
        Order order = orderRepository.findById(id_order).orElse(null);
        if (order == null) {
            log.error("Order with ID: {} not available for acceptance.", id_order);
            return false;
        } else if (order.getStatus() != OrderStatus.READY_FROM_DELIVERY) {
            log.error("Order with ID: {} already accepted or not ready.", id_order);
            return false;
        } else {
            if (order.getUsers() == null)
                order.setUsers(new ArrayList<>());
            order.getUsers().add(userRepository.getById(id_user));
            order.setStatus(OrderStatus.TAKEN_BY_COURIER);
            orderRepository.save(order);

            createQR((id_order + " " + id_user));
            log.info("Order with ID: {} accepted by courier with ID: {}", id_order, id_user);
            return true;
        }
    }

    @Override
    public boolean validQrCodeOrder(Long order_id, Long user_id) {
        boolean isValid = orderRepository.validateQrCode(order_id, user_id);
        if (!isValid) {
            log.error("QR code for user with ID: {} and order with ID: {} not found.", user_id, order_id);
            return false;
        } else {
            Order order = orderRepository.findById(order_id).orElse(null);
            if (order == null) {
                log.error("Order with ID: {} not found.", order_id);
                return false;
            } else if (order.getStatus() != OrderStatus.TAKEN_BY_COURIER) {
                log.error("Order with ID: {} was not accepted by the courier with ID: {}", order_id, user_id);
                return false;
            } else {
                order.setStatus(OrderStatus.HANDED_OVER_TO_THE_COURIER);
                orderRepository.save(order);
                log.info("QR code for user with ID: {} and order with ID: {} successfully found.", user_id, order_id);
                return true;
            }
        }
    }

    @Override
    public boolean identificationClient(byte[] data, String fileName, Long client_id) throws IOException {
        BASE64Encoder encoder = new BASE64Encoder();
        IdentificationRequestDTO identification = new IdentificationRequestDTO();
        date = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(new Date());
        String dataClient = encoder.encode(data).replaceAll("[\r\n]", "");
        identification.setPhoto(dataClient);

        InputStream in = new ByteArrayInputStream(data);
        BufferedImage bImageFromConvert = ImageIO.read(in);
        String fullPath = pathPhoto + fileName + " - " + date + ".jpg";
        ImageIO.write(bImageFromConvert, "jpg", new File(fullPath));

        Client client = clientRepository.findClientById(client_id);
        identification.setPin(client.getClientPin().replaceAll(" ", ""));
        identification.setExternal_id(client.getId());
        identification.setMsisdn(client.getClientPhoneNumber().replaceAll("[ +()]", ""));
        identification.setIdentify_type("IDENTIFIED");

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> entity = new HttpEntity<>(identification);
        ResponseEntity<IdentificationResponseMessageDTO> response = restTemplate.exchange(urlIdentificationService, HttpMethod.POST, entity, IdentificationResponseMessageDTO.class);

        if (response.getBody().getStatus().equals("FAIL")) {
            log.error("Error identification. Message from server: {}", response.getBody().getMessage());
            return false;
        } else if (response.getBody().getStatus().equals("SUCCESS")) {
            log.info("Identification was complete. Message from server: {}", response.getBody().getData().getMessage());
            return response.getBody().getData().isIdentical();
        } else {
            log.error("Error identification. Message from server: {}", response.getBody().getMessage());
            return false;
        }
    }

    @Override
    public boolean setReadyFromDeliveryStatusForOrder(Long order_id) {
        Order order = orderRepository.findById(order_id).orElse(null);
        if (order == null) {
            log.error("Order with ID: {} not found.", order_id);
            return false;
        } else {
            order.setStatus(OrderStatus.READY_FROM_DELIVERY);
            orderRepository.save(order);
            log.info("Status for order with ID: {} changed on 'READY FROM DELIVERY'", order_id);
            return true;
        }
    }

    @Override
    public boolean setDestroyedStatusForOrder(Long order_id) {
        Order order = orderRepository.findById(order_id).orElse(null);
        if (order == null) {
            log.error("Order with ID: {} not found.", order_id);
            return false;
        } else {
            order.setStatus(OrderStatus.DESTROYED);
            orderRepository.save(order);
            log.info("Status for order with ID: {} changed on 'DESTROYED'", order_id);
            return true;
        }
    }

    @Override
    public boolean setSentToBranchStatusForOrder(Long order_id) {
        return false;
    }

    @Override
    public boolean completeOrder(Long id_user, Long id_order, String requestStatus, String comment) {
        Order order = orderRepository.findById(id_order).orElse(null);
        if (order == null) {
            log.error("Order with ID: {} not found.", id_order);
            return false;
        } else if (order.getStatus() != OrderStatus.HANDED_OVER_TO_THE_COURIER) {
            log.error("Order with ID: {} was not accepted by the courier with ID: {}", id_order, id_user);
            return false;
        } else {
            OrderStory orderStory = new OrderStory();
            Calendar calendar = new GregorianCalendar();
            Date date = calendar.getTime();
            orderStory.setCreated(date);
            orderStory.setUpdated(date);
            orderStory.setAddressDelivery(order.getAddressDelivery());
            orderStory.setAddressPickup(order.getAddressPickup());
            orderStory.setClient(order.getClient());
            orderStory.setBranch(order.getBranch());
            orderStory.setCard(order.getCard());
            orderStory.setComment(comment);
            orderStory.setUser(userRepository.getById(id_user));

            switch (requestStatus) {
                case "identification_client_error":
                    order.setStatus(OrderStatus.READY_FROM_DELIVERY);
                    orderStory.setStatus(DeliveryCompleteStatus.IDENTIFICATION_CLIENT_ERROR);
                    break;
                case "unable_to_find_client":
                    order.setStatus(OrderStatus.READY_FROM_DELIVERY);
                    orderStory.setStatus(DeliveryCompleteStatus.UNABLE_TO_FIND_CLIENT);
                    break;
                case "successful_delivery":
                    order.setStatus(OrderStatus.RECEIVED_BY_CLIENT);
                    orderStory.setStatus(DeliveryCompleteStatus.SUCCESSFUL_DELIVERY);
                    break;
            }
            order.getUsers().remove(userRepository.getById(id_user));
            orderRepository.save(order);
            orderStoryRepository.save(orderStory);
            log.info("Order with ID: {} successfully completed by courier with ID: {}", id_order, id_user);
            return true;
        }
    }

    private void createQR(String data) throws WriterException, IOException {
        date = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(new Date());
        String fullPath = pathQR + date + ".png";
        BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8), BarcodeFormat.QR_CODE, 200, 200);
        MatrixToImageWriter.writeToFile(matrix, fullPath.substring(fullPath.lastIndexOf('.') + 1), new File(fullPath));
    }
}