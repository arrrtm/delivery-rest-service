package kg.banksystem.deliverybackend.service.impl;

import kg.banksystem.deliverybackend.entity.Branch;
import kg.banksystem.deliverybackend.repository.BranchRepository;
import kg.banksystem.deliverybackend.service.BranchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    @Autowired
    public BranchServiceImpl(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Override
    public Long findBranchIdByUserId(Long id) {
        Long branch = branchRepository.findBranchIdByUserId(id);
        if (branch == null) {
            log.error("Branch ID not found by User ID: {}", id);
            return null;
        } else {
            log.info("Branch ID: {} found by User ID: {}", branch, id);
            return branch;
        }
    }

    @Override
    public List<Branch> getAllBranches() {
        List<Branch> result = branchRepository.findAll();
        log.info("{} branches found.", result.size());
        return result;
    }

    @Override
    public Branch findById(Long id) {
        Branch branch = branchRepository.findById(id).orElse(null);
        if (branch == null) {
            log.error("No branch found by ID: {}", id);
            return null;
        } else {
            log.info("Branch: {} found by ID: {}", branch, id);
            return branch;
        }
    }

    @Override
    public Branch addBranch(Branch branch) {
        return null;
    }

    @Override
    public Branch editBranch(Branch branch) {
        return null;
    }

    @Override
    public void deleteBranch(Long id) {
    }
}