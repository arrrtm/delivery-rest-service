package kg.banksystem.deliverybackend.service;

import kg.banksystem.deliverybackend.entity.BranchEntity;
import kg.banksystem.deliverybackend.entity.RoleEntity;
import kg.banksystem.deliverybackend.entity.UserEntity;

import java.util.List;

public interface UserService {

    List<UserEntity> getAllUsers(int page);

    List<UserEntity> getAllUsers(String role, int page);

    UserEntity findUserById(Long userId);

    Long completeDeliveryByUserId(Long userId);

    boolean blockUser(Long userId);

    boolean unblockUser(Long userId);

    // IN PROGRESS
    boolean registerUser(UserEntity userEntity, RoleEntity roleEntity, BranchEntity branchEntity);

    // IN PROGRESS
    boolean updateUser(UserEntity userEntity, RoleEntity roleEntity, BranchEntity branchEntity);

    // IN PROGRESS
    boolean removeUser(Long userId);

    String authCheck(String username);

    boolean authAttemptReset(String username);

    int userPageCalculation(int page);

    UserEntity findByUsername(String username);
}