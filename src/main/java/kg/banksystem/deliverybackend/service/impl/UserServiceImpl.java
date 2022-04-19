package kg.banksystem.deliverybackend.service.impl;

import kg.banksystem.deliverybackend.dto.user.request.ResetPasswordRequestDTO;
import kg.banksystem.deliverybackend.entity.Branch;
import kg.banksystem.deliverybackend.entity.Role;
import kg.banksystem.deliverybackend.entity.User;
import kg.banksystem.deliverybackend.enums.UserStatus;
import kg.banksystem.deliverybackend.repository.RoleRepository;
import kg.banksystem.deliverybackend.repository.UserRepository;
import kg.banksystem.deliverybackend.service.AccountService;
import kg.banksystem.deliverybackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder, AccountService accountService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountService = accountService;
    }

    public List<User> getAllUsers() {
        List<User> result = userRepository.findAll();
        log.info("{} users found with different roles.", result.size());
        return result;
    }

    @Override
    public List<User> getAllUsers(String role) {
        if (role.equals("courier")) {
            List<User> result = userRepository.findByRole_Name("COURIER");
            log.info("{} users found with Courier role.", result.size());
            return result;
        } else if (role.equals("branch_employee")) {
            List<User> result = userRepository.findByRole_Name("BRANCH_EMPLOYEE");
            log.info("{} users found with Branch employee role.", result.size());
            return result;
        }
        return null;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            log.error("No user found by ID: {}", id);
            return null;
        } else {
            log.info("User: {} found by ID: {}", user, id);
            return user;
        }
    }

    @Override
    public Long completeDeliveryByUserId(Long id) {
        Long countDelivery = userRepository.completeDeliveryByUserId(id);
        if (countDelivery == null) {
            log.error("The order story of the user with ID: {} is empty.", id);
            return null;
        } else {
            log.info("The order story: {} found by User ID: {}", countDelivery, id);
            return countDelivery;
        }
    }

    @Override
    public boolean blockUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            log.error("No user found by ID: {}", id);
            return false;
        } else {
            if (user.getStatus().getValue().equals("Активен")) {
                user.setStatus(UserStatus.BANNED);
                userRepository.save(user);
                log.info("User with ID: {} successfully banned.", id);
                return true;
            } else {
                log.info("User with ID: {} was not active.", id);
                return false;
            }
        }
    }

    @Override
    public boolean unblockUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            log.error("No user found by ID: {}", id);
            return false;
        } else {
            if (user.getStatus().getValue().equals("Забанен")) {
                user.setStatus(UserStatus.ACTIVE);
                userRepository.save(user);
                log.info("User with ID: {} successfully unbanned.", id);
                return true;
            } else {
                log.info("User with ID: {} was not banned.", id);
                return false;
            }
        }
    }

    @Override
    public User registerUser(User user, Role role, Branch branch) {
        Role roleUser = roleRepository.findByName("ADMIN");
        List<Role> userRoles = new ArrayList<>();
        userRoles.add(roleUser);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(userRoles.get(1));
        user.setStatus(UserStatus.ACTIVE);
        User registeredUser = userRepository.save(user);

        log.info("User: {} successfully registered.", registeredUser);
        return registeredUser;
    }

    @Override
    public User editUser(User user, Role role, Branch branch) {
        Role roleUser = roleRepository.findByName("ADMIN");
        List<Role> userRoles = new ArrayList<>();
        userRoles.add(roleUser);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(userRoles.get(1));
        user.setStatus(UserStatus.ACTIVE);
        User registeredUser = userRepository.save(user);

        log.info("User: {} successfully registered.", registeredUser);
        return registeredUser;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        log.info("User with ID: {} successfully deleted.", id);
    }

    @Override
    public String authCheck(String username) {
        User user = findByUsername(username);
        if (user == null) {
            log.error("No user found by username: {}", username);
            return "Пользователь не найден.";
        }
        if (user.getStatus().equals(UserStatus.BANNED)) {
            return "Пользователь заблокирован Администратором системы.";
        }
        if (user.getAttempt() > 2) {
            log.error("User account with username: {} has new password.", username);
            ResetPasswordRequestDTO resetPasswordRequestDTO = new ResetPasswordRequestDTO();
            resetPasswordRequestDTO.setUsername(username);
            accountService.resetPassword(resetPasswordRequestDTO);
            user.setAttempt(0);
            userRepository.save(user);
            return "Вы исчерпали лимит попыток авторизации. Проверьте электронную почту для восстановления доступа к аккаунту.";
        } else {
            user.setAttempt(user.getAttempt() + 1);
            userRepository.save(user);
            return "Неверный логин или пароль. Количество оставшихся попыток авторизации: " + (4 - user.getAttempt()) + ". После 4-ёх неудачных попыток пароль будет сброшен.";
        }
    }

    @Override
    public boolean authAttemptReset(String username) {
        User user = findByUsername(username);
        if (user == null) {
            log.error("No user found by username: {}", username);
            return false;
        } else {
            user.setAttempt(0);
            userRepository.save(user);
            return true;
        }
    }
}