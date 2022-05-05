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
@Table(name = "clients")
public class ClientEntity extends ModifyBase {

    @Column(name = "client_pin", unique = true, nullable = false)
    private String clientPin;

    @Column(name = "client_full_name")
    private String clientFullName;

    @Column(name = "client_phone_number")
    private String clientPhoneNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ClientEntity clientEntity = (ClientEntity) o;
        return getId() != null && Objects.equals(getId(), clientEntity.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}