package kg.banksystem.deliverybackend.repository;

import kg.banksystem.deliverybackend.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
}