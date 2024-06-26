package com.cocus.manage.service;
import com.cocus.manage.enums.OrderStatusEnum;
import com.cocus.manage.model.Item;
import com.cocus.manage.model.Order;
import com.cocus.manage.model.StockMovement;
import com.cocus.manage.repository.ItemRepository;
import com.cocus.manage.repository.OrderRepository;
import com.cocus.manage.repository.StockMovementRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StockMovementService {

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Transactional
    public StockMovement createStockMovement(StockMovement stockMovement) {
        stockMovement.setCreationDate(LocalDateTime.now());

        // Verificar se o item existe no banco de dados
        Optional<Item> itemOptional = itemRepository.findById(stockMovement.getItem().getId());
        if (!itemOptional.isPresent()) {
            throw new IllegalArgumentException("Item with ID " + stockMovement.getItem().getId() + " does not exist.");
        }

        // Definir a instância gerenciada do item
        stockMovement.setItem(itemOptional.get());

        StockMovement savedStockMovement = stockMovementRepository.save(stockMovement);

        // Atribuir o novo movimento de estoque a pedidos pendentes
        fulfillPendingOrders(stockMovement);

        return savedStockMovement;
    }

    private void fulfillPendingOrders(StockMovement stockMovement) {
        List<Order> pendingOrders = orderRepository.findByItemAndStatus(stockMovement.getItem(), OrderStatusEnum.PENDING);
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
                // Parcialmente cumpre o pedido, se não há estoque suficiente
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
