package kg.banksystem.deliverybackend.dto.auth;

import lombok.Data;

@Data
public class AuthenticationRequestDTO {
    private String username;
    private String password;
}