package kg.banksystem.deliverybackend.service;

import kg.banksystem.deliverybackend.entity.OrderStory;

import java.util.List;

public interface OrderStoryService {

    // in progress
    List<OrderStory> getAllStoryOrder();

    List<OrderStory> getStoryOrderByCourierID(Long id);

    OrderStory findById(Long id);
}