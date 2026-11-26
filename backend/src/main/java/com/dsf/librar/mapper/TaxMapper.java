package com.dsf.librar.mapper;

import com.dsf.librar.dto.request.TaxRequestDto;
import com.dsf.librar.dto.response.TaxResponseDto;
import com.dsf.librar.entity.Tax;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaxMapper {

    Tax toEntity(TaxRequestDto dto);

    TaxResponseDto toResponseDto(Tax tax);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(TaxRequestDto dto, @MappingTarget Tax tax);
}
