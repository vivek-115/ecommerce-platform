package com.example.ecomDemo.Repositry;

import com.example.ecomDemo.Entity.Cart;
import com.example.ecomDemo.Entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemsRepo extends JpaRepository<OrderItems, Integer> {

}
