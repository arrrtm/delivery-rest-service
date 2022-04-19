package kg.banksystem.deliverybackend.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "clients")
public class Client extends BaseEntity {

    @Column(name = "client_pin", unique = true, nullable = false)
    private String clientPin;

    @Column(name = "client_full_name")
    private String clientFullName;

    @Column(name = "client_phone_number")
    private String clientPhoneNumber;
}