package kg.banksystem.deliverybackend.service.impl;

import kg.banksystem.deliverybackend.entity.BranchEntity;
import kg.banksystem.deliverybackend.entity.OrderStoryEntity;
import kg.banksystem.deliverybackend.entity.UserEntity;
import kg.banksystem.deliverybackend.repository.BranchRepository;
import kg.banksystem.deliverybackend.repository.OrderStoryRepository;
import kg.banksystem.deliverybackend.repository.UserRepository;
import kg.banksystem.deliverybackend.service.OrderStoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderStoryServiceImpl implements OrderStoryService {

    private final OrderStoryRepository orderStoryRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;

    @Autowired
    public OrderStoryServiceImpl(OrderStoryRepository orderStoryRepository, UserRepository userRepository, BranchRepository branchRepository) {
        this.orderStoryRepository = orderStoryRepository;
        this.userRepository = userRepository;
        this.branchRepository = branchRepository;
    }

    @Override
    public List<OrderStoryEntity> getAllOrderStory(int page, Long orderNumber, Long branchId, Long courierId) {
        if (orderNumber != null) {
            try {
                return orderStoryRepository.findAllByBranch(PageRequest.of(page, 5, Sort.by("updatedDate").descending()), orderNumber).getContent();
            } catch (NullPointerException npe) {
                log.error("No orders story found.");
                return null;
            }
        } else if (branchId != null) {
            try {
                return orderStoryRepository.findAllByBranchId(PageRequest.of(page, 5, Sort.by("updatedDate").descending()), branchId).getContent();
            } catch (NullPointerException npe) {
                log.error("No orders story found.");
                return null;
            }
        } else if (courierId != null) {
            try {
                return orderStoryRepository.findAllByCourier(PageRequest.of(page, 5, Sort.by("updatedDate").descending()), courierId).getContent();
            } catch (NullPointerException npe) {
                log.error("No orders story found.");
                return null;
            }
        } else {
            Page<OrderStoryEntity> orderStoryEntities = orderStoryRepository.findAll(PageRequest.of(page, 5, Sort.by("updatedDate").descending()));
            if (orderStoryEntities == null) {
                log.error("No orders story found.");
                return null;
            } else {
                log.info("{} orders story successfully found.", orderStoryEntities.getContent().size());
                return orderStoryEntities.getContent();
            }
        }
    }

    @Override
    public List<OrderStoryEntity> getOrderStoryByCourierId(Long userId) {
        List<OrderStoryEntity> orders = orderStoryRepository.getStoryOrdersByUser(userRepository.getById(userId));
        if (orders.isEmpty()) {
            log.error("No Orders story found by Courier with userId: {}.", userId);
            return null;
        } else {
            log.info("Orders story: {} successfully found by Courier with userId: {}.", orders, userId);
            return orders;
        }
    }

    @Override
    public OrderStoryEntity findOrderStoryById(Long orderId) {
        OrderStoryEntity orderStoryEntity = orderStoryRepository.findById(orderId).orElse(null);
        if (orderStoryEntity == null) {
            log.error("No Order story found by orderId: {}.", orderId);
            return null;
        } else {
            log.info("Order story: {} successfully found by orderId: {}.", orderStoryEntity, orderId);
            return orderStoryEntity;
        }
    }

    @Override
    public List<OrderStoryEntity> getAllOrderStoryForBranch(Long userId, int page, Long orderNumber, Long courierId) {
        if (orderNumber != null) {
            try {
                return orderStoryRepository.findAllByBranch(PageRequest.of(page, 5, Sort.by("updatedDate").descending()), orderNumber).getContent();
            } catch (NullPointerException npe) {
                log.error("No orders story for branch found.");
                return null;
            }
        } else if (courierId != null) {
            try {
                return orderStoryRepository.findAllByCourier(PageRequest.of(page, 5, Sort.by("updatedDate").descending()), courierId).stream()
                        .filter(orderStory -> orderStory.getUserEntity().getId().equals(courierId))
                        .collect(Collectors.toList());
            } catch (NullPointerException npe) {
                log.error("No orders story for branch found.");
                return null;
            }
        } else {
            Page<OrderStoryEntity> orderStoryEntities = orderStoryRepository.findAll(PageRequest.of(page, 5, Sort.by("updatedDate").descending()));
            if (orderStoryEntities == null) {
                log.error("No orders story for branch found.");
                return null;
            } else {
                Long branchId = branchRepository.findBranchIdByUserId(userId);
                log.info("{} orders story for branch successfully found.", orderStoryEntities.getContent().size());
                return orderStoryEntities.getContent().stream()
                        .filter(orderStory -> orderStory.getBranchEntity().getId().equals(branchId))
                        .collect(Collectors.toList());
            }
        }
    }

    @Override
    public int orderStoryPageCalculation(int page, Long userId, Long courierId) {
        Long branchId = branchRepository.findBranchIdByUserId(userId);
        Page<OrderStoryEntity> orderStoryEntities;
        if (branchId != null) {
            orderStoryEntities = orderStoryRepository.findAllByBranchId(PageRequest.of(page, 5, Sort.by("updatedDate").descending()), branchId);
        } else if (courierId != null) {
            orderStoryEntities = orderStoryRepository.findAllByCourier(PageRequest.of(page, 5, Sort.by("updatedDate").descending()), courierId);
        } else {
            orderStoryEntities = orderStoryRepository.findAll(PageRequest.of(page, 5, Sort.by("updatedDate").descending()));
        }
        return orderStoryEntities.getTotalPages();
    }

    @Override
    public List<UserEntity> getCouriersByBranch(Long userId) {
        Long branchId = branchRepository.findBranchIdByUserId(userId);
        BranchEntity branch = branchRepository.findById(branchId).orElse(null);
        return userRepository.findAll().stream().
                filter(user -> !user.isDeleted()).
                filter(user -> user.getRoleEntity().getName().equals("COURIER")).
                filter(courier -> courier.getBranchEntities().contains(branch)).
                collect(Collectors.toList());
    }
}