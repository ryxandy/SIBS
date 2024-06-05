package com.cocus.manage.repository;

import com.cocus.manage.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByItemId(Long itemId);
    List<StockMovement> findByItemIdOrderByCreationDateAsc(Long itemId);
}
