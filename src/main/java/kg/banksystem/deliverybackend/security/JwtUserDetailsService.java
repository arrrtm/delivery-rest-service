package kg.banksystem.deliverybackend.security;

import kg.banksystem.deliverybackend.entity.UserEntity;
import kg.banksystem.deliverybackend.security.jwt.JwtAuthenticationException;
import kg.banksystem.deliverybackend.security.jwt.JwtUserFactory;
import kg.banksystem.deliverybackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public JwtUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userService.findByUsername(username);
        if (userEntity == null) {
            return null;
        } else {
            try {
                return JwtUserFactory.create(userEntity);
            } catch (JwtAuthenticationException e) {
                return null;
            }
        }
    }
}