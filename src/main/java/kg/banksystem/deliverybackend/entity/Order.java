package kg.banksystem.deliverybackend.entity;

import kg.banksystem.deliverybackend.entity.abstracts.OrderAbstract;
import kg.banksystem.deliverybackend.enums.DeliveryType;
import kg.banksystem.deliverybackend.enums.OrderStatus;
import lombok.Data;

import javax.persistence.*;
import java.util.Collection;

@Data
@Entity
@Table(name = "orders")
public class Order extends OrderAbstract {

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type")
    private DeliveryType typeDelivery;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @ManyToMany
    private Collection<User> users;
}