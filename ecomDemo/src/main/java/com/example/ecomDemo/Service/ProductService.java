package com.example.ecomDemo.Service;

import com.example.ecomDemo.PayLoad.ProductDTO;
import com.example.ecomDemo.PayLoad.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDTO createProduct(ProductDTO productDTO, Integer categoryId);

     ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword, String category);

    ProductResponse getProductsbyCategory(Integer categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse getProductsByKeywords(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductDTO updateProductByCategory(ProductDTO productDTO,Integer productId);

    ProductDTO deleteProductById(Integer productId);

    ProductDTO updateProductImage(Integer productId, MultipartFile image) throws IOException;


}
