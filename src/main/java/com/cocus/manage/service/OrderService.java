package com.cocus.manage.service;

import com.cocus.manage.model.Order;
import com.cocus.manage.model.StockMovement;
import com.cocus.manage.repository.OrderRepository;
import com.cocus.manage.repository.StockMovementRepository;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository;


    private static final Logger logger = (Logger) LogManager.getLogger(OrderService.class);

    public Order createOrder(Order order) {
        order.setCreationDate(LocalDateTime.now());
        order.setStatus("PENDING");


        return orderRepository.save(order);
    }

    public int calculateAvailableStock(Long itemId) {
        int availableStock = 0;
        List<StockMovement> stockMovements = stockMovementRepository.findByItemId(itemId);

        for (StockMovement stockMovement : stockMovements) {
            availableStock += stockMovement.getQuantity();
        }

        return availableStock;
    }
}