package com.dsf.librar.mapper;

import com.dsf.librar.dto.SupplierRequestDto;
import com.dsf.librar.dto.SupplierResponseDto;
import com.dsf.librar.entity.Supplier;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    Supplier toEntity(SupplierRequestDto dto);

    SupplierResponseDto toDto(Supplier supplier);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateSupplier(SupplierRequestDto dto, @MappingTarget Supplier supplier);

    List<SupplierResponseDto> listSupplier(List<Supplier>  supplierList);
}
