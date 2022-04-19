package kg.banksystem.deliverybackend.entity;

import kg.banksystem.deliverybackend.entity.abstracts.OrderAbstract;
import kg.banksystem.deliverybackend.enums.DeliveryCompleteStatus;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "stories")
public class OrderStory extends OrderAbstract {

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DeliveryCompleteStatus status;

    @ManyToOne
    private User user;

    @Column(name = "comment")
    private String comment;
}