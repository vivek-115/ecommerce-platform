package com.example.ecomDemo.Controllers;

import com.example.ecomDemo.Entity.Cart;
import com.example.ecomDemo.PayLoad.CartDTO;
import com.example.ecomDemo.PayLoad.CartItemsDTO;
import com.example.ecomDemo.Repositry.CartRepo;
import com.example.ecomDemo.Service.CartService;
import com.example.ecomDemo.Utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private CartRepo cartRepo;

    @PostMapping("/carts/create")
    public ResponseEntity<String> createOrUpdateCart(@RequestBody List<CartItemsDTO> cartItems){
        String response=cartService.createOrUpdateCartWithItems(cartItems);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Integer productId, @PathVariable Integer quantity ){
        CartDTO cartDTO = cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<>(cartDTO, HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getCarts(){
      List<CartDTO> cartDTOS=  cartService.getAllCarts();
      return new ResponseEntity<>(cartDTOS,HttpStatus.FOUND);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCartById(){
        String emailId= authUtil.loggedInEmail();
        Cart cart=cartRepo.findCartByEmail(emailId);
        int cartId=cart.getCartId();
     CartDTO cartDTO=   cartService.getCart(emailId, cartId);
     return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Integer productId,
                                                     @PathVariable String operation) {

        CartDTO cartDTO = cartService.updateProductQuantityInCart(productId,
                operation.equalsIgnoreCase("delete") ? -1 : 1);

        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
    }

    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Integer cartId,
                                                        @PathVariable Integer productId) {
        String status = cartService.deleteProductFromCart(cartId, productId);

        return new ResponseEntity<String>(status, HttpStatus.OK);
    }


}
