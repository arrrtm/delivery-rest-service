package kg.banksystem.deliverybackend.dto.user.request;

import kg.banksystem.deliverybackend.dto.admin.request.BranchRequestDTO;
import kg.banksystem.deliverybackend.dto.user.response.RoleResponseDTO;
import lombok.Data;

import java.util.List;

@Data
public class UserRequestDTO {
    private Long id;
    private String username;
    private String userFullName;
    private String userPhoneNumber;
    private String email;
    private RoleResponseDTO role;
    private List<BranchRequestDTO> branches;
}