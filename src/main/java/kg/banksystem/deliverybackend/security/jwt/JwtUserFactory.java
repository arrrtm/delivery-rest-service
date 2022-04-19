package kg.banksystem.deliverybackend.security.jwt;

import kg.banksystem.deliverybackend.entity.Role;
import kg.banksystem.deliverybackend.entity.User;
import kg.banksystem.deliverybackend.enums.UserStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

public final class JwtUserFactory {

    public static JwtUser create(User user) {
        return new JwtUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getUserFullName(),
                user.getUserPhoneNumber(),
                user.getEmail(),
                user.getStatus().equals(UserStatus.ACTIVE),
                Collections.singleton(mapToGrantedAuthorities(user.getRole())),
                user.getBranch(),
                user.getUpdated()
        );
    }

    private static SimpleGrantedAuthority mapToGrantedAuthorities(Role userRole) {
        if (userRole == null) {
            return null;
        } else {
            return new SimpleGrantedAuthority(userRole.getName());
        }
    }
}