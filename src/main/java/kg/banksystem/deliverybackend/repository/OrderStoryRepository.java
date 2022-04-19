package kg.banksystem.deliverybackend.repository;

import kg.banksystem.deliverybackend.entity.OrderStory;
import kg.banksystem.deliverybackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderStoryRepository extends JpaRepository<OrderStory, Long> {
    @Query("select o from OrderStory o join o.user u where u in (:user)")
    List<OrderStory> getStoryOrdersByUserID(@Param("user") User user);
}