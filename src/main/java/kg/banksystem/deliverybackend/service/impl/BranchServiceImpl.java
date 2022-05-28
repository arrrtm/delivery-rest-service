package kg.banksystem.deliverybackend.service.impl;

import kg.banksystem.deliverybackend.dto.admin.request.BranchRequestDTO;
import kg.banksystem.deliverybackend.entity.BranchEntity;
import kg.banksystem.deliverybackend.entity.OrderEntity;
import kg.banksystem.deliverybackend.entity.OrderStoryEntity;
import kg.banksystem.deliverybackend.entity.UserEntity;
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
    public List<BranchEntity> getBranches() {
        return branchRepository.findAll()
                .stream().filter(branch -> !branch.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getStatistics() {
        List<BranchEntity> branchEntities = branchRepository.findAll().stream().filter(branch -> !branch.isDeleted()).collect(Collectors.toList());
        if (branchEntities.isEmpty()) {
            log.error("Branches is empty!");
            return null;
        }
        List<Map<String, Object>> elementsStatistics = new ArrayList<>();
        try {
            return statsData(elementsStatistics, branchEntities);
        } catch (NullPointerException npe) {
            log.error("Statistics is empty!");
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> getReport(String branchName, String period) {
        BranchEntity branch = branchRepository.findBranchEntityByName(branchName);
        if (branch == null) {
            log.error("Branches is empty!");
            return null;
        }
        List<OrderEntity> orderEntities;
        switch (period) {
            case "За неделю":
                orderEntities = orderRepository.findAllByBranch(branch.getId(), 7).stream().filter(order -> !order.isDeleted()).collect(Collectors.toList());
                break;
            case "За месяц":
                orderEntities = orderRepository.findAllByBranch(branch.getId(), 30).stream().filter(order -> !order.isDeleted()).collect(Collectors.toList());
                break;
            case "За год":
                orderEntities = orderRepository.findAllByBranch(branch.getId(), 365).stream().filter(order -> !order.isDeleted()).collect(Collectors.toList());
                break;
            default:
                log.error("Incorrect period!");
                return null;
        }
        if (orderEntities.isEmpty()) {
            log.error("Orders is empty!");
            return null;
        }
        List<Map<String, Object>> elementsReport = new ArrayList<>();
        Map<String, Object> report;
        try {
            for (OrderEntity order : orderEntities) {
                report = new HashMap<>();
                report.put("orderNumber", order.getId());
                report.put("addressPickup", order.getAddressPickup());
                report.put("addressDelivery", order.getAddressDelivery());
                report.put("typeDelivery", order.getTypeDelivery());
                report.put("orderStatus", order.getStatus());
                report.put("orderCreated", order.getCreatedDate());
                report.put("cardType", order.getCardEntity().getTypeCard());
                report.put("cardDescription", order.getCardEntity().getDescription());
                report.put("clientPin", order.getClientEntity().getClientPin());
                report.put("clientFullName", order.getClientEntity().getClientFullName());
                report.put("clientPhoneNumber", order.getClientEntity().getClientPhoneNumber());
                OrderStoryEntity orderStory;
                switch (period) {
                    case "За неделю":
                        orderStory = orderStoryRepository.findStoryByOrder(order.getBranchEntity().getId(), order.getId(), 7);
                        break;
                    case "За месяц":
                        orderStory = orderStoryRepository.findStoryByOrder(order.getBranchEntity().getId(), order.getId(), 30);
                        break;
                    case "За год":
                        orderStory = orderStoryRepository.findStoryByOrder(order.getBranchEntity().getId(), order.getId(), 365);
                        break;
                    default:
                        log.error("Incorrect period!");
                        return null;
                }
                if (orderStory == null) {
                    log.debug("Order Story for Report not found!");
                    report.put("completedStoryNumber", "Нет");
                    report.put("userFullName", "—");
                    report.put("userPhoneNumber", "—");
                    report.put("userEmail", "—");
                    report.put("orderCompleted", order.getUpdatedDate());
                    report.put("storyStatus", "—");
                    report.put("storyComment", "—");
                    report.put("branchName", order.getBranchEntity().getName());
                    report.put("branchAddress", order.getBranchEntity().getAddress());
                } else {
                    log.info("Order Story for Report successfully found!");
                    report.put("completedStoryNumber", "Да");
                    report.put("userFullName", orderStory.getUserEntity().getUserFullName());
                    report.put("userPhoneNumber", orderStory.getUserEntity().getUserPhoneNumber());
                    report.put("userEmail", orderStory.getUserEntity().getEmail());
                    report.put("orderCompleted", orderStory.getUpdatedDate());
                    report.put("storyStatus", orderStory.getStatus());
                    report.put("storyComment", orderStory.getComment());
                    report.put("branchName", orderStory.getBranchEntity().getName());
                    report.put("branchAddress", orderStory.getBranchEntity().getAddress());
                }
                log.info("Branch report: {}", report);
                elementsReport.add(report);
            }
            return elementsReport;
        } catch (NullPointerException npe) {
            log.error("Report is empty!");
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> getStatisticByBranch(Long userId) {
        Long branchId = branchRepository.findBranchIdByUserId(userId);
        List<BranchEntity> branchEntities = branchRepository.findAllByBranchId(branchId);
        if (branchEntities.isEmpty()) {
            log.error("Branch not found!");
            return null;
        }
        List<Map<String, Object>> elementsStatistics = new ArrayList<>();
        try {
            return statsData(elementsStatistics, branchEntities);
        } catch (NullPointerException npe) {
            log.error("Statistic is empty!");
            return null;
        }
    }

    @Override
    public BranchEntity getBranchByUserId(Long user_id) {
        Long branchId = branchRepository.findBranchIdByUserId(user_id);
        if (branchId == null) {
            log.error("Branch not found.");
            return null;
        } else {
            BranchEntity branch = branchRepository.findById(branchId).orElse(null);
            if (branch == null) {
                log.error("Branch not found.");
                return null;
            } else {
                log.info("Branch successfully found.");
                return branch;
            }
        }
    }

    @Override
    public List<Map<String, Object>> getCourierStatisticByBranch(Long userId) {
        BranchEntity branch = getBranchByUserId(userId);
        List<Map<String, Object>> elementsCouriersStatistic = new ArrayList<>();
        List<UserEntity> userEntities = userRepository.findAllByBranch(branch);
        try {
            log.info("Count of couriers for statistics: {}", userEntities.size());
            for (UserEntity user : userEntities) {
                Map<String, Object> statistics = new HashMap<>();
                statistics.put("courierFullName", user.getUserFullName());
                statistics.put("courierPhoneNumber", user.getUserPhoneNumber());
                statistics.put("countOfCompletedOrdersPerDay", orderStoryRepository.countOfCompletedOrdersPerDayByCourierId(user.getId()));
                statistics.put("countOfCompletedOrdersPerWeek", orderStoryRepository.countOfCompletedOrdersPerWeekByCourierId(user.getId()));
                statistics.put("countOfCompletedOrdersPerMonth", orderStoryRepository.countOfCompletedOrdersPerMonthByCourierId(user.getId()));
                if (orderStoryRepository.lastOrderDateByCourierId(user.getId()) == null) {
                    statistics.put("lastOrderDate", "—");
                } else {
                    statistics.put("lastOrderDate", orderStoryRepository.lastOrderDateByCourierId(user.getId()));
                }
                log.info("Courier stats: {}", statistics);
                elementsCouriersStatistic.add(statistics);
            }
            return elementsCouriersStatistic;
        } catch (NullPointerException npe) {
            log.error("Statistics is empty!");
            return null;
        }
    }

    public List<Map<String, Object>> statsData(List<Map<String, Object>> elementsStatistics, List<BranchEntity> branchEntities) {
        log.info("Count of branches for statistics: {}", branchEntities.size());
        for (BranchEntity branch : branchEntities) {
            Map<String, Object> statistics = new HashMap<>();
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
    }
}