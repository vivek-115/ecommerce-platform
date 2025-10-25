package com.example.ecomDemo.Repositry;

import com.example.ecomDemo.Entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemsRepo extends JpaRepository<CartItem,Integer> {

  @Query("SELECT c from CartItem c where c.cart.cartId=?1 AND c.product.productId=?2")
  CartItem  findByProductIdAndCartId(Integer cartId, Integer productId);

  @Modifying
  @Query("DELETE FROM CartItem c where c.cart.cartId=?1 AND c.product.productId=?2")
  void deleteCartItemByProductIdAndCartId(Integer cartId, Integer productId);

  @Query("SELECT c from CartItem c where c.cart.cartId=?1")
  List<CartItem> findByCartId(Integer cartId);

  @Modifying
  @Query("DELETE FROM CartItem ci WHERE ci.cart.id=?1")
  void deleteAllByCartId(Integer cartId);
}
