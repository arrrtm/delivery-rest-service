package kg.banksystem.deliverybackend.service.impl;

import kg.banksystem.deliverybackend.dto.user.request.ResetPasswordRequestDTO;
import kg.banksystem.deliverybackend.dto.user.request.UserRequestDTO;
import kg.banksystem.deliverybackend.entity.BranchEntity;
import kg.banksystem.deliverybackend.entity.RoleEntity;
import kg.banksystem.deliverybackend.entity.UserEntity;
import kg.banksystem.deliverybackend.enums.UserStatus;
import kg.banksystem.deliverybackend.repository.BranchRepository;
import kg.banksystem.deliverybackend.repository.RoleRepository;
import kg.banksystem.deliverybackend.repository.UserRepository;
import kg.banksystem.deliverybackend.service.AccountService;
import kg.banksystem.deliverybackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           BranchRepository branchRepository, PasswordEncoder passwordEncoder, AccountService accountService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.branchRepository = branchRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountService = accountService;
    }

    public List<UserEntity> getAllUsers(int page) {
        Page<UserEntity> userEntities = userRepository.findAll(PageRequest.of(page, 5, Sort.by("updatedDate").descending()));
        log.info("{} users found with different roles.", userEntities.getContent().size());
        return userEntities.getContent();
    }

    @Override
    public List<UserEntity> getAllUsers(String role, int page) {
        if (role.equals("courier")) {
            Page<UserEntity> userEntities = userRepository.findByRoleName("COURIER", PageRequest.of(page, 5, Sort.by("updatedDate").descending()));
            log.info("{} users found with Courier role.", userEntities.getContent().size());
            return userEntities.getContent();
        } else if (role.equals("branch_employee")) {
            Page<UserEntity> userEntities = userRepository.findByRoleName("BRANCH_EMPLOYEE", PageRequest.of(page, 5, Sort.by("updatedDate").descending()));
            log.info("{} users found with Branch Employee role.", userEntities.getContent().size());
            return userEntities.getContent();
        }
        return null;
    }

    @Override
    public UserEntity findUserById(Long userId) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.error("No User found by userId: {}.", userId);
            return null;
        } else {
            log.info("User: {} successfully found by userId: {}.", user, userId);
            return user;
        }
    }

    @Override
    public boolean registerUser(UserRequestDTO userRequestDTO) {
        RoleEntity role = roleRepository.findById(userRequestDTO.getRole()).orElse(null);
        if (role == null) {
            log.error("Role with Id: {} not found.", userRequestDTO.getRole());
            return false;
        }
        Collection<BranchEntity> branch = branchRepository.findAllByBranchId(userRequestDTO.getBranches());
        if (branch.isEmpty()) {
            log.error("Branch with Id: {} not found.", userRequestDTO.getBranches());
            return false;
        }
        try {
            UserEntity user = new UserEntity();
            user.setCreatedDate(LocalDateTime.now());
            user.setUpdatedDate(LocalDateTime.now());
            user.setDeleted(false);
            user.setAttempt(0);
            user.setUsername(userRequestDTO.getUsername());
            user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
            user.setUserFullName(userRequestDTO.getUserFullName());
            user.setUserPhoneNumber(userRequestDTO.getUserPhoneNumber());
            user.setEmail(userRequestDTO.getEmail());
            user.setStatus(UserStatus.ACTIVE);
            user.setRoleEntity(role);
            user.setBranchEntities(branch);
            userRepository.save(user);
            log.info("User: {} successfully registered.", userRequestDTO.getUserFullName());
            return true;
        } catch (Exception ex) {
            log.error("User: {} was not registered.", userRequestDTO.getUserFullName());
            System.out.println(ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateUser(UserRequestDTO userRequestDTO) {
        UserEntity user = findUserById(userRequestDTO.getId());
        if (user == null) {
            log.error("User with userId: {} was not found.", userRequestDTO.getId());
            return false;
        } else {
            try {
                RoleEntity role = roleRepository.findById(userRequestDTO.getRole()).orElse(null);
                if (role == null) {
                    log.error("Role with Id: {} not found.", userRequestDTO.getRole());
                    return false;
                }
                Collection<BranchEntity> branch = branchRepository.findAllByBranchId(userRequestDTO.getBranches());
                if (branch.isEmpty()) {
                    log.error("Branch with Id: {} not found.", userRequestDTO.getBranches());
                    return false;
                }
                user.setUpdatedDate(LocalDateTime.now());
                user.setUsername(userRequestDTO.getUsername());
                user.setUserFullName(userRequestDTO.getUserFullName());
                user.setUserPhoneNumber(userRequestDTO.getUserPhoneNumber());
                user.setEmail(userRequestDTO.getEmail());
                user.setRoleEntity(role);
                user.setBranchEntities(branch);
                userRepository.save(user);
                log.info("User with userId: {} successfully updated", userRequestDTO.getId());
                return true;
            } catch (Exception ex) {
                log.error("User with userId: {} was not updated.", userRequestDTO.getId());
                System.out.println(ex.getMessage());
                return false;
            }
        }
    }

    @Override
    public boolean removeUser(UserRequestDTO userRequestDTO) {
        UserEntity user = findUserById(userRequestDTO.getId());
        if (user == null) {
            log.error("User with userId: {} was not found.", userRequestDTO.getId());
            return false;
        } else {
            try {
                user.setUpdatedDate(LocalDateTime.now());
                user.setDeletedDate(LocalDateTime.now());
                user.setDeleted(true);
                userRepository.save(user);
                log.info("User with userId: {} successfully removed. It can be viewed in the database.", userRequestDTO.getId());
                return true;
            } catch (Exception ex) {
                log.error("User with userId: {} was not removed.", userRequestDTO.getId());
                System.out.println(ex.getMessage());
                return false;
            }
        }
    }

    @Override
    public Long completeDeliveryByUserId(Long userId) {
        Long countDelivery = userRepository.completeDeliveryByUserId(userId);
        if (countDelivery == null) {
            log.error("Order Story of the User with userId: {} is empty.", userId);
            return null;
        } else {
            log.info("Order Story: {} found by User with userId: {}.", countDelivery, userId);
            return countDelivery;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean blockUser(Long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElse(null);
        if (userEntity == null) {
            log.error("No User found by userId: {}.", userId);
            return false;
        } else {
            if (userEntity.getStatus().getValue().equals("Активен")) {
                userEntity.setStatus(UserStatus.BANNED);
                userRepository.save(userEntity);
                log.info("User with userId: {} successfully banned.", userId);
                return true;
            } else {
                log.info("User with userId: {} was not active.", userId);
                return false;
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unblockUser(Long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElse(null);
        if (userEntity == null) {
            log.error("No User found by userId: {}.", userId);
            return false;
        } else {
            if (userEntity.getStatus().getValue().equals("Забанен")) {
                userEntity.setStatus(UserStatus.ACTIVE);
                userRepository.save(userEntity);
                log.info("User with userId: {} successfully unbanned.", userId);
                return true;
            } else {
                log.info("User with userId: {} was not banned.", userId);
                return false;
            }
        }
    }

    @Override
    public String authCheck(String username) {
        UserEntity userEntity = findByUsername(username);
        if (userEntity == null) {
            log.error("No User found by username: {}.", username);
            return "Пользователь не найден.";
        }
        if (userEntity.getStatus().equals(UserStatus.BANNED)) {
            return "Пользователь заблокирован Администратором системы.";
        }
        if (userEntity.getAttempt() > 2) {
            log.error("User account with username: {} has new password.", username);
            ResetPasswordRequestDTO resetPasswordRequestDTO = new ResetPasswordRequestDTO();
            resetPasswordRequestDTO.setUsername(username);
            accountService.resetPassword(resetPasswordRequestDTO);
            userEntity.setAttempt(0);
            userRepository.save(userEntity);
            return "Вы исчерпали лимит попыток авторизации. Проверьте электронную почту для восстановления доступа к аккаунту.";
        } else {
            userEntity.setAttempt(userEntity.getAttempt() + 1);
            userRepository.save(userEntity);
            return "Неверный логин или пароль. Количество оставшихся попыток авторизации: " + (4 - userEntity.getAttempt()) + ". После 4-ёх неудачных попыток пароль будет сброшен.";
        }
    }

    @Override
    public boolean authAttemptReset(String username) {
        UserEntity userEntity = findByUsername(username);
        if (userEntity == null) {
            log.error("No User found by username: {}.", username);
            return false;
        } else {
            userEntity.setAttempt(0);
            userRepository.save(userEntity);
            return true;
        }
    }

    @Override
    public int userPageCalculation(int page) {
        Page<UserEntity> userEntities = userRepository.findAll(PageRequest.of(page, 5, Sort.by("updatedDate").descending()));
        return userEntities.getTotalPages();
    }

    @Override
    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<UserEntity> getCouriers() {
        return userRepository.findAll()
                .stream().filter(user -> !user.isDeleted())
                .filter(user -> user.getRoleEntity().getName().equals("COURIER"))
                .collect(Collectors.toList());
    }

    @Override
    public List<RoleEntity> getRoles() {
        return new ArrayList<>(roleRepository.findAll());
    }
}