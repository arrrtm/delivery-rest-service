package kg.banksystem.deliverybackend.dto.user.response;

import kg.banksystem.deliverybackend.dto.order.response.BranchResponseDTO;
import kg.banksystem.deliverybackend.entity.Branch;
import kg.banksystem.deliverybackend.entity.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private String userFullName;
    private String userPhoneNumber;
    private String email;
    private String status;
    private Date created;
    private Date updated;
    private RoleResponseDTO role;
    private List<BranchResponseDTO> branches;

    public static UserResponseDTO userPersonalAccount(User user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(user.getId());
        userResponseDTO.setUsername(user.getUsername());
        userResponseDTO.setUserFullName(user.getUserFullName());
        userResponseDTO.setUserPhoneNumber(user.getUserPhoneNumber());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setStatus(user.getStatus().getValue());
        userResponseDTO.setCreated(user.getCreated());
        userResponseDTO.setUpdated(user.getUpdated());
        userResponseDTO.setRole(RoleResponseDTO.roleData(user.getRole()));

        List<BranchResponseDTO> branchResponseDTOS = new ArrayList<>();
        for (Branch branch : user.getBranch()) {
            branchResponseDTOS.add(BranchResponseDTO.branchData(branch));
        }
        userResponseDTO.setBranches(branchResponseDTOS);
        return userResponseDTO;
    }
}