package kg.banksystem.deliverybackend.service;

import kg.banksystem.deliverybackend.dto.user.request.UserRequestDTO;
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

    boolean registerUser(UserRequestDTO userRequestDTO);

    boolean updateUser(UserRequestDTO userRequestDTO);

    boolean removeUser(UserRequestDTO userRequestDTO);

    String authCheck(String username);

    boolean authAttemptReset(String username);

    int userPageCalculation(int page);

    UserEntity findByUsername(String username);

    List<UserEntity> getCouriers();

    List<RoleEntity> getRoles();
}