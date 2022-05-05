package kg.banksystem.deliverybackend.dto.admin.response;

import kg.banksystem.deliverybackend.entity.BranchEntity;
import lombok.Data;

@Data
public class BranchResponseDTO {
    private Long id;
    private String name;
    private String address;

    public static BranchResponseDTO branchData(BranchEntity branchEntity) {
        BranchResponseDTO branchResponseDTO = new BranchResponseDTO();
        branchResponseDTO.setId(branchEntity.getId());
        branchResponseDTO.setName(branchEntity.getName());
        branchResponseDTO.setAddress(branchEntity.getAddress());
        return branchResponseDTO;
    }
}