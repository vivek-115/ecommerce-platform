package com.example.ecomDemo.Controllers;

import com.example.ecomDemo.PayLoad.OrderDTO;
import com.example.ecomDemo.PayLoad.OrderRequestDTO;
import com.example.ecomDemo.PayLoad.StripePaymentDTO;
import com.example.ecomDemo.Service.OrderService;
import com.example.ecomDemo.Service.StripeService;
import com.example.ecomDemo.Utils.AuthUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StripeService stripeService;

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProducts(@PathVariable String paymentMethod, @RequestBody OrderRequestDTO orderRequestDTO){
        String email=authUtil.loggedInEmail();
        System.out.println("orderRequestDTo DATA: "+orderRequestDTO);
       OrderDTO order= orderService.placeOrder(
                email,
                paymentMethod,
                orderRequestDTO.getAddressId(),
                orderRequestDTO.getPgName(),
                orderRequestDTO.getPgPaymentId(),
                orderRequestDTO.getPgStatus(),
                orderRequestDTO.getPgResponseMessage()
        );

       return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @PostMapping("/order/stripe-client-secret")
    public ResponseEntity<String> createStripeClientSecret(@RequestBody StripePaymentDTO stripePaymentDTO ) throws StripeException {
        PaymentIntent paymentIntent=stripeService.paymentIntent(stripePaymentDTO);
        return new ResponseEntity<>(paymentIntent.getClientSecret(),HttpStatus.CREATED);
    }

}
