package kg.banksystem.deliverybackend.dto.user.response;

import kg.banksystem.deliverybackend.entity.RoleEntity;
import lombok.Data;

@Data
public class RoleResponseDTO {
    private Long id;
    private String name;

    public static RoleResponseDTO roleData(RoleEntity roleEntity) {
        RoleResponseDTO roleResponseDTO = new RoleResponseDTO();
        roleResponseDTO.setId(roleEntity.getId());
        switch (roleEntity.getName()) {
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