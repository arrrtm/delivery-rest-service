package kg.banksystem.deliverybackend.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "currencies")
public class Currency extends BaseEntity {

    @Column(name = "name", unique = true)
    private String name;
}