package com.dsf.librar.mapper;

import com.dsf.librar.dto.CategoryRequestDto;
import com.dsf.librar.dto.CategoryResponseDto;
import com.dsf.librar.entity.Category;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    Category toEntity(CategoryRequestDto dto);


    CategoryResponseDto toDto(Category category);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateCategory(CategoryRequestDto dto, @MappingTarget Category category);

    List<CategoryResponseDto> listCategory(List<Category> categoryList);
}
