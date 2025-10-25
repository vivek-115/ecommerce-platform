package com.example.ecomDemo.Repositry;

import com.example.ecomDemo.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepo extends JpaRepository<Cart,Integer> {
    @Query("SELECT c FROM Cart c where c.user.email=?1")
    Cart findCartByEmail(String email);

    @Query("SELECT c FROM Cart c where c.user.email=?1 AND c.cartId=?2")
    Cart findCartByEmailAndCartId(String emailId, int cartId);

    @Query("SELECT c FROM Cart c JOIN FETCH c.cartItems ci JOIN FETCH ci.product p WHERE p.id = ?1")
    List<Cart> findCartsByProductId(Integer productId);

}
