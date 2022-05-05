package kg.banksystem.deliverybackend.repository;

import kg.banksystem.deliverybackend.entity.ClientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<ClientEntity, Long> {
    ClientEntity findClientEntityById(Long clientId);

    @NonNull
    @Override
    @Query("select ce from ClientEntity ce where ce.deleted = false")
    Page<ClientEntity> findAll(@NonNull Pageable pageable);

    @NonNull
    @Override
    @Query("select ce from ClientEntity ce where ce.deleted = false and ce.id = ?1")
    Optional<ClientEntity> findById(@NonNull Long clientId);
}