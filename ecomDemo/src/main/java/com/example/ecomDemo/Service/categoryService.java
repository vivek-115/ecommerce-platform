package com.example.ecomDemo.Service;

import com.example.ecomDemo.Entity.Category;
import com.example.ecomDemo.PayLoad.CategoryDTO;
import com.example.ecomDemo.PayLoad.CategoryResponse;

public interface categoryService {
    Category createCategory(CategoryDTO categoryDTO);
    public CategoryResponse getAllCategories(Integer pageNumber,Integer pageSize, String sortBy, String sortOrder);
    public Category updateCategory(CategoryDTO categoryDTO, int categoryid);
}
