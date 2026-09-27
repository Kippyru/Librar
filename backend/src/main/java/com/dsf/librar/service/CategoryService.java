package com.dsf.librar.service;

import com.dsf.librar.dto.CategoryRequestDto;
import com.dsf.librar.dto.CategoryResponseDto;

import java.util.List;

public interface CategoryService {
    void createCategory(CategoryRequestDto categoryRequestDto);
    List<CategoryResponseDto> listCategory();
    CategoryResponseDto getCategoryById(Long id);
    void updateCategory(Long id, CategoryRequestDto categoryRequestDto);
    void deleteCategory(Long id);
    void restoreCategory(Long id);
}
