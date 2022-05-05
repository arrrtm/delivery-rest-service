package kg.banksystem.deliverybackend.service.impl;

import kg.banksystem.deliverybackend.entity.OrderStoryEntity;
import kg.banksystem.deliverybackend.repository.OrderStoryRepository;
import kg.banksystem.deliverybackend.repository.UserRepository;
import kg.banksystem.deliverybackend.service.OrderStoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OrderStoryServiceImpl implements OrderStoryService {

    private final OrderStoryRepository orderStoryRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderStoryServiceImpl(OrderStoryRepository orderStoryRepository, UserRepository userRepository) {
        this.orderStoryRepository = orderStoryRepository;
        this.userRepository = userRepository;
    }

    // IN PROGRESS
    @Override
    public List<OrderStoryEntity> getAllOrderStory() {
        return null;
    }

    @Override
    public List<OrderStoryEntity> getOrderStoryByCourierId(Long userId) {
        List<OrderStoryEntity> orders = orderStoryRepository.getStoryOrdersByUser(userRepository.getById(userId));
        if (orders.isEmpty()) {
            log.error("No Story Orders found by Courier with userId: {}.", userId);
            return null;
        } else {
            log.info("Story Orders: {} successfully found by Courier with userId: {}.", orders, userId);
            return orders;
        }
    }

    @Override
    public OrderStoryEntity findOrderStoryById(Long orderId) {
        OrderStoryEntity orderStoryEntity = orderStoryRepository.findById(orderId).orElse(null);
        if (orderStoryEntity == null) {
            log.error("No Order Story found by orderId: {}.", orderId);
            return null;
        } else {
            log.info("Order Story: {} successfully found by orderId: {}.", orderStoryEntity, orderId);
            return orderStoryEntity;
        }
    }
}