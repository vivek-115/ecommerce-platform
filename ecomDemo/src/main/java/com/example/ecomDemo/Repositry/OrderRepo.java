package com.example.ecomDemo.Repositry;

import com.example.ecomDemo.Entity.Address;
import com.example.ecomDemo.Entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepo extends JpaRepository<Orders, Integer> {
    boolean existsByAddress(Address address);
}
