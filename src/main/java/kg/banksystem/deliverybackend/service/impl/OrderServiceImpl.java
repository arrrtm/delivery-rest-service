package kg.banksystem.deliverybackend.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import kg.banksystem.deliverybackend.dto.order.request.IdentificationRequestDTO;
import kg.banksystem.deliverybackend.dto.order.request.OrderOperationsRequestDTO;
import kg.banksystem.deliverybackend.dto.order.response.identification.IdentificationResponseMessageDTO;
import kg.banksystem.deliverybackend.entity.*;
import kg.banksystem.deliverybackend.enums.DeliveryCompleteStatus;
import kg.banksystem.deliverybackend.enums.DeliveryType;
import kg.banksystem.deliverybackend.enums.OrderStatus;
import kg.banksystem.deliverybackend.repository.*;
import kg.banksystem.deliverybackend.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderStoryRepository orderStoryRepository;
    private final BranchRepository branchRepository;
    private final CardRepository cardRepository;
    private final ClientRepository clientRepository;
    private final QrCodeRepository qrCodeRepository;

    @Value("${qr-code.path}")
    private String pathQR;

    @Value("${identification.path}")
    private String pathPhoto;

    @Value("${identification.service.url}")
    private String urlIdentificationService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository, OrderStoryRepository orderStoryRepository, BranchRepository branchRepository, CardRepository cardRepository, ClientRepository clientRepository, QrCodeRepository qrCodeRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderStoryRepository = orderStoryRepository;
        this.branchRepository = branchRepository;
        this.cardRepository = cardRepository;
        this.clientRepository = clientRepository;
        this.qrCodeRepository = qrCodeRepository;
    }

    @Override
    public List<OrderEntity> getAllActiveOrders(int page, Long orderNumber, Long branchId) {
        if (orderNumber != null) {
            try {
                return Stream.of(findOrderById(orderNumber)).filter(orderEntity -> !orderEntity.getStatus().equals(OrderStatus.DESTROYED)).collect(Collectors.toList());
            } catch (NullPointerException npe) {
                log.error("No active orders found.");
                return null;
            }
        } else if (branchId != null) {
            try {
                return orderRepository.findAllByBranch(PageRequest.of(page, 5, Sort.by("updatedDate").descending()), branchId).getContent();
            } catch (NullPointerException npe) {
                log.error("No active orders found.");
                return null;
            }
        } else {
            Page<OrderEntity> orders = orderRepository.findAll(PageRequest.of(page, 5, Sort.by("updatedDate").descending()));
            if (orders == null) {
                log.error("No active orders found.");
                return null;
            } else {
                log.info("{} active orders successfully found.", orders.getContent().size());
                return orders.getContent();
            }
        }
    }

    @Override
    public List<OrderEntity> getOrdersForBranch(Long userId, String status, int page, Long orderNumber) {
        Long branchId = branchRepository.findBranchIdByUserId(userId);
        if (orderNumber != null) {
            try {
                return Stream.of(findOrderById(orderNumber)).filter(orderEntity -> orderEntity.getBranchEntity().getId().equals(branchId)).collect(Collectors.toList());
            } catch (NullPointerException npe) {
                log.error("No orders found by orderNumber {}", orderNumber);
                return null;
            }
        }
        Page<OrderEntity> ordersForBranches;
        switch (status) {
            case "new":
                ordersForBranches = orderRepository.getNewOrdersForBranchByUserId(PageRequest.of(page, 5), userId);
                break;
            case "active":
                ordersForBranches = orderRepository.getActiveOrdersForBranchByUserId(PageRequest.of(page, 5), userId);
                break;
            case "destroyed":
                ordersForBranches = orderRepository.getDestroyedOrdersForBranchByUserId(PageRequest.of(page, 5), userId);
                break;
            default:
                log.error("No orders for Branch found by User with userId: {}.", userId);
                return null;
        }
        if (ordersForBranches.isEmpty()) {
            log.error("No orders for Branch found by User with userId: {}.", userId);
            return null;
        } else {
            log.info("Orders for Branch successfully found by User with userId: {}.", userId);
            return ordersForBranches.getContent();
        }
    }

    @Override
    public OrderEntity findOrderById(Long orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElse(null);
        if (orderEntity == null) {
            log.error("No order found by orderId: {}.", orderId);
            return null;
        } else {
            log.info("Order: {} successfully found by orderId: {}.", orderEntity, orderId);
            return orderEntity;
        }
    }

    @Override
    public boolean addOrder(OrderOperationsRequestDTO orderOperationsRequestDTO) {
        BranchEntity branch = branchRepository.findById(orderOperationsRequestDTO.getBranch()).orElse(null);
        if (branch == null) {
            log.error("Branch with Id: {} not found.", orderOperationsRequestDTO.getBranch());
            return false;
        }
        CardEntity card = cardRepository.findById(orderOperationsRequestDTO.getCard()).orElse(null);
        if (card == null) {
            log.error("Card with Id: {} not found.", orderOperationsRequestDTO.getCard());
            return false;
        }
        ClientEntity client = clientRepository.findById(orderOperationsRequestDTO.getClient()).orElse(null);
        if (client == null) {
            log.error("Client with Id: {} not found.", orderOperationsRequestDTO.getClient());
            return false;
        }
        try {
            OrderEntity order = new OrderEntity();
            order.setCreatedDate(LocalDateTime.now());
            order.setUpdatedDate(LocalDateTime.now());
            order.setDeleted(false);
            order.setAddressDelivery(orderOperationsRequestDTO.getAddressDelivery());
            order.setAddressPickup(orderOperationsRequestDTO.getAddressPickup());
            order.setStatus(OrderStatus.NEW_ORDER);
            order.setTypeDelivery(DeliveryType.COURIER_DELIVERY);
            order.setBranchEntity(branch);
            order.setCardEntity(card);
            order.setClientEntity(client);
            orderRepository.save(order);
            log.info("Order: {} successfully added.", orderOperationsRequestDTO.getId());
            return true;
        } catch (Exception ex) {
            log.error("Order: {} was not added.", orderOperationsRequestDTO.getId());
            System.out.println(ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean editOrder(OrderOperationsRequestDTO orderOperationsRequestDTO) {
        OrderEntity order = findOrderById(orderOperationsRequestDTO.getId());
        if (order == null) {
            log.error("Order with orderId: {} was not found.", orderOperationsRequestDTO.getId());
            return false;
        } else {
            try {
                BranchEntity branch = branchRepository.findById(orderOperationsRequestDTO.getBranch()).orElse(null);
                if (branch == null) {
                    log.error("Branch with Id: {} not found.", orderOperationsRequestDTO.getBranch());
                    return false;
                }
                CardEntity card = cardRepository.findById(orderOperationsRequestDTO.getCard()).orElse(null);
                if (card == null) {
                    log.error("Card with Id: {} not found.", orderOperationsRequestDTO.getCard());
                    return false;
                }
                ClientEntity client = clientRepository.findById(orderOperationsRequestDTO.getClient()).orElse(null);
                if (client == null) {
                    log.error("Client with Id: {} not found.", orderOperationsRequestDTO.getClient());
                    return false;
                }
                order.setUpdatedDate(LocalDateTime.now());
                order.setAddressDelivery(orderOperationsRequestDTO.getAddressDelivery());
                order.setAddressPickup(orderOperationsRequestDTO.getAddressPickup());
                order.setStatus(OrderStatus.IN_PROCESS);
                order.setTypeDelivery(DeliveryType.COURIER_DELIVERY);
                order.setBranchEntity(branch);
                order.setCardEntity(card);
                order.setClientEntity(client);
                orderRepository.save(order);
                log.info("Order with orderId: {} successfully updated", orderOperationsRequestDTO.getId());
                return true;
            } catch (Exception ex) {
                log.error("Order with orderId: {} was not updated.", orderOperationsRequestDTO.getId());
                System.out.println(ex.getMessage());
                return false;
            }
        }
    }

    @Override
    public boolean deleteOrder(OrderOperationsRequestDTO orderOperationsRequestDTO) {
        OrderEntity order = findOrderById(orderOperationsRequestDTO.getId());
        if (order == null) {
            log.error("Order with orderId: {} was not found.", orderOperationsRequestDTO.getId());
            return false;
        } else {
            try {
                order.setUpdatedDate(LocalDateTime.now());
                order.setDeletedDate(LocalDateTime.now());
                order.setDeleted(true);
                orderRepository.save(order);
                log.info("Order with orderId: {} successfully removed. It can be viewed in the database.", orderOperationsRequestDTO.getId());
                return true;
            } catch (Exception ex) {
                log.error("Order with orderId: {} was not removed.", orderOperationsRequestDTO.getId());
                System.out.println(ex.getMessage());
                return false;
            }
        }
    }

    @Override
    public List<OrderEntity> getAllNewOrdersByUserId(Long userId) {
        Long branchId = branchRepository.findBranchIdByUserId(userId);
        if (branchId == null) {
            log.error("Branch Id not found by User with userId: {}", userId);
            return null;
        }
        List<OrderEntity> orderEntities = orderRepository.getAllNewOrdersByBranchId(branchId);
        if (orderEntities.isEmpty()) {
            log.error("No new orders found by User with userId: {}.", userId);
            return null;
        } else {
            log.info("New orders: {} successfully found by User with userId: {}.", orderEntities, userId);
            return orderEntities;
        }
    }

    @Override
    public List<OrderEntity> getAllAcceptedOrdersByUserId(Long userId) {
        List<OrderEntity> orderEntities = orderRepository.getAllAcceptedOrdersByUser(userRepository.getById(userId));
        if (orderEntities.isEmpty()) {
            log.error("No accept orders found by User with userId: {}.", userId);
            return null;
        } else {
            log.info("Accept orders: {} successfully found by User with userId: {}.", orderEntities, userId);
            return orderEntities;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean acceptOrder(Long userId, Long orderId) throws IOException, WriterException {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElse(null);
        if (orderEntity == null) {
            log.error("Order with orderId: {} not available for acceptance.", orderId);
            return false;
        } else if (orderEntity.getStatus() != OrderStatus.READY_FROM_DELIVERY) {
            log.error("Order with orderId: {} already accepted or not ready.", orderId);
            return false;
        } else {
            if (orderEntity.getUserEntities() == null) orderEntity.setUserEntities(new ArrayList<>());
            orderEntity.getUserEntities().add(userRepository.getById(userId));
            orderEntity.setStatus(OrderStatus.TAKEN_BY_COURIER);
            orderRepository.save(orderEntity);
            String uniqueName = createQrToServer((orderId + " " + userId));
            QrCodeTemporaryEntity qrCodeTemporaryEntity = new QrCodeTemporaryEntity(orderId, userId, uniqueName);
            qrCodeRepository.save(qrCodeTemporaryEntity);
            log.info("Order with orderId: {} accepted by Courier with userId: {}.", orderId, userId);
            return true;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean validQrCodeOrder(Long orderId, Long userId) {
        boolean isValid = orderRepository.validateQrCode(orderId, userId);
        if (!isValid) {
            log.error("QR code for User with userId: {} and Order with orderId: {} not found.", userId, orderId);
            return false;
        } else {
            OrderEntity orderEntity = orderRepository.findById(orderId).orElse(null);
            if (orderEntity == null) {
                log.error("Order with orderId: {} not found.", orderId);
                return false;
            } else if (orderEntity.getStatus() != OrderStatus.TAKEN_BY_COURIER) {
                log.error("Order with orderId: {} was not accepted by the Courier with userId: {}.", orderId, userId);
                return false;
            } else {
                orderEntity.setStatus(OrderStatus.HANDED_OVER_TO_THE_COURIER);
                orderRepository.save(orderEntity);
                QrCodeTemporaryEntity qrCodeTemporaryEntity = qrCodeRepository.findByOrderIdAndUserId(orderId, userId);
                deleteQrFromServer(qrCodeTemporaryEntity.getPath().trim());
                qrCodeRepository.delete(qrCodeTemporaryEntity);
                log.info("QR code for User with userId: {} and Order with orderId: {} successfully found.", userId, orderId);
                return true;
            }
        }
    }

    @Override
    public boolean identificationClient(byte[] data, String fileName, Long clientId) throws IOException {
        BASE64Encoder encoder = new BASE64Encoder();
        IdentificationRequestDTO identification = new IdentificationRequestDTO();
        String dataClient = encoder.encode(data).replaceAll("[\r\n]", "");
        identification.setPhoto(dataClient);

        InputStream in = new ByteArrayInputStream(data);
        BufferedImage bImageFromConvert = ImageIO.read(in);
        String fullPath = pathPhoto + fileName + " - " + LocalDateTime.now().toString().replace(":", "-") + ".jpg";
        ImageIO.write(bImageFromConvert, "jpg", new File(fullPath));

        ClientEntity clientEntity = clientRepository.findById(clientId).orElse(null);
        identification.setPin(clientEntity.getClientPin().replaceAll(" ", ""));
        identification.setExternal_id(clientEntity.getId());
        identification.setMsisdn(clientEntity.getClientPhoneNumber().replaceAll("[ +()]", ""));
        identification.setIdentify_type("IDENTIFIED");

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> entity = new HttpEntity<>(identification);
        ResponseEntity<IdentificationResponseMessageDTO> response = restTemplate.exchange(urlIdentificationService, HttpMethod.POST, entity, IdentificationResponseMessageDTO.class);

        if (response.getBody().getStatus().equals("FAIL")) {
            log.error("Error identification. Message from server: {}.", response.getBody().getMessage());
            return false;
        } else if (response.getBody().getStatus().equals("SUCCESS")) {
            log.info("Identification was complete. Message from server: {}.", response.getBody().getData().getMessage());
            return response.getBody().getData().isIdentical();
        } else {
            log.error("Error identification. Message from server: {}.", response.getBody().getMessage());
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean completeOrder(Long userId, Long orderId, String requestStatus, String comment) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElse(null);
        if (orderEntity == null) {
            log.error("Order with orderId: {} not found.", orderId);
            return false;
        } else if (orderEntity.getStatus() != OrderStatus.HANDED_OVER_TO_THE_COURIER) {
            log.error("Order with orderId: {} was not accepted by Courier with userId: {}.", orderId, userId);
            return false;
        } else {
            OrderStoryEntity orderStoryEntity = new OrderStoryEntity();
            orderStoryEntity.setCreatedDate(LocalDateTime.now());
            orderStoryEntity.setUpdatedDate(LocalDateTime.now());
            orderStoryEntity.setAddressDelivery(orderEntity.getAddressDelivery());
            orderStoryEntity.setAddressPickup(orderEntity.getAddressPickup());
            orderStoryEntity.setClientEntity(orderEntity.getClientEntity());
            orderStoryEntity.setBranchEntity(orderEntity.getBranchEntity());
            orderStoryEntity.setCardEntity(orderEntity.getCardEntity());
            orderStoryEntity.setOrderNumber(orderEntity.getId());
            orderStoryEntity.setComment(comment);
            orderStoryEntity.setUserEntity(userRepository.getById(userId));
            switch (requestStatus) {
                case "identification_client_error":
                    orderEntity.setStatus(OrderStatus.READY_FROM_DELIVERY);
                    orderStoryEntity.setStatus(DeliveryCompleteStatus.IDENTIFICATION_CLIENT_ERROR);
                    break;
                case "unable_to_find_client":
                    orderEntity.setStatus(OrderStatus.READY_FROM_DELIVERY);
                    orderStoryEntity.setStatus(DeliveryCompleteStatus.UNABLE_TO_FIND_CLIENT);
                    break;
                case "successful_delivery":
                    orderEntity.setStatus(OrderStatus.RECEIVED_BY_CLIENT);
                    orderStoryEntity.setStatus(DeliveryCompleteStatus.SUCCESSFUL_DELIVERY);
                    break;
                default:
                    return false;
            }
            orderEntity.getUserEntities().remove(userRepository.getById(userId));
            orderRepository.save(orderEntity);
            orderStoryRepository.save(orderStoryEntity);
            log.info("Order with orderId: {} successfully completed by Courier with userId: {}.", orderId, userId);
            return true;
        }
    }

    @Override
    public boolean setReadyFromDeliveryStatusForOrder(Long orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElse(null);
        if (orderEntity == null) {
            log.error("Order with orderId: {} not found.", orderId);
            return false;
        } else {
            orderEntity.setStatus(OrderStatus.READY_FROM_DELIVERY);
            orderEntity.setUpdatedDate(LocalDateTime.now());
            orderRepository.save(orderEntity);
            log.info("Status for Order with orderId: {} changed on 'READY FROM DELIVERY'.", orderId);
            return true;
        }
    }

    @Override
    public boolean setDestroyedStatusForOrder(Long orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElse(null);
        if (orderEntity == null) {
            log.error("Order with orderId: {} not found.", orderId);
            return false;
        } else {
            orderEntity.setStatus(OrderStatus.DESTROYED);
            orderEntity.setUpdatedDate(LocalDateTime.now());
            orderRepository.save(orderEntity);
            log.info("Status for Order with orderId: {} changed on 'DESTROYED'.", orderId);
            return true;
        }
    }

    @Override
    public boolean setSentToBranchStatusForOrder(Long orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElse(null);
        if (orderEntity == null) {
            log.error("Order with orderId: {} not found.", orderId);
            return false;
        } else {
            orderEntity.setStatus(OrderStatus.SENT_TO_FILIAL);
            orderEntity.setUpdatedDate(LocalDateTime.now());
            orderRepository.save(orderEntity);
            log.info("Status for Order with orderId: {} changed on 'SENT TO FILIAL'.", orderId);
            return true;
        }
    }

    @Override
    public int orderPageCalculation(Long userId, String status, int page) {
        Page<OrderEntity> orders;
        switch (status) {
            case "new":
                orders = orderRepository.getNewOrdersForBranchByUserId(PageRequest.of(page, 5), userId);
                break;
            case "active":
                orders = orderRepository.getActiveOrdersForBranchByUserId(PageRequest.of(page, 5), userId);
                break;
            case "destroyed":
                orders = orderRepository.getDestroyedOrdersForBranchByUserId(PageRequest.of(page, 5), userId);
                break;
            default:
                orders = orderRepository.findAll(PageRequest.of(page, 5, Sort.by("updatedDate").descending()));
                break;
        }
        return orders.getTotalPages();
    }

    @Override
    public int orderWithBranchPageCalculation(int page, Long branchId) {
        Page<OrderEntity> orders = orderRepository.findAllByBranch(PageRequest.of(page, 5, Sort.by("updatedDate").descending()), branchId);
        return orders.getTotalPages();
    }

    @Override
    public String getQrUniqueName(Long orderId) {
        return qrCodeRepository.getQrName(orderId).orElse(null);
    }

    @Override
    public List<CardEntity> getCards() {
        return cardRepository.findAll().stream().filter(order -> !order.isDeleted()).collect(Collectors.toList());
    }

    private String createQrToServer(String data) throws WriterException, IOException {
        String uniqueName = LocalDateTime.now().toString().replace(":", "-") + ".png";
        String fullPath = pathQR + uniqueName;
        BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8), BarcodeFormat.QR_CODE, 200, 200);
        MatrixToImageWriter.writeToFile(matrix, fullPath.substring(fullPath.lastIndexOf('.') + 1), new File(fullPath));
        log.info("Created QR-code along the way: {}", fullPath);
        return uniqueName;
    }

    private void deleteQrFromServer(String uniqueName) {
        File fileForDelete = new File(pathQR + uniqueName);
        if (fileForDelete.delete()) {
            log.info("QR code successfully deleted.");
        } else {
            log.error("Error while deleting QR-code.");
        }
    }
}