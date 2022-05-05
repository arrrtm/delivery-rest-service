package kg.banksystem.deliverybackend.security.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kg.banksystem.deliverybackend.entity.BranchEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

public class JwtUser implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final String userFullName;
    private final String userPhoneNumber;
    private final String email;
    private final boolean status;
    private final Collection<? extends GrantedAuthority> role;
    private final Collection<BranchEntity> branchEntityEntities;
    private final LocalDateTime lastPasswordResetDate;

    public JwtUser(Long id, String username, String password, String userFullName,
                   String userPhoneNumber, String email, boolean status, Collection<? extends GrantedAuthority> role,
                   Collection<BranchEntity> branchEntityEntities, LocalDateTime lastPasswordResetDate) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.userFullName = userFullName;
        this.userPhoneNumber = userPhoneNumber;
        this.email = email;
        this.status = status;
        this.role = role;
        this.branchEntityEntities = branchEntityEntities;
        this.lastPasswordResetDate = lastPasswordResetDate;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return status;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role;
    }

    public Collection<BranchEntity> getBranch() {
        return branchEntityEntities;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }
}