package kg.banksystem.deliverybackend.repository;

import kg.banksystem.deliverybackend.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findClientById(Long id);
}