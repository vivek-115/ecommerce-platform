package com.example.ecomDemo.PayLoad;

import lombok.Data;

import java.util.List;

@Data
public class CategoryResponse {


    List<CategoryDTO> content;
    private Integer pageNumber;
    private Integer pageSize;
    private  Long totalElements;
    private  Integer totalPages;
    private boolean lastPage;


}
