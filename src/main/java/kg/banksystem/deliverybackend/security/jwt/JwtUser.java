package kg.banksystem.deliverybackend.security.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kg.banksystem.deliverybackend.entity.Branch;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;

public class JwtUser implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final String userFullName;
    private final String userPhoneNumber;
    private final String email;
    private final boolean status;
    private final Collection<? extends GrantedAuthority> role;
    private final Collection<Branch> branch;
    private final Date lastPasswordResetDate;

    public JwtUser(Long id, String username, String password, String userFullName,
                   String userPhoneNumber, String email, boolean status, Collection<? extends GrantedAuthority> role,
                   Collection<Branch> branch, Date lastPasswordResetDate) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.userFullName = userFullName;
        this.userPhoneNumber = userPhoneNumber;
        this.email = email;
        this.status = status;
        this.role = role;
        this.branch = branch;
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

    public Collection<Branch> getBranch() {
        return branch;
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

    public Date getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }
}