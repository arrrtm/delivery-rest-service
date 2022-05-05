package kg.banksystem.deliverybackend.entity;

import kg.banksystem.deliverybackend.entity.base.OrderAbstract;
import kg.banksystem.deliverybackend.enums.DeliveryCompleteStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "stories")
public class OrderStoryEntity extends OrderAbstract {

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DeliveryCompleteStatus status;

    @ManyToOne
    private UserEntity userEntity;

    @Column(name = "comment")
    private String comment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        OrderStoryEntity that = (OrderStoryEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}