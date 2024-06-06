package com.cocus.manage;

import com.cocus.manage.model.StockMovement;
import com.cocus.manage.repository.StockMovementRepository;
import com.cocus.manage.service.StockMovementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StockMovementServiceTest {

    @Mock
    private StockMovementRepository stockMovementRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    void testGetAllStockMovements() {
        StockMovement sm1 = new StockMovement();
        sm1.setQuantity(10);
        StockMovement sm2 = new StockMovement();
        sm2.setQuantity(20);

        when(stockMovementRepository.findAll()).thenReturn(Arrays.asList(sm1, sm2));

        List<StockMovement> result = stockMovementRepository.findAll();

        assertEquals(2, result.size());
        assertEquals(10, result.get(0).getQuantity());
        assertEquals(20, result.get(1).getQuantity());
        verify(stockMovementRepository, times(1)).findAll();
    }

    @Test
    void testGetStockMovementsByItemId() {
        Long itemId = 1L;
        StockMovement sm1 = new StockMovement();
        sm1.setQuantity(10);
        StockMovement sm2 = new StockMovement();
        sm2.setQuantity(20);

        when(stockMovementRepository.findByItemId(itemId)).thenReturn(Arrays.asList(sm1, sm2));

        List<StockMovement> result = stockMovementRepository.findByItemId(itemId);

        assertEquals(2, result.size());
        assertEquals(10, result.get(0).getQuantity());
        assertEquals(20, result.get(1).getQuantity());
        verify(stockMovementRepository, times(1)).findByItemId(itemId);
    }
}
