package com.example.ecomDemo.Controllers;

import com.example.ecomDemo.Config.AppConstants;
import com.example.ecomDemo.Entity.Category;
import com.example.ecomDemo.PayLoad.CategoryDTO;
import com.example.ecomDemo.PayLoad.CategoryResponse;
import com.example.ecomDemo.Service.CategoryServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CategoryController {
    @Autowired
    private CategoryServiceImpl categoryService;

    @PostMapping("/admin/category")
    public ResponseEntity<Category> createCategory(@Valid  @RequestBody CategoryDTO categoryDTO){
       Category category= categoryService.createCategory(categoryDTO);
       return  new ResponseEntity<>(category, HttpStatus.OK);
    }

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber" ,defaultValue = AppConstants.pageNumber,required = false) Integer pageNumber,
            @RequestParam(name = "pageSize" , defaultValue = AppConstants.pageSize,required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BY_CATEGORIES,required = false) String sortBy,
            @RequestParam(name = "sortOrder" , defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder
    ){
        CategoryResponse allCategories = categoryService.getAllCategories(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(allCategories,HttpStatus.OK);
    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<Category> updateCategoryById(@RequestBody CategoryDTO categoryDTO, @PathVariable Integer categoryId){
        return new ResponseEntity<>(categoryService.updateCategory(categoryDTO,categoryId),HttpStatus.OK);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> deleteCategoryById(@PathVariable int categoryId){
        String result= categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

        //findByCategoryName
}
