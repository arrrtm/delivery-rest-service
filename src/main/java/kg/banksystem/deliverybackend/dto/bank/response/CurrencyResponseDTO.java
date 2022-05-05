package kg.banksystem.deliverybackend.dto.bank.response;

import kg.banksystem.deliverybackend.entity.CurrencyEntity;
import lombok.Data;

@Data
public class CurrencyResponseDTO {
    private String name;

    public static CurrencyResponseDTO currencyData(CurrencyEntity currencyEntity) {
        CurrencyResponseDTO currency = new CurrencyResponseDTO();
        currency.setName(currencyEntity.getName());
        return currency;
    }
}