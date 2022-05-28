package kg.banksystem.deliverybackend;

import kg.banksystem.deliverybackend.dto.user.request.ResetPasswordRequestDTO;
import kg.banksystem.deliverybackend.dto.user.request.UserRequestDTO;
import kg.banksystem.deliverybackend.entity.RoleEntity;
import kg.banksystem.deliverybackend.entity.UserEntity;
import kg.banksystem.deliverybackend.repository.UserRepository;
import kg.banksystem.deliverybackend.service.AccountService;
import kg.banksystem.deliverybackend.service.BranchService;
import kg.banksystem.deliverybackend.service.OrderService;
import kg.banksystem.deliverybackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

@Slf4j
@SpringBootTest
class DeliveryBackendApplicationTests {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserRepository userRepository;
    private final AccountService accountService;
    private final BranchService branchService;
    private final OrderService orderService;

    @Autowired
    DeliveryBackendApplicationTests(AuthenticationManager authenticationManager, UserService userService, UserRepository userRepository, AccountService accountService, BranchService branchService, OrderService orderService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.userRepository = userRepository;
        this.accountService = accountService;
        this.branchService = branchService;
        this.orderService = orderService;
    }

    @Test
    public void authorizationAdmin() {
        String username = "artem";
        String password = "Qlvo4zbgMhkawp2a8xYF";
        try {
            boolean user = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)).isAuthenticated();
            Assertions.assertTrue(user);
            log.info("Пользователь с логином: {} успешно авторизован. (Администратор системы)", username);
        } catch (AuthenticationException e) {
            log.error("Пользователь не найден.");
        }
    }

    @Test
    public void authorizationBank() {
        String username = "berembek";
        String password = "baOdKHZhl845Vd2RoOuX";
        try {
            boolean user = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)).isAuthenticated();
            Assertions.assertTrue(user);
            log.info("Пользователь с логином: {} успешно авторизован. (Сотрудник банка)", username);
        } catch (AuthenticationException e) {
            log.error("Пользователь не найден.");
        }
    }

    @Test
    public void authorizationBranch() {
        String username = "nikita";
        String password = "4HH1S5Me94gTVgpEvSVC";
        try {
            boolean user = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)).isAuthenticated();
            Assertions.assertTrue(user);
            log.info("Пользователь с логином: {} успешно авторизован. (Сотрудник филиала)", username);
        } catch (AuthenticationException e) {
            log.error("Пользователь не найден.");
        }
    }

    @Test
    public void authorizationCourier() {
        String username = "adilaga";
        String password = "nNI9dB5JzF0KhG6lnGau";
        try {
            boolean user = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)).isAuthenticated();
            Assertions.assertTrue(user);
            log.info("Пользователь с логином: {} успешно авторизован. (Курьер)", username);
        } catch (AuthenticationException e) {
            log.error("Пользователь не найден.");
        }
    }

    @Test
    public void authorizationNotFound() {
        String username = "Test123";
        String password = "Test123";
        try {
            boolean user = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)).isAuthenticated();
            Assertions.assertTrue(user);
            log.info("Пользователь с логином: {} успешно авторизован.", username);
        } catch (AuthenticationException e) {
            log.error("Пользователь не найден.");
        }
    }

    @Test
    public void authorizationIncorrectPassword() {
        String username = "artem";
        String password = "111";
        try {
            boolean user = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)).isAuthenticated();
            Assertions.assertTrue(user);
            log.info("Пользователь с логином: {} успешно авторизован.", username);
        } catch (AuthenticationException e) {
            log.error("Неверный логин или пароль.");
        }
    }

    @Test
    public void resetPasswordAdmin() {
        ResetPasswordRequestDTO resetPasswordRequestDTO = new ResetPasswordRequestDTO();
        resetPasswordRequestDTO.setUsername("artem");
        Assertions.assertTrue(accountService.resetPassword(resetPasswordRequestDTO));
        log.info("Пароль успешно сброшен. Проверьте электронную почту.\n" + "(Пользователь веб-приложения – администратор системы)\n");
    }

    @Test
    public void resetPasswordCourier() {
        ResetPasswordRequestDTO resetPasswordRequestDTO = new ResetPasswordRequestDTO();
        resetPasswordRequestDTO.setUsername("adilaga");
        Assertions.assertTrue(accountService.resetPassword(resetPasswordRequestDTO));
        log.info("Пароль успешно сброшен. Проверьте электронную почту.\n" + "(Пользователь мобильного приложения – курьер банка)\n");
    }

    @Test
    public void resetPasswordNotFound() {
        ResetPasswordRequestDTO resetPasswordRequestDTO = new ResetPasswordRequestDTO();
        resetPasswordRequestDTO.setUsername("Test123456");
        Assertions.assertFalse(accountService.resetPassword(resetPasswordRequestDTO));
        log.info("Ошибка сброса пароля. Пользователь с указанным логином не найден в системе.");
    }

    @Test
    public void blockUserByAdmin() {
        String testUser = "victor";
        UserEntity user = userService.findByUsername(testUser);
        Assertions.assertTrue(userService.blockUser(user.getId()));
        log.info("Пользователь {} успешно заблокирован.", user.getUsername());
    }

    @Test
    public void repeatedBlockUserByAdmin() {
        String testUser = "victor";
        UserEntity user = userService.findByUsername(testUser);
        Assertions.assertFalse(userService.blockUser(user.getId()));
        log.info("Пользователь {} уже заблокирован. (повторная блокировка невозможна)", testUser);
    }

    @Test
    public void unblockUserByBankEmployee() {
        String testUser = "victor";
        UserEntity user = userService.findByUsername(testUser);
        Assertions.assertFalse(userService.unblockUser(user.getId()));
        log.info("Пользователь {} успешно разблокирован.", testUser);
    }

    @Test
    public void repeatedUnblockUserByAdmin() {
        String testUser = "victor";
        UserEntity user = userService.findByUsername(testUser);
        Assertions.assertFalse(userService.unblockUser(user.getId()));
        log.info("Пользователь {} не заблокирован. (повторная разблокировка невозможна)", testUser);
    }

    @Test
    public void addUserSuccess() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setUsername("test_22.05");
        userRequestDTO.setPassword("test_22.05");
        userRequestDTO.setUserFullName("Test");
        userRequestDTO.setUserPhoneNumber("0556999777");
        userRequestDTO.setEmail("exp22.05a@gmail.com");
        RoleEntity role = userService.getRoles().get(3);
        userRequestDTO.setRole(role.getId());
        userRequestDTO.setBranches(branchService.getBranchById(1L).getId());
        Assertions.assertTrue(userService.registerUser(userRequestDTO));
        log.info("Новый пользователь {} был успешно зарегистрирован!", userRequestDTO.getUserFullName());
    }

    @Test
    public void repeatedAddUser() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setUsername("test_22.05");
        userRequestDTO.setPassword("test_22.05");
        userRequestDTO.setUserFullName("Test");
        userRequestDTO.setUserPhoneNumber("0556999777");
        userRequestDTO.setEmail("exp22.05a@gmail.com");
        RoleEntity role = userService.getRoles().get(3);
        userRequestDTO.setRole(role.getId());
        userRequestDTO.setBranches(branchService.getBranchById(1L).getId());
        Assertions.assertFalse(userService.registerUser(userRequestDTO));
        log.info("Новый пользователь не был успешно зарегистрирован! Возможно он уже существует. Проверьте данные и повторите попытку ещё раз!");
    }

    @Test
    public void addUserWithEmptyData() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setUsername("test_22.05");
        userRequestDTO.setUserPhoneNumber("0556999777");
        RoleEntity role = userService.getRoles().get(3);
        userRequestDTO.setRole(role.getId());
        userRequestDTO.setBranches(branchService.getBranchById(1L).getId());
        Assertions.assertFalse(userService.registerUser(userRequestDTO));
        log.info("Новый пользователь не был зарегистрирован. Заполните все данные!");
    }

    @Test
    public void addUserWithIncorrectData() {
        try {
            UserRequestDTO userRequestDTO = new UserRequestDTO();
            userRequestDTO.setUsername("test_22.05");
            userRequestDTO.setPassword("test_22.05");
            userRequestDTO.setUserFullName("Test");
            userRequestDTO.setUserPhoneNumber("0556999777");
            userRequestDTO.setEmail("exp22.05a@gmail.com");
            RoleEntity role = userService.getRoles().get(3);
            userRequestDTO.setRole(role.getId());
            userRequestDTO.setBranches(branchService.getBranchById(0L).getId()); // incorrect branch
            Assertions.assertTrue(userService.registerUser(userRequestDTO));
        } catch (NullPointerException npe) {
            log.info("Новый пользователь не был зарегистрирован. Неверные данные!");
        }
    }

    @Test
    public void updateUserSuccess() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setId(31L);
        userRequestDTO.setUsername("test_22.05");
        userRequestDTO.setPassword("test_22.05");
        userRequestDTO.setUserFullName("Test123");
        userRequestDTO.setUserPhoneNumber("0556999123");
        userRequestDTO.setEmail("exp13ax@gmail.com");
        RoleEntity role = userService.getRoles().get(3);
        userRequestDTO.setRole(role.getId());
        userRequestDTO.setBranches(branchService.getBranchById(1L).getId());
        Assertions.assertTrue(userService.updateUser(userRequestDTO));
        log.info("Пользователь {} был успешно обновлён!", userRequestDTO.getUserFullName());
    }

    @Test
    public void updateUserWithIncorrectId() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setId(222L);
        userRequestDTO.setUsername("test_22.05");
        userRequestDTO.setPassword("test_22.05");
        userRequestDTO.setUserFullName("Test123");
        userRequestDTO.setUserPhoneNumber("0556999123");
        userRequestDTO.setEmail("exp13ax@gmail.com");
        RoleEntity role = userService.getRoles().get(3);
        userRequestDTO.setRole(role.getId());
        userRequestDTO.setBranches(branchService.getBranchById(1L).getId());
        Assertions.assertFalse(userService.updateUser(userRequestDTO));
        log.info("Пользователь {} не был обновлён. Неверные данные!", userRequestDTO.getUserFullName());
    }

    @Test
    public void updateUserWithSameLogin() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setId(31L);
        userRequestDTO.setUsername("artem"); // the same login
        userRequestDTO.setPassword("test_22.05");
        userRequestDTO.setUserFullName("Test123");
        userRequestDTO.setUserPhoneNumber("0556999123");
        userRequestDTO.setEmail("exp13ax@gmail.com");
        RoleEntity role = userService.getRoles().get(3);
        userRequestDTO.setRole(role.getId());
        userRequestDTO.setBranches(branchService.getBranchById(1L).getId());
        Assertions.assertFalse(userService.updateUser(userRequestDTO));
        log.info("Пользователь {} не был обновлён, возможно он уже существует. Проверьте данные и повторите попытку ещё раз!", userRequestDTO.getUserFullName());
    }

    @Test
    public void deleteUserSuccess() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setId(31L);
        Assertions.assertTrue(userService.removeUser(userRequestDTO));
        log.info("Пользователь был успешно удалён!");
    }

    @Test
    public void repeatedDeleteUser() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setId(31L);
        Assertions.assertFalse(userService.removeUser(userRequestDTO));
        log.info("Пользователь не был удалён. Проверьте данные и повторите попытку ещё раз!");
    }

    @Test
    public void getAllUsers() {
        Page<UserEntity> users = userRepository.findAll(PageRequest.of(1, 5, Sort.by("updatedDate").descending()));
        Assertions.assertEquals(userService.getAllUsers(1), users.getContent());
        log.info("Пользователи успешно найдены.");
    }

    @Test
    public void getEmptyPageUsers() {
        Page<UserEntity> users = userRepository.findAll(PageRequest.of(3, 5, Sort.by("updatedDate").descending()));
        Assertions.assertEquals(userService.getAllUsers(3), users.getContent());
        log.info("Пользовательские данные не найдены. (Список пользователей пуст)");
    }

    @Test
    public void getQrById() {
        Long orderNumber = 14L;
        Assertions.assertEquals(orderService.getQrUniqueName(orderNumber), "2022-05-24T01-23-49.032.png");
        log.info("QR код для заказа с номером {} успешно найден!", orderNumber);
    }

    @Test
    public void getQrByNonExistentId() {
        Long orderNumber = 144L;
        Assertions.assertNotEquals(orderService.getQrUniqueName(orderNumber), "2022-05-24T01-23-49.032.png");
        log.info("QR код для заказа с номером {} не найден!", orderNumber);
    }

    @Test
    public void invalidQrCodeByUserId() {
        Long courierId = 5L;
        Long orderNumber = 14L;
        Assertions.assertFalse(orderService.validQrCodeOrder(orderNumber, courierId));
        log.info("Не валидный QR код!");
    }

    @Test
    public void invalidQrCodeByOrderId() {
        Long courierId = 4L;
        Long orderNumber = 15L;
        Assertions.assertFalse(orderService.validQrCodeOrder(orderNumber, courierId));
        log.info("Не валидный QR код!");
    }

    @Test
    public void getValidQrCode() {
        Long courierId = 4L;
        Long orderNumber = 14L;
        Assertions.assertTrue(orderService.validQrCodeOrder(orderNumber, courierId));
        log.info("QR код является валидным. Успешное сканирование!");
    }

    @Test
    public void incorrectStatusCompleteOrder() {
        Long courierId = 4L;
        Long orderNumber = 14L;
        String status = "successful...";
        String comment = "Карта успешно передана клиенту";
        Assertions.assertFalse(orderService.completeOrder(courierId, orderNumber, status, comment));
        log.info("Неверный статус запроса!");
    }

    @Test
    public void orderCompletionByUserFromWrongBranch() {
        Long courierId = 4L;
        Long orderNumber = 25L;
        String status = "unable_to_find_client";
        String comment = "";
        Assertions.assertFalse(orderService.completeOrder(courierId, orderNumber, status, comment));
        log.info("Заказ не был завершён успешно. (Курьер с другого филиала)");
    }

    @Test
    public void orderCompletionByUserFromCorrectBranch() {
        Long courierId = 5L;
        Long orderNumber = 25L;
        String status = "unable_to_find_client";
        String comment = "";
        Assertions.assertTrue(orderService.completeOrder(courierId, orderNumber, status, comment));
        log.info("Заказ был успешно завершён курьером.");
    }

    @Test
    public void successfulCompleteOrder() {
        Long courierId = 4L;
        Long orderNumber = 14L;
        String status = "successful_delivery";
        String comment = "Карта успешно передана клиенту";
        Assertions.assertTrue(orderService.completeOrder(courierId, orderNumber, status, comment));
        log.info("Заказ был успешно завершён курьером.");
    }

    @Test
    public void repeatedCompleteOrder() {
        Long courierId = 4L;
        Long orderNumber = 14L;
        String status = "successful_delivery";
        String comment = "Карта успешно передана клиенту";
        Assertions.assertFalse(orderService.completeOrder(courierId, orderNumber, status, comment));
        log.info("Заказ не был завершён успешно. (Повторное завершение недопустимо)");
    }
}