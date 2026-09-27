package com.dsf.librar.mapper;

import com.dsf.librar.dto.CategoryRequestDto;
import com.dsf.librar.dto.CategoryResponseDto;
import com.dsf.librar.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    Category toEntity(CategoryRequestDto dto);


    CategoryResponseDto toDto(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateCategory(CategoryRequestDto dto, @MappingTarget Category category);

    List<CategoryResponseDto> listCategory(List<Category> categoryList);
}
