package kg.banksystem.deliverybackend.repository;

import kg.banksystem.deliverybackend.entity.OrderStoryEntity;
import kg.banksystem.deliverybackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderStoryRepository extends JpaRepository<OrderStoryEntity, Long> {
    @Query("select ose from OrderStoryEntity ose join ose.userEntity ue where ue in (:user) and ose.deleted = false")
    List<OrderStoryEntity> getStoryOrdersByUser(@Param("user") UserEntity user);
}