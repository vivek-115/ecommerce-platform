package com.example.ecomDemo.Repositry;

import com.example.ecomDemo.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepo extends JpaRepository<Category,Integer> {

 Category   findByCategoryName(String categoryName);
}
