package kg.banksystem.deliverybackend.entity;

import kg.banksystem.deliverybackend.entity.base.ModifyBase;
import kg.banksystem.deliverybackend.enums.UserStatus;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity extends ModifyBase {

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
    private RoleEntity roleEntity;

    @ManyToMany(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Collection<BranchEntity> branchEntities;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserEntity userEntity = (UserEntity) o;
        return getId() != null && Objects.equals(getId(), userEntity.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}