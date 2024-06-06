package com.cocus.manage.repository;

import com.cocus.manage.enums.OrderStatusEnum;
import com.cocus.manage.model.Item;
import com.cocus.manage.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByItemAndStatus(Item item, OrderStatusEnum status);
}
