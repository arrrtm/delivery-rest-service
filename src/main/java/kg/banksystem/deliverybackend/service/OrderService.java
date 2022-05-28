package kg.banksystem.deliverybackend.service;

import com.google.zxing.WriterException;
import kg.banksystem.deliverybackend.dto.order.request.OrderOperationsRequestDTO;
import kg.banksystem.deliverybackend.entity.CardEntity;
import kg.banksystem.deliverybackend.entity.OrderEntity;

import java.io.IOException;
import java.util.List;

public interface OrderService {

    List<OrderEntity> getAllActiveOrders(int page, Long orderNumber, Long branchId);

    List<OrderEntity> getOrdersForBranch(Long userId, String status, int page, Long orderNumber);

    OrderEntity findOrderById(Long orderId);

    boolean addOrder(OrderOperationsRequestDTO orderOperationsRequestDTO);

    boolean editOrder(OrderOperationsRequestDTO orderOperationsRequestDTO);

    boolean deleteOrder(OrderOperationsRequestDTO orderOperationsRequestDTO);

    List<OrderEntity> getAllNewOrdersByUserId(Long userId);

    List<OrderEntity> getAllAcceptedOrdersByUserId(Long userId);

    boolean acceptOrder(Long userId, Long orderId) throws IOException, WriterException;

    boolean completeOrder(Long userId, Long orderId, String requestStatus, String comment);

    boolean validQrCodeOrder(Long orderId, Long userId);

    boolean identificationClient(byte[] data, String fileName, Long clientId) throws IOException;

    boolean setReadyFromDeliveryStatusForOrder(Long orderId);

    boolean setDestroyedStatusForOrder(Long orderId);

    boolean setSentToBranchStatusForOrder(Long orderId);

    int orderPageCalculation(Long userId, String status, int page);

    int orderWithBranchPageCalculation(int page, Long branchId);

    String getQrUniqueName(Long orderId);

    List<CardEntity> getCards();
}