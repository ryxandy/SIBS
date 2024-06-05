package com.cocus.manage.repository;

import com.cocus.manage.model.Item;
import com.cocus.manage.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
