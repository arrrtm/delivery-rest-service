package kg.banksystem.deliverybackend.repository;

import kg.banksystem.deliverybackend.entity.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<CardEntity, Long> {
}