package kg.banksystem.deliverybackend.service.impl;

import kg.banksystem.deliverybackend.dto.user.request.EditAccountRequestDTO;
import kg.banksystem.deliverybackend.dto.user.request.ResetPasswordRequestDTO;
import kg.banksystem.deliverybackend.entity.ResetEntity;
import kg.banksystem.deliverybackend.entity.UserEntity;
import kg.banksystem.deliverybackend.repository.ResetEntityRepository;
import kg.banksystem.deliverybackend.repository.UserRepository;
import kg.banksystem.deliverybackend.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Properties;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    private static final String CHAR_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPERCASE = CHAR_LOWERCASE.toUpperCase();
    private static final String DIGIT = "0123456789";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResetEntityRepository resetEntityRepository;

    @Autowired
    public AccountServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, ResetEntityRepository resetEntityRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.resetEntityRepository = resetEntityRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean editAccount(Long userId, EditAccountRequestDTO requestDTO) {
        UserEntity userEntity = userRepository.findById(userId).orElse(null);
        if (userEntity == null) {
            log.error("User with userId: {} not found.", userId);
            return false;
        } else {
            try {
                userEntity.setUserFullName(requestDTO.getUserFullName());
                userEntity.setUserPhoneNumber(requestDTO.getUserPhoneNumber());
                userEntity.setEmail(requestDTO.getEmail());
                userEntity.setUpdatedDate(LocalDateTime.now());
                userRepository.save(userEntity);
                log.info("User with userId: {} successfully updated!", userId);
                return true;
            } catch (Exception ex) {
                log.info("User with userId: {} cannot be updated.", userId);
                return false;
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean editPassword(Long userId, EditAccountRequestDTO requestDTO) {
        UserEntity userEntity = userRepository.findById(userId).orElse(null);
        if (userEntity == null) {
            log.error("User with userId: {} not found.", userId);
            return false;
        } else {
            try {
                userEntity.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
                userEntity.setUpdatedDate(LocalDateTime.now());
                userRepository.save(userEntity);
                log.info("Password for User with userId: {} successfully updated!", userId);
                return true;
            } catch (Exception ex) {
                log.info("Password for User with userId: {} cannot be updated.", userId);
                return false;
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO) {
        String username = resetPasswordRequestDTO.getUsername();
        UserEntity userEntity = userRepository.findByUsername(username);
        ResetEntity resetEntity = resetEntityRepository.getById(1L);
        try {
            Properties prop = new Properties();
            prop.put("mail.smtp.host", "smtp.gmail.com");
            prop.put("mail.smtp.port", "587");
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(resetEntity.getGmailLogin(), resetEntity.getGmailPassword());
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(resetEntity.getGmailLogin()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEntity.getEmail()));

            String newUserPassword = RandomPasswordGenerator();
            log.info("New password from User with username: {} successfully generated.", username);
            message.setSubject("Сброс пароля для пользователя " + userEntity.getUserFullName());
            message.setText("Ваш новый пароль для входа в аккаунт: " + newUserPassword + "\n\nПосле успешной авторизации, рекомендуем изменить пароль в личном кабинете.\n\nДоступ к аккаунту восстановлен!");
            userEntity.setPassword(passwordEncoder.encode(newUserPassword));
            Transport.send(message);
            log.info("Message for User with username: {} sent successfully.", username);
            userRepository.save(userEntity);
            log.info("Password for User with username: {} successfully updated.", username);
            return true;

        } catch (AuthenticationFailedException authenticationFailedException) {
            log.error("Mail {} authentication error. Access not restored.", resetEntity.getGmailLogin());
            return false;
        } catch (Exception exception) {
            log.error("Message for User with username: {} not sent. Access not restored.", username);
            return false;
        }
    }

    private String RandomPasswordGenerator() {
        String input = CHAR_LOWERCASE + CHAR_UPPERCASE + DIGIT;
        int size = 20;
        SecureRandom random = new SecureRandom();
        StringBuilder result = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            int index = random.nextInt(input.length());
            result.append(input.charAt(index));
        }
        return result.toString();
    }
}