package kg.banksystem.deliverybackend.service;

import com.google.zxing.WriterException;
import kg.banksystem.deliverybackend.entity.Branch;
import kg.banksystem.deliverybackend.entity.Order;

import java.io.IOException;
import java.util.List;

public interface OrderService {

    Order findById(Long id);

    // in progress
    Order addOrder(Order order, Branch branch);

    // in progress
    Order editOrder(Order order, Branch branch);

    // in progress
    void deleteOrder(Long id);

    List<Order> getAllActiveOrders();

    List<Order> getOrdersForBranch(Long user_id, String status);

    List<Order> getAllNewOrdersByUserID(Long id);

    List<Order> getAllAcceptedOrdersByUserID(Long id);

    boolean acceptOrder(Long id_user, Long id_order) throws IOException, WriterException;

    boolean completeOrder(Long id_user, Long id_order, String requestStatus, String comment);

    boolean validQrCodeOrder(Long order_id, Long user_id);

    boolean identificationClient(byte[] data, String fileName, Long client_id) throws IOException;

    boolean setReadyFromDeliveryStatusForOrder(Long order_id);

    boolean setDestroyedStatusForOrder(Long order_id);

    // in progress
    boolean setSentToBranchStatusForOrder(Long order_id);
}