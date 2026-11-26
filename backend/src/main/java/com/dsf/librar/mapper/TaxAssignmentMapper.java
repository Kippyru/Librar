package com.dsf.librar.mapper;

import com.dsf.librar.dto.response.TaxAssignmentResponseDto;
import com.dsf.librar.entity.TaxAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaxAssignmentMapper {

    @Mapping(source = "tax.id", target = "taxId")
    @Mapping(source = "tax.name", target = "taxName")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "category.id", target = "categoryId")
    TaxAssignmentResponseDto toResponseDto(TaxAssignment assignment);
}
