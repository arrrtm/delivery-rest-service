package kg.banksystem.deliverybackend.entity.abstracts;

import kg.banksystem.deliverybackend.entity.Branch;
import kg.banksystem.deliverybackend.entity.Card;
import kg.banksystem.deliverybackend.entity.Client;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@Getter
@Setter
@MappedSuperclass
public abstract class OrderAbstract extends DeleteDataAbstract {

    @Column(name = "address_pickup")
    private String addressPickup;

    @Column(name = "address_delivery")
    private String addressDelivery;

    @ManyToOne
    private Card card;

    @ManyToOne
    private Client client;

    @ManyToOne
    private Branch branch;
}