package com.cocus.manage.service;
import com.cocus.manage.model.Order;
import com.cocus.manage.model.StockMovement;
import com.cocus.manage.repository.OrderRepository;
import com.cocus.manage.repository.StockMovementRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockMovementService {

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Transactional
    public StockMovement createStockMovement(StockMovement stockMovement) {
        stockMovement.setCreationDate(LocalDateTime.now());
        StockMovement savedStockMovement = stockMovementRepository.save(stockMovement);
        fulfillPendingOrders(stockMovement);

        return savedStockMovement;
    }

    private void fulfillPendingOrders(StockMovement stockMovement) {
        List<Order> pendingOrders = orderRepository.findByItemAndStatus(stockMovement.getItem(), "PENDING");
        int remainingStock = stockMovement.getQuantity();

        for (Order order : pendingOrders) {
            if (remainingStock <= 0) {
                break;
            }

            int orderQuantity = order.getQuantity();
            if (orderQuantity <= remainingStock) {
                remainingStock -= orderQuantity;
                orderService.fulfillOrder(order);
            } else {
                reduceOrderQuantity(order, orderQuantity - remainingStock);
                remainingStock = 0;
            }
        }

        if (remainingStock > 0) {
            stockMovement.setQuantity(remainingStock);
        } else {
            stockMovement.setQuantity(0);
        }

        stockMovementRepository.save(stockMovement);
    }

    private void reduceOrderQuantity(Order order, int newQuantity) {
        order.setQuantity(newQuantity);
        orderRepository.save(order);
    }
}
