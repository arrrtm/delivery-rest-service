package kg.banksystem.deliverybackend.service;

import kg.banksystem.deliverybackend.entity.Branch;
import kg.banksystem.deliverybackend.entity.Role;
import kg.banksystem.deliverybackend.entity.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    List<User> getAllUsers(String role);

    User findByUsername(String username);

    User findById(Long id);

    Long completeDeliveryByUserId(Long id);

    boolean blockUser(Long id);

    boolean unblockUser(Long id);

    // in progress
    User registerUser(User user, Role role, Branch branch);

    // in progress
    User editUser(User user, Role role, Branch branch);

    // in progress
    void deleteUser(Long id);

    String authCheck(String username);

    boolean authAttemptReset(String username);
}