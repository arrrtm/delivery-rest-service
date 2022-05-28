package kg.banksystem.deliverybackend.dto.bank.response;

import kg.banksystem.deliverybackend.entity.CardEntity;
import kg.banksystem.deliverybackend.entity.CurrencyEntity;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class CardResponseDTO {
    private Long id;
    private String typeCard;
    private String description;
    private Set<CurrencyResponseDTO> currency;

    public static CardResponseDTO cardData(CardEntity cardEntity) {
        CardResponseDTO cardResponseDTO = new CardResponseDTO();
        cardResponseDTO.setId(cardEntity.getId());
        cardResponseDTO.setTypeCard(cardEntity.getTypeCard());
        cardResponseDTO.setDescription(cardEntity.getDescription());
        Set<CurrencyResponseDTO> currency = new HashSet<>();
        for (CurrencyEntity currencyEntity : cardEntity.getCurrencyEntities()) {
            currency.add(CurrencyResponseDTO.currencyData(currencyEntity));
        }
        cardResponseDTO.setCurrency(currency);
        return cardResponseDTO;
    }
}