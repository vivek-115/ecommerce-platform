package com.example.ecomDemo.Entity;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int categoryId;

    @NotBlank
    private String categoryName;

    @OneToMany(mappedBy = "category")
    List<Product> productList;
}
