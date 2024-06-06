package com.cocus.manage;



import com.cocus.manage.enums.OrderStatusEnum;
import com.cocus.manage.model.Item;
import com.cocus.manage.model.Order;
import com.cocus.manage.model.StockMovement;
import com.cocus.manage.model.User;
import com.cocus.manage.repository.ItemRepository;
import com.cocus.manage.repository.OrderRepository;
import com.cocus.manage.repository.StockMovementRepository;
import com.cocus.manage.repository.UserRepository;
import com.cocus.manage.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JavaMailSender mailSender;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalculateAvailableStock() {
        Long itemId = 1L;
        StockMovement sm1 = new StockMovement();
        sm1.setQuantity(10);
        StockMovement sm2 = new StockMovement();
        sm2.setQuantity(15);

        when(stockMovementRepository.findByItemId(itemId)).thenReturn(Arrays.asList(sm1, sm2));

        int availableStock = orderService.calculateAvailableStock(itemId);

        assertEquals(25, availableStock);
    }

    @Test
    void testCreateOrder() {
        Long itemId = 1L;
        Long userId = 1L;
        Item item = new Item();
        item.setId(itemId);
        item.setName("Test Item");
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");

        Order order = new Order();
        order.setItem(item);
        order.setUser(user);
        order.setQuantity(5);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(stockMovementRepository.findByItemId(itemId)).thenReturn(Arrays.asList(new StockMovement(1L,LocalDateTime.now(), item, 10)));

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setStatus(OrderStatusEnum.FULFILLED);
        savedOrder.setQuantity(5);

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Order result = orderService.createOrder(order);

        assertEquals(OrderStatusEnum.FULFILLED, result.getStatus());
    }

    @Test
    void testFulfillOrder() {
        Item item = new Item();
        item.setId(1L);
        User user = new User();
        user.setEmail("test@example.com");

        Order order = new Order();
        order.setItem(item);
        order.setUser(user);
        order.setQuantity(5);
        order.setStatus(OrderStatusEnum.PENDING);

        StockMovement stockMovement = new StockMovement();
        stockMovement.setItem(item);
        stockMovement.setQuantity(10);

        when(stockMovementRepository.findByItemIdOrderByCreationDateAsc(item.getId())).thenReturn(List.of(stockMovement));

        orderService.fulfillOrder(order);

        assertEquals(OrderStatusEnum.FULFILLED, order.getStatus());
        verify(stockMovementRepository, times(1)).save(any(StockMovement.class));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}

