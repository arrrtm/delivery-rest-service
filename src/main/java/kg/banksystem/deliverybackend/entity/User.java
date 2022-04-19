package kg.banksystem.deliverybackend.entity;

import kg.banksystem.deliverybackend.entity.abstracts.DeleteDataAbstract;
import kg.banksystem.deliverybackend.enums.UserStatus;
import lombok.Data;

import javax.persistence.*;
import java.util.Collection;

@Data
@Entity
@Table(name = "users")
public class User extends DeleteDataAbstract {

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "user_full_name")
    private String userFullName;

    @Column(name = "user_phone_number")
    private String userPhoneNumber;

    @Column(name = "attempt")
    private Integer attempt = 0;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Branch> branch;
}