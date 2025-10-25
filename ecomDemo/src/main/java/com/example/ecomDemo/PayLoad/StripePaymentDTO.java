package com.example.ecomDemo.PayLoad;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StripePaymentDTO {

    private Long amount;
    private String currency;
}
