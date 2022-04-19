package kg.banksystem.deliverybackend.service.impl;

import kg.banksystem.deliverybackend.entity.OrderStory;
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

    @Override
    public List<OrderStory> getAllStoryOrder() {
        return null;
    }

    @Override
    public List<OrderStory> getStoryOrderByCourierID(Long id) {
        List<OrderStory> orders = orderStoryRepository.getStoryOrdersByUserID(userRepository.getById(id));
        if (orders.isEmpty()) {
            log.error("No story orders found by User ID: {}.", id);
            return null;
        } else {
            log.info("Story order: {} found by User ID: {}.", orders, id);
            return orders;
        }
    }

    @Override
    public OrderStory findById(Long id) {
        OrderStory orderStory = orderStoryRepository.findById(id).orElse(null);
        if (orderStory == null) {
            log.error("No order story found by ID: {}.", id);
            return null;
        } else {
            log.info("Order story: {} found by ID: {}.", orderStory, id);
            return orderStory;
        }
    }
}