package kg.banksystem.deliverybackend.repository;

import kg.banksystem.deliverybackend.entity.BranchEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface BranchRepository extends JpaRepository<BranchEntity, Long> {
    @Query(value = "SELECT users_branch_entities.branch_entities_id FROM users, users_branch_entities WHERE users_branch_entities.user_entity_id = users.id AND users_branch_entities.user_entity_id = :userId", nativeQuery = true)
    Long findBranchIdByUserId(@Param("userId") Long userId);

    @NonNull
    @Override
    @Query("select be from BranchEntity be where be.deleted = false")
    Page<BranchEntity> findAll(@NonNull Pageable pageable);

    @NonNull
    @Override
    @Query("select be from BranchEntity be where be.deleted = false and be.id = ?1")
    Optional<BranchEntity> findById(@NonNull Long branchId);

    @Query("select be from BranchEntity be where be.deleted = false and be.id = ?1")
    List<BranchEntity> findAllByBranchId(Long branchId);

    BranchEntity findBranchEntityByName(String branchName);
}