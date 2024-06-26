package com.cocus.manage.model;

import com.cocus.manage.enums.OrderStatusEnum;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Orders")
@Getter
@Setter
@EqualsAndHashCode
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime creationDate;

    @ManyToOne
    private Item item;

    private Integer quantity;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status;
}

