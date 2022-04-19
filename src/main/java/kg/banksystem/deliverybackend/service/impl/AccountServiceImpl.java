package kg.banksystem.deliverybackend.service.impl;

import kg.banksystem.deliverybackend.dto.user.request.EditAccountRequestDTO;
import kg.banksystem.deliverybackend.dto.user.request.ResetPasswordRequestDTO;
import kg.banksystem.deliverybackend.entity.ResetEntity;
import kg.banksystem.deliverybackend.entity.User;
import kg.banksystem.deliverybackend.repository.ResetEntityRepository;
import kg.banksystem.deliverybackend.repository.UserRepository;
import kg.banksystem.deliverybackend.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
    Calendar calendar;
    Date date;

    @Autowired
    public AccountServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, ResetEntityRepository resetEntityRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.resetEntityRepository = resetEntityRepository;
        this.calendar = new GregorianCalendar();
        this.date = calendar.getTime();
    }

    @Override
    public boolean editAccount(Long id, EditAccountRequestDTO requestDTO) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            log.error("User with ID: {} not found.", id);
            return false;
        } else {
            try {
                user.setUserFullName(requestDTO.getUserFullName());
                user.setUserPhoneNumber(requestDTO.getUserPhoneNumber());
                user.setEmail(requestDTO.getEmail());
                user.setUpdated(date);
                userRepository.save(user);
                log.info("User with ID: {} successfully updated!", id);
                return true;
            } catch (Exception ex) {
                log.info("User with ID: {} cannot be updated.", id);
                return false;
            }
        }
    }

    @Override
    public boolean editPassword(Long id, EditAccountRequestDTO requestDTO) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            log.error("User with ID: {} not found.", id);
            return false;
        } else {
            try {
                user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
                user.setUpdated(date);
                userRepository.save(user);
                log.info("Password for User with ID: {} successfully updated!", id);
                return true;
            } catch (Exception ex) {
                log.info("Password for User with ID: {} cannot be updated.", id);
                return false;
            }
        }
    }

    @Override
    public boolean resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO) {
        String username = resetPasswordRequestDTO.getUsername();
        User user = userRepository.findByUsername(username);
        ResetEntity resetEntity = resetEntityRepository.getById(1L);

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

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(resetEntity.getGmailLogin()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));

            String newUserPassword = RandomPasswordGenerator();
            log.info("New password from User with Username: {} successfully generated.", username);

            message.setSubject("Сброс пароля для пользователя " + user.getUserFullName());
            message.setText("Ваш новый пароль для входа в аккаунт: " + newUserPassword + "\n\nПосле успешной авторизации, рекомендуем изменить пароль в личном кабинете.\n\nДоступ к аккаунту восстановлен!");

            user.setPassword(passwordEncoder.encode(newUserPassword));
            Transport.send(message);
            log.info("Message for User with Username: {} sent successfully.", username);

            userRepository.save(user);
            log.info("Password for User with Username: {} successfully updated.", username);
            return true;

        } catch (AuthenticationFailedException authenticationFailedException) {
            log.error("Mail {} authentication error. Access not restored.", resetEntity.getGmailLogin());
            return false;
        } catch (Exception exception) {
            log.error("Message for User with Username: {} not sent. Access not restored.", username);
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