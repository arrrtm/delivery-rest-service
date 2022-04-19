package kg.banksystem.deliverybackend.service;

import kg.banksystem.deliverybackend.entity.Branch;

import java.util.List;

public interface BranchService {

    Long findBranchIdByUserId(Long id);

    List<Branch> getAllBranches();

    Branch findById(Long id);

    // in progress
    Branch addBranch(Branch branch);

    // in progress
    Branch editBranch(Branch branch);

    // in progress
    void deleteBranch(Long id);
}