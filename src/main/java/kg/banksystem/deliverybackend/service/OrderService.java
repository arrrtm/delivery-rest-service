package kg.banksystem.deliverybackend.service;

import com.google.zxing.WriterException;
import kg.banksystem.deliverybackend.entity.BranchEntity;
import kg.banksystem.deliverybackend.entity.OrderEntity;

import java.io.IOException;
import java.util.List;

public interface OrderService {

    OrderEntity findOrderById(Long orderId);

    // IN PROGRESS
    boolean addOrder(OrderEntity orderEntity, BranchEntity branchEntity);

    // IN PROGRESS
    boolean editOrder(OrderEntity orderEntity, BranchEntity branchEntity);

    // IN PROGRESS
    boolean deleteOrder(Long orderId);

    List<OrderEntity> getAllActiveOrders(int page, Long orderNumber, String branchName);

    List<OrderEntity> getOrdersForBranch(Long userId, String status, int page);

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

    int orderWithBranchPageCalculation(int page, String branchName);

    String getQrUniqueName(Long orderId);
}