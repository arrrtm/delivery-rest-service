package kg.banksystem.deliverybackend.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "email_reset")
public class ResetEntity extends BaseEntity {

    @Column(name = "gmail_login")
    private String gmailLogin;

    @Column(name = "gmail_password")
    private String gmailPassword;
}