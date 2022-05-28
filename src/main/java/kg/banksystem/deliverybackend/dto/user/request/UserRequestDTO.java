package kg.banksystem.deliverybackend.dto.user.request;

import lombok.Data;

@Data
public class UserRequestDTO {
    private Long id;
    private String username;
    private String password;
    private String userFullName;
    private String userPhoneNumber;
    private String email;
    private Long role;
    private Long branches;
}