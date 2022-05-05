package kg.banksystem.deliverybackend.security.jwt;

import kg.banksystem.deliverybackend.entity.RoleEntity;
import kg.banksystem.deliverybackend.entity.UserEntity;
import kg.banksystem.deliverybackend.enums.UserStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

public final class JwtUserFactory {

    public static JwtUser create(UserEntity userEntity) {
        return new JwtUser(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.getUserFullName(),
                userEntity.getUserPhoneNumber(),
                userEntity.getEmail(),
                userEntity.getStatus().equals(UserStatus.ACTIVE),
                Collections.singleton(mapToGrantedAuthorities(userEntity.getRoleEntity())),
                userEntity.getBranchEntities(),
                userEntity.getUpdatedDate()
        );
    }

    private static SimpleGrantedAuthority mapToGrantedAuthorities(RoleEntity userRoleEntity) {
        if (userRoleEntity == null) {
            return null;
        } else {
            return new SimpleGrantedAuthority(userRoleEntity.getName());
        }
    }
}