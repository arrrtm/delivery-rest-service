package kg.banksystem.deliverybackend.service;

import kg.banksystem.deliverybackend.entity.OrderStoryEntity;

import java.util.List;

public interface OrderStoryService {

    // IN PROGRESS
    List<OrderStoryEntity> getAllOrderStory();

    List<OrderStoryEntity> getOrderStoryByCourierId(Long userId);

    OrderStoryEntity findOrderStoryById(Long orderId);
}