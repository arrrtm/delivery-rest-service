package kg.banksystem.deliverybackend.entity.base;

import kg.banksystem.deliverybackend.entity.BranchEntity;
import kg.banksystem.deliverybackend.entity.CardEntity;
import kg.banksystem.deliverybackend.entity.ClientEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@Getter
@Setter
@MappedSuperclass
public abstract class OrderAbstract extends ModifyBase {

    @Column(name = "address_pickup")
    private String addressPickup;

    @Column(name = "address_delivery")
    private String addressDelivery;

    @ManyToOne
    private CardEntity cardEntity;

    @ManyToOne
    private ClientEntity clientEntity;

    @ManyToOne
    private BranchEntity branchEntity;
}