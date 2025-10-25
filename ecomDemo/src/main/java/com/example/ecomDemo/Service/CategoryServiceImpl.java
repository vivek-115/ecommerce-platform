package com.example.ecomDemo.Service;

import com.example.ecomDemo.Entity.Category;
import com.example.ecomDemo.Exception.APIException;
import com.example.ecomDemo.Exception.ResourceNotFoundException;
import com.example.ecomDemo.PayLoad.CategoryDTO;
import com.example.ecomDemo.PayLoad.CategoryResponse;
import com.example.ecomDemo.Repositry.CategoryRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements categoryService{

    @Autowired
    private CategoryRepo categoryRepo;

  @Autowired
  private ModelMapper modelMapper;

    public Category createCategory(CategoryDTO categoryDTO){
        Category byCategoryName = categoryRepo.findByCategoryName(categoryDTO.getCategoryName());
        System.out.println(byCategoryName);
        if(byCategoryName!=null){
            throw new APIException("Category with name: "+categoryDTO.getCategoryName()+" already Exits");
        }

        Category category=new Category();
        category.setCategoryName(categoryDTO.getCategoryName());
        categoryRepo.save(category);
        return  category;
    }
    //this Service returns all the categories in a sortedFormat(either Asc or Desc) and
    //also pagination is implemented
    public CategoryResponse getAllCategories(Integer pageNumber,Integer pageSize, String sortBy, String sortOrder){
        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")? Sort.by(sortBy).ascending():Sort.by(sortBy).descending();

        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortByAndOrder);


        Page<Category> categoryPage = categoryRepo.findAll(pageDetails);
        List<Category> all = categoryPage.getContent();
        if(all.isEmpty()){
            throw new APIException("No Categories Created till now");
        }


     List<CategoryDTO> categoryDTOList=  all.stream().map(category -> modelMapper.map(category,CategoryDTO.class)).toList();

        CategoryResponse categoryResponse=new CategoryResponse();
        categoryResponse.setContent(categoryDTOList);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());

        return  categoryResponse;

    }

    public Category updateCategory(CategoryDTO categoryDTO, int categoryid){
        Category existingCategory = categoryRepo.findById(categoryid)
                .orElseThrow( ()->new ResourceNotFoundException("Category with Category ID "+categoryid+ " does not exist"));

        existingCategory.setCategoryName(categoryDTO.getCategoryName());
        categoryRepo.save(existingCategory);
        return  existingCategory;

    }

    public String deleteCategory(int categoryId){
        Category existingCategory = categoryRepo.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category with Category ID "+categoryId+ " does not exist"));

     categoryRepo.deleteById(categoryId);
     return "Category with category ID: "+categoryId+" successfully Deleted from the Record";

    }




}
