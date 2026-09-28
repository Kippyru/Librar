package com.dsf.librar.mapper;

import com.dsf.librar.dto.ProductRequestDto;
import com.dsf.librar.dto.ProductResponseDto;
import com.dsf.librar.entity.Product;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    Product toEntity(ProductRequestDto dto);

    @Mapping(target = "category", source = "category.id")
    @Mapping(target = "supplier", source = "supplier.id")
    ProductResponseDto toDto(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateProduct(ProductRequestDto dto, @MappingTarget Product product);

    List<ProductResponseDto> listProduct(List<Product> products);
}
