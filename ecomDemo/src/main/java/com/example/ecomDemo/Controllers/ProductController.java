package com.example.ecomDemo.Controllers;

import com.example.ecomDemo.Config.AppConstants;
import com.example.ecomDemo.PayLoad.ProductDTO;
import com.example.ecomDemo.PayLoad.ProductResponse;
import com.example.ecomDemo.Service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> createProducts(@Valid @RequestBody ProductDTO productDTO, @PathVariable Integer categoryId){
      ProductDTO product=  productService.createProduct(productDTO,categoryId);
      return new ResponseEntity<>(product, HttpStatus.CREATED);
    } //DONE

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name="category",required = false) String category,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.pageNumber,required = false) Integer pageNumber,
            @RequestParam(name="pageSize", defaultValue = AppConstants.pageSize,required = false)Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BY_PRODUCTS, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder
    ){
      ProductResponse productResponse=  productService.getAllProducts(pageNumber,pageSize,sortBy,sortOrder, keyword, category);
      return new ResponseEntity<>(productResponse,HttpStatus.OK);
    } //DONE

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategoryId(@PathVariable Integer categoryId,
           @RequestParam(name = "pageNumber", defaultValue = AppConstants.pageNumber,required = false) Integer pageNumber,
           @RequestParam(name="pageSize", defaultValue = AppConstants.pageSize,required = false)Integer pageSize,
           @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BY_PRODUCTS, required = false) String sortBy,
           @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder){
       ProductResponse productResponse= productService.getProductsbyCategory(categoryId, pageNumber,pageSize,sortBy,sortOrder);
       return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }

    //Search feature
    @GetMapping("public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeywords(@PathVariable String keyword,
                   @RequestParam(name = "pageNumber", defaultValue = AppConstants.pageNumber,required = false) Integer pageNumber,
                   @RequestParam(name="pageSize", defaultValue = AppConstants.pageSize,required = false)Integer pageSize,
                   @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BY_PRODUCTS, required = false) String sortBy,
                   @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder                                                       ){
      ProductResponse productResponse=  productService.getProductsByKeywords(keyword,pageNumber,pageSize,sortBy,sortOrder);
      return new ResponseEntity<>(productResponse,HttpStatus.FOUND);
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<ProductDTO> updateProductById(@Valid @RequestBody ProductDTO productDTO,@PathVariable Integer productId){
       ProductDTO product= productService.updateProductByCategory(productDTO,productId);
       return new ResponseEntity<>(product,HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProductById(@PathVariable Integer productId){
     ProductDTO productDTO=   productService.deleteProductById(productId);
     return new ResponseEntity<>(productDTO,HttpStatus.OK);
    }


    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Integer productId,
                                                         @RequestParam("image")MultipartFile image) throws IOException {
      ProductDTO productDTO=  productService.updateProductImage(productId,image);
      return new ResponseEntity<>(productDTO,HttpStatus.OK);
    }
}
