package com.example.ecomDemo.PayLoad;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryDTO {

    private int id;

    @NotBlank
    private String categoryName;
}
