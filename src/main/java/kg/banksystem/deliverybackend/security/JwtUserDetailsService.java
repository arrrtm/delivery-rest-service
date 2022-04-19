package kg.banksystem.deliverybackend.security;

import kg.banksystem.deliverybackend.entity.User;
import kg.banksystem.deliverybackend.security.jwt.JwtAuthenticationException;
import kg.banksystem.deliverybackend.security.jwt.JwtUser;
import kg.banksystem.deliverybackend.security.jwt.JwtUserFactory;
import kg.banksystem.deliverybackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public JwtUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);

        if (user == null) {
            return null;
        }

        try {
            JwtUser jwtUser = JwtUserFactory.create(user);
            log.info("User with username: {} successfully loaded.", username);
            return jwtUser;

        } catch (JwtAuthenticationException e) {
            return null;
        }
    }
}