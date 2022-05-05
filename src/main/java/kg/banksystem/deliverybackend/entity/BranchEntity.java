package kg.banksystem.deliverybackend.entity;

import kg.banksystem.deliverybackend.entity.base.ModifyBase;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "branches")
public class BranchEntity extends ModifyBase {

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "address")
    private String address;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BranchEntity branchEntity = (BranchEntity) o;
        return getId() != null && Objects.equals(getId(), branchEntity.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}