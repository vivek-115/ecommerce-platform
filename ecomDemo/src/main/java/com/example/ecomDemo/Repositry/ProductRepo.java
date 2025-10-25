package com.example.ecomDemo.Repositry;

import com.example.ecomDemo.Entity.Category;
import com.example.ecomDemo.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product,Integer> , JpaSpecificationExecutor<Product> {
    Page<Product> findByCategoryOrderByPriceAsc(Category category, Pageable pageDetails);

   Page<Product> findByProductNameLikeIgnoreCase(String keyword, Pageable pageDetails);
}
