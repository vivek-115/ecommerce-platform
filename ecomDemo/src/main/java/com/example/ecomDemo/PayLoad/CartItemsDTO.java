package com.example.ecomDemo.PayLoad;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemsDTO {

    private Integer productId;
    private Integer quantity;


//    private CartDTO cartDTO;
//    private ProductDTO productDTO;
//    private double discount;
//    private double productPrice;
}
