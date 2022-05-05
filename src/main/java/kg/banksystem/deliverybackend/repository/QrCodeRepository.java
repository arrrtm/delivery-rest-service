package kg.banksystem.deliverybackend.repository;

import kg.banksystem.deliverybackend.entity.QrCodeTemporaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QrCodeRepository extends JpaRepository<QrCodeTemporaryEntity, Long> {
    QrCodeTemporaryEntity findByOrderIdAndUserId(Long orderId, Long userId);
}