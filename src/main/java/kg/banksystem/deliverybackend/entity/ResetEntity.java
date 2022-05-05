package kg.banksystem.deliverybackend.entity;

import kg.banksystem.deliverybackend.entity.base.IdentifyBase;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "email_reset")
public class ResetEntity extends IdentifyBase {

    @Column(name = "gmail_login")
    private String gmailLogin;

    @Column(name = "gmail_password")
    private String gmailPassword;
}