package kg.banksystem.deliverybackend.repository;

import kg.banksystem.deliverybackend.entity.OrderEntity;
import kg.banksystem.deliverybackend.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    @Query("select oe from OrderEntity oe join oe.branchEntity be where oe.deleted = false and be.id in (:branchId) and oe.status = 'READY_FROM_DELIVERY'")
    List<OrderEntity> getAllNewOrdersByBranchId(@Param("branchId") Long branchId);

    @Query("select oe from OrderEntity oe join oe.userEntities ue where oe.deleted = false and ue in (:user) and (oe.status = 'TAKEN_BY_COURIER' or oe.status = 'HANDED_OVER_TO_THE_COURIER')")
    List<OrderEntity> getAllAcceptedOrdersByUser(@Param("user") UserEntity user);

    @Query(value = "SELECT CASE WHEN COUNT(orders_user_entities) > 0 THEN true ELSE false END FROM orders_user_entities WHERE order_entity_id = ?1 AND user_entities_id = ?2", nativeQuery = true)
    boolean validateQrCode(Long orderId, Long userId);

    @Query(value = "SELECT * FROM orders INNER JOIN users_branch_entities ON users_branch_entities.branch_entities_id = orders.branch_entity_id WHERE orders.status = 'SENT_TO_FILIAL' AND users_branch_entities.user_entity_id = ?1 ORDER BY updated DESC", nativeQuery = true)
    Page<OrderEntity> getNewOrdersForBranchByUserId(Pageable pageable, Long userId);

    @Query(value = "SELECT * FROM orders INNER JOIN users_branch_entities ON users_branch_entities.branch_entities_id = orders.branch_entity_id WHERE (orders.status = 'READY_FROM_DELIVERY' OR orders.status = 'TAKEN_BY_COURIER' OR orders.status = 'HANDED_OVER_TO_THE_COURIER') AND users_branch_entities.user_entity_id = ?1 ORDER BY updated DESC", nativeQuery = true)
    Page<OrderEntity> getActiveOrdersForBranchByUserId(Pageable pageable, Long userId);

    @Query(value = "SELECT * FROM orders INNER JOIN users_branch_entities ON users_branch_entities.branch_entities_id = orders.branch_entity_id WHERE orders.status = 'DESTROYED' AND users_branch_entities.user_entity_id = ?1 ORDER BY updated DESC", nativeQuery = true)
    Page<OrderEntity> getDestroyedOrdersForBranchByUserId(Pageable pageable, Long userId);

    @Query("select oe from OrderEntity oe where oe.status <> 'RECEIVED_BY_CLIENT' and oe.status <> 'DESTROYED' and oe.deleted = false and oe.branchEntity.name = ?1")
    Page<OrderEntity> findAllByBranch(Pageable pageable, String branchName);

    @Query(value = "select * from orders oe where oe.is_deleted = false and oe.updated >= (current_date - ?2) and oe.branch_entity_id = ?1", nativeQuery = true)
    List<OrderEntity> findAllByBranch(Long branchId, int period);

    @NonNull
    @Override
    @Query("select oe from OrderEntity oe where (oe.status <> 'TAKEN_BY_COURIER' and oe.status <> 'DESTROYED') and oe.deleted = false")
    Page<OrderEntity> findAll(@NonNull Pageable pageable);

    @NonNull
    @Override
    @Query("select oe from OrderEntity oe where oe.deleted = false and oe.id = ?1")
    Optional<OrderEntity> findById(@NonNull Long orderId);

    @Query("select count(oe) from OrderEntity oe where (oe.status = 'SENT_TO_FILIAL' or oe.status = 'READY_FROM_DELIVERY' or oe.status = 'TAKEN_BY_COURIER' or oe.status = 'HANDED_OVER_TO_THE_COURIER') and oe.deleted = false and oe.branchEntity.id = ?1")
    Long totalCountOfOrdersActive(Long branchId);
}