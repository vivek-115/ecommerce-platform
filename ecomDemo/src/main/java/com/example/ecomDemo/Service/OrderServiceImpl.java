package com.example.ecomDemo.Service;

import com.example.ecomDemo.Entity.*;
import com.example.ecomDemo.Exception.APIException;
import com.example.ecomDemo.Exception.ResourceNotFoundException;
import com.example.ecomDemo.PayLoad.OrderDTO;
import com.example.ecomDemo.PayLoad.OrderItemDTO;
import com.example.ecomDemo.Repositry.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements  OrderService{

    @Autowired
    private CartRepo cartRepo;
    
    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private AddressRepo addressRepo;
    
   @Autowired
   private OrderItemsRepo orderItemsRepo;

   @Autowired
   private ProductRepo productRepo;
    
    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private CartService cartService;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    @Transactional
    public OrderDTO placeOrder(String email, String paymentMethod, Integer addressId, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {
        Cart cart = cartRepo.findCartByEmail(email);
        if(cart==null){
            throw new ResourceNotFoundException("Cart with Email Id: "+email+" does not exists");
        }
       Address address= addressRepo.findById(addressId)
               .orElseThrow(()->new ResourceNotFoundException("Address with Address ID: "+addressId+" does not exists"));


        Orders order =new Orders();
        order.setEmail(email);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted !!!");
        order.setAddress(address);

        Payment payment=new Payment(paymentMethod, pgName,pgPaymentId,pgStatus,pgResponseMessage);
        payment.setOrder(order);
        paymentRepo.save(payment);
        
        order.setPayment(payment);
       Orders savedOrder= orderRepo.save(order);

        List<CartItem> cartItems = cart.getCartItems();
        if(cartItems.isEmpty()){
            throw new APIException(" Cart is Empty");
        }
        
        List<OrderItems> orderItems=new ArrayList<>();
        
        for(CartItem cartItem:cartItems){
            OrderItems orderItem=new OrderItems();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProduct_Price());
            orderItem.setOrders(savedOrder);
            orderItems.add(orderItem);
        }
         orderItems = orderItemsRepo.saveAll(orderItems);

        cart.getCartItems().forEach(item->{
            int quantity= item.getQuantity();

            //reducing stock from the product DB as the Order is Placed
            Product product=item.getProduct();

            ///saving the updated/Reduced stock in the DB(product DB)
            productRepo.save(product);
            product.setQuantity(product.getQuantity()-quantity);


            //Once the Order is placed; need to remove items from the cart
            cartService.deleteProductFromCart(cart.getCartId(), item.getProduct().getProductId());


        });
     OrderDTO orderDTO=   modelMapper.map(savedOrder,OrderDTO.class);

        orderItems.forEach(item -> orderDTO.getOrderItems().add(modelMapper.map(item, OrderItemDTO.class)));

        orderDTO.setAddressId(addressId);

        return orderDTO;
    }
}
