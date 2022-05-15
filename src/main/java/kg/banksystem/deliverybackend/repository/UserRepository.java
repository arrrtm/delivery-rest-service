package kg.banksystem.deliverybackend.repository;

import kg.banksystem.deliverybackend.entity.BranchEntity;
import kg.banksystem.deliverybackend.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query(value = "SELECT COUNT(id) FROM stories WHERE stories.user_entity_id = :userId", nativeQuery = true)
    Long completeDeliveryByUserId(Long userId);

    @NonNull
    @Override
    @Query("select ue from UserEntity ue where ue.deleted = false")
    Page<UserEntity> findAll(@NonNull Pageable pageable);

    @NonNull
    @Override
    @Query("select ue from UserEntity ue where ue.deleted = false and ue.id = ?1")
    Optional<UserEntity> findById(@NonNull Long userId);

    @Query("select ue from UserEntity ue where ue.deleted = false and ue.roleEntity.name = ?1")
    Page<UserEntity> findByRoleName(String role, Pageable pageable);

    UserEntity findByUsername(String name);

    @Query("select count(ue) from UserEntity ue join ue.branchEntities be where ue.status = 'ACTIVE' and ue.roleEntity.name = 'COURIER' and ue.deleted = false and be.id = ?1")
    Long totalCountOfCouriersActive(Long branchId);

    @Query("select count(ue) from UserEntity ue join ue.branchEntities be where ue.roleEntity.name = 'COURIER' and (ue.status = 'BANNED' or ue.deleted = true) and be.id = ?1")
    Long totalCountOfCouriersInactive(Long branchId);

    @Query("select ue from UserEntity ue join ue.branchEntities be where ue.status = 'ACTIVE' and ue.roleEntity.name = 'COURIER' and ue.deleted = false and be = ?1")
    List<UserEntity> findAllByBranch(BranchEntity branch);
}