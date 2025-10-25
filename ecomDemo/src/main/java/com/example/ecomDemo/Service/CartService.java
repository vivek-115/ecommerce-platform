package com.example.ecomDemo.Service;

import com.example.ecomDemo.PayLoad.CartDTO;
import com.example.ecomDemo.PayLoad.CartItemsDTO;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CartService {
   CartDTO addProductToCart(Integer productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getCart(String emailId, int cartId);

    @Transactional
    CartDTO updateProductQuantityInCart(Integer productId, Integer quantity);

    String deleteProductFromCart(Integer cartId, Integer productId);

 void updateProductInCarts(Integer cartId, Integer productId);

 String createOrUpdateCartWithItems(List<CartItemsDTO> cartItems);
}
