package com.cocus.manage.service;

import com.cocus.manage.OrderStatus;
import com.cocus.manage.model.Item;
import com.cocus.manage.model.Order;
import com.cocus.manage.model.StockMovement;
import com.cocus.manage.model.User;
import com.cocus.manage.repository.ItemRepository;
import com.cocus.manage.repository.OrderRepository;
import com.cocus.manage.repository.StockMovementRepository;
import com.cocus.manage.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private static final Logger logger = LogManager.getLogger(OrderService.class);

    public Order createOrder(Order order) {
        // Verificar se o item e o usuário existem
        Optional<Item> itemOptional = itemRepository.findById(order.getItem().getId());
        Optional<User> userOptional = userRepository.findById(order.getUser().getId());
        if (!itemOptional.isPresent() || !userOptional.isPresent()) {
            throw new IllegalArgumentException("Item or User not found");
        }

        // Definir a instância gerenciada do item e usuário
        order.setItem(itemOptional.get());
        order.setUser(userOptional.get());

        // Verificar se o email do usuário não é nulo
        if (order.getUser().getEmail() == null || order.getUser().getEmail().isEmpty()) {
            throw new IllegalArgumentException("User email is null or empty");
        }

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

    public void fulfillOrder(Order order) {
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
        if (item == null) {
            throw new IllegalStateException("Item cannot be null");
        }

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