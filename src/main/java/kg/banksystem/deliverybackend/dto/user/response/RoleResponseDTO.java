package kg.banksystem.deliverybackend.dto.user.response;

import kg.banksystem.deliverybackend.entity.Role;
import lombok.Data;

@Data
public class RoleResponseDTO {
    private String name;

    public static RoleResponseDTO roleData(Role role) {
        RoleResponseDTO roleResponseDTO = new RoleResponseDTO();
        switch (role.getName()) {
            case "ADMIN":
                roleResponseDTO.setName("Администратор");
                break;
            case "BANK_EMPLOYEE":
                roleResponseDTO.setName("Сотрудник банка");
                break;
            case "BRANCH_EMPLOYEE":
                roleResponseDTO.setName("Сотрудник филиала");
                break;
            case "COURIER":
                roleResponseDTO.setName("Курьер");
                break;
            default:
                roleResponseDTO.setName("Нет роли");
                break;
        }
        return roleResponseDTO;
    }
}