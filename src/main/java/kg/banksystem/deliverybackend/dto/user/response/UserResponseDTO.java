package kg.banksystem.deliverybackend.dto.user.response;

import kg.banksystem.deliverybackend.dto.admin.response.BranchResponseDTO;
import kg.banksystem.deliverybackend.entity.BranchEntity;
import kg.banksystem.deliverybackend.entity.UserEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private String userFullName;
    private String userPhoneNumber;
    private String email;
    private String status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private RoleResponseDTO role;
    private List<BranchResponseDTO> branches;

    public static UserResponseDTO userPersonalAccount(UserEntity userEntity) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(userEntity.getId());
        userResponseDTO.setUsername(userEntity.getUsername());
        userResponseDTO.setUserFullName(userEntity.getUserFullName());
        userResponseDTO.setUserPhoneNumber(userEntity.getUserPhoneNumber());
        userResponseDTO.setEmail(userEntity.getEmail());
        userResponseDTO.setStatus(userEntity.getStatus().getValue());
        userResponseDTO.setCreatedDate(userEntity.getCreatedDate());
        userResponseDTO.setUpdatedDate(userEntity.getUpdatedDate());
        userResponseDTO.setRole(RoleResponseDTO.roleData(userEntity.getRoleEntity()));
        List<BranchResponseDTO> branchResponseDTOS = new ArrayList<>();
        for (BranchEntity branchEntity : userEntity.getBranchEntities()) {
            branchResponseDTOS.add(BranchResponseDTO.branchData(branchEntity));
        }
        userResponseDTO.setBranches(branchResponseDTOS);
        return userResponseDTO;
    }
}