package com.example.ecomDemo.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer cartItemId;

    private Integer quantity;
    private Double discount;
    private Double product_Price;

    @ManyToOne
    @JoinColumn(name = "cart_Id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_Id")
    private Product product;
}
