package kg.banksystem.deliverybackend.entity;

import kg.banksystem.deliverybackend.entity.base.OrderAbstract;
import kg.banksystem.deliverybackend.enums.DeliveryType;
import kg.banksystem.deliverybackend.enums.OrderStatus;
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
@Table(name = "orders")
public class OrderEntity extends OrderAbstract {

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type")
    private DeliveryType typeDelivery;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @ManyToMany
    @ToString.Exclude
    private Collection<UserEntity> userEntities;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        OrderEntity that = (OrderEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}