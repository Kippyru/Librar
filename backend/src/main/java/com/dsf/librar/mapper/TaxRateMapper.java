package com.dsf.librar.mapper;

import com.dsf.librar.dto.response.TaxRateResponseDto;
import com.dsf.librar.entity.TaxRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaxRateMapper {

    @Mapping(source = "tax.id", target = "taxId")
    TaxRateResponseDto toResponseDto(TaxRate taxRate);
}
