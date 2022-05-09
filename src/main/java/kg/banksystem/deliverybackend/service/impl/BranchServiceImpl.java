package kg.banksystem.deliverybackend.service.impl;

import kg.banksystem.deliverybackend.dto.admin.request.BranchRequestDTO;
import kg.banksystem.deliverybackend.entity.BranchEntity;
import kg.banksystem.deliverybackend.repository.BranchRepository;
import kg.banksystem.deliverybackend.repository.OrderRepository;
import kg.banksystem.deliverybackend.repository.OrderStoryRepository;
import kg.banksystem.deliverybackend.repository.UserRepository;
import kg.banksystem.deliverybackend.service.BranchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderStoryRepository orderStoryRepository;

    @Autowired
    public BranchServiceImpl(BranchRepository branchRepository, OrderRepository orderRepository, UserRepository userRepository, OrderStoryRepository orderStoryRepository) {
        this.branchRepository = branchRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderStoryRepository = orderStoryRepository;
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

    @Override
    public List<Map<String, Object>> getStatistics() {
        List<BranchEntity> branchEntities = branchRepository.findAll().stream().filter(branch -> !branch.isDeleted()).collect(Collectors.toList());
        if (branchEntities.isEmpty()) {
            log.error("Branches is empty!");
            return null;
        }
        List<Map<String, Object>> elementsStatistics = new ArrayList<>();
        Map<String, Object> statistics;
        try {
            log.info("Count of branches for statistics: {}", branchEntities.size());
            for (BranchEntity branch : branchEntities) {
                statistics = new HashMap<>();
                statistics.put("branchName", branch.getName());
                statistics.put("branchAddress", branch.getAddress());
                statistics.put("totalCountOfOrdersActive", orderRepository.totalCountOfOrdersActive(branch.getId()));
                statistics.put("totalCountOfOrdersComplete", orderStoryRepository.totalCountOfOrdersComplete(branch.getId()));
                statistics.put("totalCountOfCouriersActive", userRepository.totalCountOfCouriersActive(branch.getId()));
                statistics.put("totalCountOfCouriersInactive", userRepository.totalCountOfCouriersInactive(branch.getId()));
                statistics.put("countOfCompletedOrdersPerWeek", orderStoryRepository.countOfCompletedOrdersPerWeek(branch.getId()));
                statistics.put("countOfCompletedOrdersPerMonth", orderStoryRepository.countOfCompletedOrdersPerMonth(branch.getId()));
                statistics.put("countOfCompletedOrdersPerYear", orderStoryRepository.countOfCompletedOrdersPerYear(branch.getId()));
                log.info("Branch stats: {}", statistics);
                elementsStatistics.add(statistics);
            }
            return elementsStatistics;
        } catch (NullPointerException npe) {
            log.error("Statistics is empty!");
            return null;
        }
    }
}