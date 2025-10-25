package com.example.ecomDemo.Service;

import com.example.ecomDemo.PayLoad.OrderDTO;
import jakarta.transaction.Transactional;

public interface OrderService {

    @Transactional
    OrderDTO placeOrder(String email, String paymentMethod, Integer addressId, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage);
}
