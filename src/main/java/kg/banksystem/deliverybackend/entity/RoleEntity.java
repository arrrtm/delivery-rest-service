package kg.banksystem.deliverybackend.entity;

import kg.banksystem.deliverybackend.entity.base.IdentifyBase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "roles")
public class RoleEntity extends IdentifyBase {

    @Column(name = "name", unique = true)
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RoleEntity roleEntity = (RoleEntity) o;
        return getId() != null && Objects.equals(getId(), roleEntity.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}