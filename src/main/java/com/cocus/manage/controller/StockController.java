package com.cocus.manage.controller;

import com.cocus.manage.model.StockMovement;
import com.cocus.manage.repository.StockMovementRepository;
import com.cocus.manage.service.StockMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/stock-movements")
public class StockController {

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private StockMovementService stockMovementService;

    @GetMapping
    public List<StockMovement> getAllStockMovements() {
        return stockMovementRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockMovement> getStockMovementById(@PathVariable Long id) {
        Optional<StockMovement> stockMovement = stockMovementRepository.findById(id);
        if (stockMovement.isPresent()) {
            return ResponseEntity.ok().body(stockMovement.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<StockMovement> createStockMovement(@RequestBody StockMovement stockMovement) {
        try {
            StockMovement createdStockMovement = stockMovementService.createStockMovement(stockMovement);
            return ResponseEntity.status(201).body(createdStockMovement);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockMovement> updateStockMovement(@PathVariable Long id, @RequestBody StockMovement stockMovementDetails) {
        Optional<StockMovement> stockMovement = stockMovementRepository.findById(id);
        if (stockMovement.isPresent()) {
            StockMovement existingStockMovement = stockMovement.get();
            existingStockMovement.setQuantity(stockMovementDetails.getQuantity());
            StockMovement updatedStockMovement = stockMovementRepository.save(existingStockMovement);
            return ResponseEntity.ok().body(updatedStockMovement);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockMovement(@PathVariable Long id) {
        Optional<StockMovement> stockMovement = stockMovementRepository.findById(id);
        if (stockMovement.isPresent()) {
            stockMovementRepository.delete(stockMovement.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
