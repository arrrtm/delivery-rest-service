package kg.banksystem.deliverybackend.service;

import kg.banksystem.deliverybackend.entity.OrderStoryEntity;
import kg.banksystem.deliverybackend.entity.UserEntity;

import java.util.List;

public interface OrderStoryService {

    List<OrderStoryEntity> getAllOrderStory(int page, Long orderNumber, String branchName, Long courierId);

    List<OrderStoryEntity> getOrderStoryByCourierId(Long userId);

    OrderStoryEntity findOrderStoryById(Long orderId);

    int orderStoryPageCalculation(int page, String branchName, Long courierId);

    List<OrderStoryEntity> getAllOrderStoryForBranch(Long userId, int page, Long orderNumber, Long courierId);

    int orderStoryPageCalculation(Long userId, int page, Long courierId);

    List<UserEntity> getCouriersByBranch(Long userId);
}