package kg.banksystem.deliverybackend.service;

import kg.banksystem.deliverybackend.dto.admin.request.BranchRequestDTO;
import kg.banksystem.deliverybackend.entity.BranchEntity;

import java.util.List;
import java.util.Map;

public interface BranchService {

    List<BranchEntity> getAllBranches(int page);

    BranchEntity getBranchById(Long branchId);

    boolean addBranch(BranchRequestDTO branchRequestDTO);

    boolean editBranch(BranchRequestDTO branchRequestDTO);

    boolean deleteBranch(BranchRequestDTO branchRequestDTO);

    int branchPageCalculation(int page);

    List<String> getBranchNames();

    List<Map<String, Object>> getStatistics();

    List<Map<String, Object>> getReport(String branchName, String period);

    List<Map<String, Object>> getStatisticByBranch(Long userId);

    BranchEntity getBranchByUserId(Long user_id);

    List<Map<String, Object>> getCourierStatisticByBranch(Long user_id);
}