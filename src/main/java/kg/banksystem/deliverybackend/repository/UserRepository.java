package kg.banksystem.deliverybackend.repository;

import kg.banksystem.deliverybackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT COUNT(id) FROM stories WHERE stories.user_id = :id", nativeQuery = true)
    Long completeDeliveryByUserId(Long id);

    User findByUsername(String name);

    List<User> findByRole_Name(String role);
}