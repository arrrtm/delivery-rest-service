package kg.banksystem.deliverybackend.service.impl;

import kg.banksystem.deliverybackend.dto.admin.request.BranchRequestDTO;
import kg.banksystem.deliverybackend.entity.BranchEntity;
import kg.banksystem.deliverybackend.repository.BranchRepository;
import kg.banksystem.deliverybackend.service.BranchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    @Autowired
    public BranchServiceImpl(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Override
    public Long findBranchIdByUserId(Long userId) {
        Long branch = branchRepository.findBranchIdByUserId(userId);
        if (branch == null) {
            log.error("Branch Id not found by User with userId: {}", userId);
            return null;
        } else {
            log.info("Branch Id: {} successfully found by User with userId: {}", branch, userId);
            return branch;
        }
    }

    @Override
    public List<BranchEntity> getAllBranches(int page) {
        Page<BranchEntity> branchEntities = branchRepository.findAll(PageRequest.of(page, 5, Sort.by("updatedDate").descending()));
        log.info("{} branches found.", branchEntities.getContent().size());
        return branchEntities.getContent();
    }

    @Override
    public BranchEntity getBranchById(Long branchId) {
        BranchEntity branch = branchRepository.findById(branchId).orElse(null);
        if (branch == null) {
            log.error("No branch found by branchId: {}.", branchId);
            return null;
        } else {
            log.info("Branch: {} successfully found by branchId: {}.", branch, branchId);
            return branch;
        }
    }

    @Override
    public boolean addBranch(BranchRequestDTO branchRequestDTO) {
        try {
            BranchEntity branch = new BranchEntity(branchRequestDTO.getName(), branchRequestDTO.getAddress());
            branch.setCreatedDate(LocalDateTime.now());
            branch.setUpdatedDate(LocalDateTime.now());
            branchRepository.save(branch);
            log.info("Branch: {} was successfully added.", branchRequestDTO.getName());
            return true;
        } catch (Exception ex) {
            log.error("Branch: {} was not added.", branchRequestDTO.getName());
            System.out.println(ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean editBranch(BranchRequestDTO branchRequestDTO) {
        BranchEntity branch = getBranchById(branchRequestDTO.getId());
        if (branch == null) {
            log.error("Branch with branchId: {} was not found.", branchRequestDTO.getId());
            return false;
        } else {
            try {
                branch.setName(branchRequestDTO.getName());
                branch.setAddress(branchRequestDTO.getAddress());
                branch.setUpdatedDate(LocalDateTime.now());
                branchRepository.save(branch);
                log.info("Branch: {} was successfully updated.", branchRequestDTO.getName());
                return true;
            } catch (Exception ex) {
                log.error("Branch: {} was not updated.", branchRequestDTO.getName());
                System.out.println(ex.getMessage());
                return false;
            }
        }
    }

    @Override
    public boolean deleteBranch(BranchRequestDTO branchRequestDTO) {
        BranchEntity branch = getBranchById(branchRequestDTO.getId());
        if (branch == null) {
            log.error("Branch with branchId: {} was not found.", branchRequestDTO.getId());
            return false;
        } else {
            try {
                branch.setUpdatedDate(LocalDateTime.now());
                branch.setDeletedDate(LocalDateTime.now());
                branch.setDeleted(true);
                branchRepository.save(branch);
                log.info("Branch with branchId: {} was successfully deleted. It can be viewed in the database.", branchRequestDTO.getId());
                return true;
            } catch (Exception ex) {
                log.error("Branch with branchId: {} was not deleted.", branchRequestDTO.getId());
                System.out.println(ex.getMessage());
                return false;
            }
        }
    }

    @Override
    public int branchPageCalculation(int page) {
        Page<BranchEntity> branchEntities = branchRepository.findAll(PageRequest.of(page, 5, Sort.by("updatedDate").descending()));
        return branchEntities.getTotalPages();
    }

    @Override
    public List<String> getBranchNames() {
        List<BranchEntity> branchEntities = branchRepository.findAll()
                .stream().filter(branch -> !branch.isDeleted())
                .collect(Collectors.toList());
        List<String> names = new ArrayList<>();
        for (BranchEntity branch : branchEntities) {
            names.add(branch.getName());
        }
        return names;
    }
}