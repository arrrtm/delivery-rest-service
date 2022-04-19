package kg.banksystem.deliverybackend.dto.order.response;

import kg.banksystem.deliverybackend.entity.Card;
import kg.banksystem.deliverybackend.entity.Currency;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class CardResponseDTO {
    private String typeCard;
    private String Description;
    private Set<CurrencyResponseDTO> currency;

    public static CardResponseDTO cardData(Card card) {
        CardResponseDTO cardResponseDTO = new CardResponseDTO();
        cardResponseDTO.setTypeCard(card.getTypeCard());
        cardResponseDTO.setDescription(card.getDescription());

        Set<CurrencyResponseDTO> currencyResponseDTOS = new HashSet<>();
        for (Currency currency : card.getCurrencies()) {
            currencyResponseDTOS.add(CurrencyResponseDTO.currencyData(currency));
        }
        cardResponseDTO.setCurrency(currencyResponseDTOS);

        return cardResponseDTO;
    }
}