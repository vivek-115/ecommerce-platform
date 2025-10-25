package com.example.ecomDemo.Service;

import com.example.ecomDemo.Entity.Cart;
import com.example.ecomDemo.Entity.CartItem;
import com.example.ecomDemo.Entity.Product;
import com.example.ecomDemo.Exception.APIException;
import com.example.ecomDemo.Exception.ResourceNotFoundException;
import com.example.ecomDemo.PayLoad.CartDTO;
import com.example.ecomDemo.PayLoad.CartItemsDTO;
import com.example.ecomDemo.PayLoad.ProductDTO;
import com.example.ecomDemo.Repositry.CartItemsRepo;
import com.example.ecomDemo.Repositry.CartRepo;
import com.example.ecomDemo.Repositry.ProductRepo;
import com.example.ecomDemo.Utils.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements  CartService{

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private CartItemsRepo cartItemsRepo;



    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthUtil authUtil;

    //Add products to the cart
    @Override
    public CartDTO addProductToCart(Integer productId, Integer quantity) {
        Cart cart = createCart();
         Product product=   productRepo.findById(productId)
                 .orElseThrow(()-> new ResourceNotFoundException("Product with Product Id:"+productId+" does not exist"));

      CartItem cartItem=   cartItemsRepo.findByProductIdAndCartId(cart.getCartId(), productId);  //recheck

        if(cartItem!=null){
            throw  new APIException("Product "+product.getProductName()+ " already exists in the cart");
        }
        if(product.getQuantity()==0){
            throw  new APIException(product.getProductName()+" is Out of Stock");
        }
        if(product.getQuantity()<quantity){
            throw new APIException("Please make and order of the "+product.getProductName()+ " less than or equal to the Quantity"
            +product.getQuantity()+ ".");
        }

        CartItem newCartItem=new CartItem();
        newCartItem.setCart(cart);
        newCartItem.setProduct(product);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setQuantity(quantity);
        newCartItem.setProduct_Price(product.getPrice());
        cartItemsRepo.save(newCartItem);

        //we can reduce the stock once the order is placed
        product.setQuantity(product.getQuantity());  //check inn Postman

        cart.setTotalPrice(cart.getTotalPrice()+product.getSpecialPrice()*quantity);
        cartRepo.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();
        List<ProductDTO> list = cartItems.stream().map(items -> modelMapper.map(items.getProduct(), ProductDTO.class)).toList();
        cartDTO.setProducts(list);


        return cartDTO;
    }


    //Get All the carts
    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepo.findAll();
        if(carts.isEmpty()){
            throw new APIException("No cart Items exits");
        }


       List<CartDTO> cartDTOs = carts.stream().map(cart -> {
           CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

           List<ProductDTO> products = cart.getCartItems().stream().map(cartItem -> {
               ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
               productDTO.setQuantity(cartItem.getQuantity()); // Set the quantity from CartItem
               return productDTO;
           }).collect(Collectors.toList());


           cartDTO.setProducts(products);

           return cartDTO;

       }).collect(Collectors.toList());

       return cartDTOs;

   }

   //Get a Particular Cart of a LoggedIn user by using User's emailId and Cart ID
    @Override
    public CartDTO getCart(String emailId, int cartId) {
        Cart cart=cartRepo.findCartByEmailAndCartId(emailId,cartId);
        if(cart==null){
            throw new ResourceNotFoundException("Cart with Cart ID:"+cartId +" does not exits");
        }

        CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);
        cart.getCartItems().forEach(items->items.getProduct().setQuantity(items.getQuantity()));
        List<ProductDTO> productDTOS=cart.getCartItems().stream().map(p->modelMapper.map(p.getProduct(),ProductDTO.class)).toList();
        cartDTO.setProducts(productDTOS);
        return cartDTO;
    }

    //Service to Update the Quantity of the Cart Items
    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Integer productId, Integer quantity) {

        String emailId = authUtil.loggedInEmail();
        Cart userCart = cartRepo.findCartByEmail(emailId);
        Integer cartId  = userCart.getCartId();

        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart with Cart Id:"+cartId+" Not found"));

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with Product Id:"+productId+" Not found"));

        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");
        }

        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");
        }

        CartItem cartItem = cartItemsRepo.findByProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not available in the cart!!!");
        }

        int newQuantity = cartItem.getQuantity() + quantity;

        // Validation to prevent negative quantities
        if (newQuantity < 0) {
            throw new APIException("The resulting quantity cannot be negative.");
        }

        if (newQuantity == 0){
            deleteProductFromCart(cartId, productId);
        }else{
            cartItem.setProduct_Price(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProduct_Price() * quantity));
            cartRepo.save(cart);
        }


        CartItem updatedItem = cartItemsRepo.save(cartItem);
        if(updatedItem.getQuantity() == 0){
            cartItemsRepo.deleteById(updatedItem.getCartItemId());
        }


        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO prd = modelMapper.map(item.getProduct(), ProductDTO.class);
            prd.setQuantity(item.getQuantity());
            return prd;
        });


        cartDTO.setProducts(productStream.toList());

        return cartDTO;
    }



    //Service to delete a cart Item from the user's cart
    @Transactional
    @Override
    public String deleteProductFromCart(Integer cartId, Integer productId) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart with Cart Id:"+cartId+" Not found"));

        CartItem cartItem = cartItemsRepo.findByProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw new ResourceNotFoundException("Product with Product Id:"+productId+" Not found");
        }



        cartItemsRepo.deleteCartItemByProductIdAndCartId(cartId, productId);


//        cart.setTotalPrice(cart.getTotalPrice() -
//                (cartItem.getProduct_Price() * cartItem.getQuantity()));
//        cartRepo.save(cart);


        //reference ChatGpt --> issue the Cartitems needs to be deleted first and the recalculated
        double newTotalPrice = cartItemsRepo.findByCartId(cartId)
                .stream()
                .mapToDouble(item -> item.getProduct_Price() * item.getQuantity())
                .sum();

         //Set total price to 0 if no products remain
        cart.setTotalPrice(newTotalPrice);

        cartRepo.save(cart);
        return "Product " + cartItem.getProduct().getProductName() + " removed from the cart !!!";
    }

    @Override
    public void updateProductInCarts(Integer cartId, Integer productId) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart with Cart Id:"+cartId+" Not found"));

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with Product Id:"+productId+" Not found"));

        CartItem cartItem = cartItemsRepo.findByProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not available in the cart!!!");
        }

        double cartPrice = cart.getTotalPrice()
                - (cartItem.getProduct_Price() * cartItem.getQuantity());

        cartItem.setProduct_Price(product.getSpecialPrice());

        cart.setTotalPrice(cartPrice
                + (cartItem.getProduct_Price() * cartItem.getQuantity()));

        cartItem = cartItemsRepo.save(cartItem);
    }
    //This Method is responsible to save the cart when the address and the payment method is selected from the Front-end
   @Transactional
    @Override
    public String createOrUpdateCartWithItems(List<CartItemsDTO> cartItems) {
        //Get User's email
        String emailId=authUtil.loggedInEmail();

        //Check if an existing cart is available or create a new one
        Cart existingCart=cartRepo.findCartByEmail(emailId);
        if(existingCart==null){
            existingCart=new Cart();
            existingCart.setTotalPrice(0.00);
            existingCart.setUser(authUtil.loggedInUser());
            existingCart=cartRepo.save(existingCart);
        }else {
            //Clear all current items in the existing cart
            cartItemsRepo.deleteAllByCartId(existingCart.getCartId());
        }

        double totalPrice=0.0;
        //Process each item in the request to add to the cart
        for(CartItemsDTO cartItemsDTO:cartItems){
            Integer productId=cartItemsDTO.getProductId();
            Integer quantity=cartItemsDTO.getQuantity();


            //Find the product by ID
           Product product= productRepo.findById(productId)
                    .orElseThrow(()->new ResourceNotFoundException("Product with Product Id:"+productId+" Not found"));
            //Directly update product stock and total price
          //  product.setQuantity(product.getQuantity()-quantity);

            totalPrice+=product.getSpecialPrice()*quantity;
            //Create and save cart item

            CartItem cartItem=new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(existingCart);
            cartItem.setQuantity(quantity);
            cartItem.setProduct_Price(product.getSpecialPrice());
            cartItem.setDiscount(product.getDiscount());
            cartItemsRepo.save(cartItem);
        }



        //Update the cart's total price and save
        existingCart.setTotalPrice(totalPrice);
        cartRepo.save(existingCart);
        return "Cart created/updated with the new Items Successfully";
    }


    //Helper Method used in addProductToCart()
    private Cart createCart(){
      Cart userCart= cartRepo.findCartByEmail(authUtil.loggedInEmail());
      if(userCart!=null){
          return userCart;
      }
      Cart cart=new Cart();
      cart.setTotalPrice(0.00);
      cart.setUser(authUtil.loggedInUser());
     Cart newCart= cartRepo.save(cart);

     return newCart;
    }
}
