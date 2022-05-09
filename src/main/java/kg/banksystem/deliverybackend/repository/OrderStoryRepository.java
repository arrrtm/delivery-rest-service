package kg.banksystem.deliverybackend.repository;

import kg.banksystem.deliverybackend.entity.OrderStoryEntity;
import kg.banksystem.deliverybackend.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface OrderStoryRepository extends JpaRepository<OrderStoryEntity, Long> {
    @Query("select ose from OrderStoryEntity ose join ose.userEntity ue where ue in (:user) and ose.deleted = false")
    List<OrderStoryEntity> getStoryOrdersByUser(@Param("user") UserEntity user);

    @Query("select ose from OrderStoryEntity ose where ose.deleted = false and ose.branchEntity.name = ?1")
    Page<OrderStoryEntity> findAllByBranch(Pageable pageable, String branchName);

    @Query("select ose from OrderStoryEntity ose where ose.deleted = false and ose.orderNumber = ?1")
    Page<OrderStoryEntity> findAllByBranch(Pageable pageable, Long orderId);

    @Query("select ose from OrderStoryEntity ose where ose.deleted = false and ose.userEntity.id = ?1")
    Page<OrderStoryEntity> findAllByCourier(Pageable pageable, Long courierId);

    @NonNull
    @Override
    @Query("select ose from OrderStoryEntity ose where ose.deleted = false")
    Page<OrderStoryEntity> findAll(@NonNull Pageable pageable);

    @NonNull
    @Override
    @Query("select ose from OrderStoryEntity ose where ose.deleted = false and ose.id = ?1")
    Optional<OrderStoryEntity> findById(@NonNull Long orderId);

    @Query("select count(ose) from OrderStoryEntity ose where ose.deleted = false and ose.branchEntity.id = ?1")
    Long totalCountOfOrdersComplete(Long branchId);

    @Query(value = "select count(ose) from stories ose where ose.is_deleted = false and (ose.updated >= current_date - 7) and ose.branch_entity_id = ?1", nativeQuery = true)
    Long countOfCompletedOrdersPerWeek(Long branchId);

    @Query(value = "select count(ose) from stories ose where ose.is_deleted = false and (ose.updated >= current_date - 30) and ose.branch_entity_id = ?1", nativeQuery = true)
    Long countOfCompletedOrdersPerMonth(Long branchId);

    @Query(value = "select count(ose) from stories ose where ose.is_deleted = false and (ose.updated >= current_date - 365) and ose.branch_entity_id = ?1", nativeQuery = true)
    Long countOfCompletedOrdersPerYear(Long branchId);
}