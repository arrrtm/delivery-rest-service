package kg.banksystem.deliverybackend.dto.order.response;

import kg.banksystem.deliverybackend.entity.Currency;
import lombok.Data;

@Data
public class CurrencyResponseDTO {
    private String name;

    public static CurrencyResponseDTO currencyData(Currency currency) {
        CurrencyResponseDTO currencyResponseDTO = new CurrencyResponseDTO();
        currencyResponseDTO.setName(currency.getName());
        return currencyResponseDTO;
    }
}