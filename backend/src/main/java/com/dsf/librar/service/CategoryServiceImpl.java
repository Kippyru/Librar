package com.dsf.librar.service;

import com.dsf.librar.dto.CategoryRequestDto;
import com.dsf.librar.dto.CategoryResponseDto;
import com.dsf.librar.entity.Category;
import com.dsf.librar.mapper.CategoryMapper;
import com.dsf.librar.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public void createCategory(CategoryRequestDto categoryRequestDto) {
        Category category = categoryMapper.toEntity(categoryRequestDto);
        categoryRepository.save(category);
    }

    @Override
    public List<CategoryResponseDto> listCategory() {
        return categoryMapper.listCategory(categoryRepository.findAll());
    }

    @Override
    public CategoryResponseDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return categoryMapper.toDto(category);
    }

    @Override
    public void updateCategory(Long id, CategoryRequestDto categoryRequestDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        categoryMapper.updateCategory(categoryRequestDto, category);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.setActive(false);
        categoryRepository.save(category);
    }

    @Override
    public void restoreCategory(Long id) {
        categoryRepository.restoreById(id);
    }
}
