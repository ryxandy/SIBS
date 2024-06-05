package com.cocus.manage.service;

import com.cocus.manage.OrderStatus;
import com.cocus.manage.model.Item;
import com.cocus.manage.model.Order;
import com.cocus.manage.model.StockMovement;
import com.cocus.manage.repository.OrderRepository;
import com.cocus.manage.repository.StockMovementRepository;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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

    @Autowired
    private JavaMailSender mailSender;


    private static final Logger logger = (Logger) LogManager.getLogger(OrderService.class);

    public Order createOrder(Order order) {
        order.setCreationDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

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

    void fulfillOrder(Order order) {
        order.setStatus(OrderStatus.FULFILLED);
        updateStock(order.getItem(), order.getQuantity());
        sendOrderCompletionEmail(order);
        logOrderCompletion(order);
    }


    private void sendOrderCompletionEmail(Order order) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(order.getUser().getEmail());
        message.setSubject("Order Completed");
        message.setText("Your order for " + order.getQuantity() + " " + order.getItem().getName() + " is complete.");

        mailSender.send(message);
    }

    private void updateStock(Item item, int orderQuantity) {
        List<StockMovement> stockMovements = stockMovementRepository.findByItemIdOrderByCreationDateAsc(item.getId());

        int remainingQuantityToReduce = orderQuantity;

        for (StockMovement stockMovement : stockMovements) {
            if (remainingQuantityToReduce <= 0) {
                break;
            }

            int availableQuantity = stockMovement.getQuantity();
            if (availableQuantity <= remainingQuantityToReduce) {
                remainingQuantityToReduce -= availableQuantity;
                stockMovement.setQuantity(0);
            } else {
                stockMovement.setQuantity(availableQuantity - remainingQuantityToReduce);
                remainingQuantityToReduce = 0;
            }

            stockMovementRepository.save(stockMovement);
        }

        if (remainingQuantityToReduce > 0) {
            throw new IllegalStateException("Estoque insuficiente para o item: " + item.getName());
        }
    }


    private void logOrderCompletion(Order order) {
        logger.info("Order completed: " + order);
    }
}