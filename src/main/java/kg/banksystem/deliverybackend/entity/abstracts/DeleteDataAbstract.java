package kg.banksystem.deliverybackend.entity.abstracts;

import kg.banksystem.deliverybackend.entity.TimedEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public abstract class DeleteDataAbstract extends TimedEntity {
    @Column(name = "isDeleted", nullable = true)
    private Boolean isDeleted;

    @Column(name = "delete_date_time", nullable = true)
    private Date deleteDateTime;
}