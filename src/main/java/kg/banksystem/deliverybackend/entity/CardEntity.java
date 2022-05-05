package kg.banksystem.deliverybackend.entity;

import kg.banksystem.deliverybackend.entity.base.ModifyBase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Collection;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "cards")
public class CardEntity extends ModifyBase {

    @Column(name = "type_card")
    private String typeCard;

    @Column(name = "description")
    private String description;

    @ManyToMany
    @ToString.Exclude
    private Collection<CurrencyEntity> currencyEntities;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CardEntity cardEntity = (CardEntity) o;
        return getId() != null && Objects.equals(getId(), cardEntity.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}