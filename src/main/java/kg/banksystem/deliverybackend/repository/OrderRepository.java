package kg.banksystem.deliverybackend.repository;

import kg.banksystem.deliverybackend.entity.Order;
import kg.banksystem.deliverybackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("select o from Order o join o.branch b where b.id in (:branch) and o.status = 'READY_FROM_DELIVERY'")
    List<Order> getAllNewOrdersByBranch(@Param("branch") Long branch);

    @Query("select o from Order o join o.users u where u in (:user) and (o.status = 'TAKEN_BY_COURIER' or o.status = 'HANDED_OVER_TO_THE_COURIER')")
    List<Order> getAllAcceptedOrdersByUserID(@Param("user") User user);

    @Query("select o from Order o where o.status <> 'RECEIVED_BY_CLIENT' AND o.status <> 'DESTROYED'")
    List<Order> getAllActiveOrders();

    @Query(value = "SELECT CASE WHEN COUNT(orders_users) > 0 THEN true ELSE false END FROM orders_users WHERE order_id = ?1 AND users_id = ?2", nativeQuery = true)
    boolean validateQrCode(Long order_id, Long user_id);

    @Query(value = "SELECT * FROM orders INNER JOIN users_branch ON users_branch.branch_id = orders.branch_id WHERE orders.status = 'SENT_TO_FILIAL' AND users_branch.user_id = ?1", nativeQuery = true)
    List<Order> getNewOrdersForBranch(Long user_id);

    @Query(value = "SELECT * FROM orders INNER JOIN users_branch ON users_branch.branch_id = orders.branch_id WHERE (orders.status = 'READY_FROM_DELIVERY' OR orders.status = 'TAKEN_BY_COURIER' OR orders.status = 'HANDED_OVER_TO_THE_COURIER') AND users_branch.user_id = ?1", nativeQuery = true)
    List<Order> getActiveOrdersForBranch(Long user_id);

    @Query(value = "SELECT * FROM orders INNER JOIN users_branch ON users_branch.branch_id = orders.branch_id WHERE orders.status = 'DESTROYED' AND users_branch.user_id = ?1", nativeQuery = true)
    List<Order> getDestroyedOrdersForBranch(Long user_id);
}