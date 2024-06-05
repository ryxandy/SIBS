package com.cocus.manage.service;

import com.cocus.manage.model.Order;
import com.cocus.manage.model.StockMovement;
import com.cocus.manage.repository.OrderRepository;
import com.cocus.manage.repository.StockMovementRepository;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
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

        int availableStock = calculateAvailableStock(order.getItem().getId());

        if (availableStock >= order.getQuantity()) {
            fulfillOrder(order);
        }

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

    private void fulfillOrder(Order order) {
        order.setStatus("FULFILLED");

        //need to come back here to create the create update the stock method
        logOrderCompletion(order);
    }

    private void sendOrderCompletionEmail(Order order) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(order.getUser().getEmail());
        message.setSubject("Order Completed");
        message.setText("Your order for " + order.getQuantity() + " " + order.getItem().getName() + " is complete.");
    }

    private void logOrderCompletion(Order order) {
        logger.info("Order completed: " + order);
    }
}