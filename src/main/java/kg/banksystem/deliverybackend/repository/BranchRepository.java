package kg.banksystem.deliverybackend.repository;

import kg.banksystem.deliverybackend.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    @Query(value = "SELECT users_branch.branch_id FROM users, users_branch " +
            "WHERE users_branch.user_id = users.id AND users_branch.user_id = :id", nativeQuery = true)
    Long findBranchIdByUserId(@Param("id") Long id);
}