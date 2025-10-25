package com.example.ecomDemo.PayLoad;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {

    private Integer cartId;
    private Double totalPrice=0.0;
    List<ProductDTO> products=new ArrayList<>();
}
