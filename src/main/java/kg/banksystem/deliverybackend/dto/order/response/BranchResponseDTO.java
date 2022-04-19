package kg.banksystem.deliverybackend.dto.order.response;

import kg.banksystem.deliverybackend.entity.Branch;
import lombok.Data;

@Data
public class BranchResponseDTO {
    private Long id;
    private String name;
    private String address;

    public static BranchResponseDTO branchData(Branch branch) {
        BranchResponseDTO branchResponseDTO = new BranchResponseDTO();
        branchResponseDTO.setId(branch.getId());
        branchResponseDTO.setName(branch.getName());
        branchResponseDTO.setAddress(branch.getAddress());
        return branchResponseDTO;
    }
}