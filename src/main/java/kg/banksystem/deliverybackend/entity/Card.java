package kg.banksystem.deliverybackend.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Collection;

@Data
@Entity
@Table(name = "cards")
public class Card extends BaseEntity {

    @Column(name = "type_card")
    private String typeCard;

    @Column(name = "description")
    private String description;

    @ManyToMany
    private Collection<Currency> currencies;
}