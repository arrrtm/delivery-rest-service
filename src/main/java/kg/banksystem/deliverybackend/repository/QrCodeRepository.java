package kg.banksystem.deliverybackend.repository;

import kg.banksystem.deliverybackend.entity.QrCodeTemporaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface QrCodeRepository extends JpaRepository<QrCodeTemporaryEntity, Long> {
    QrCodeTemporaryEntity findByOrderIdAndUserId(Long orderId, Long userId);

    @Query("select qrte.path from QrCodeTemporaryEntity qrte where qrte.orderId = ?1")
    Optional<String> getQrName(Long orderId);
}