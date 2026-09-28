package com.dsf.librar.mapper;

import com.dsf.librar.dto.StockSucursalRequestDto;
import com.dsf.librar.dto.StockSucursalResponseDto;
import com.dsf.librar.entity.StockSucursal;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StockSucursalMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "sucursal", ignore = true)
    StockSucursal toEntity(StockSucursalRequestDto dto);

    @Mapping(target = "product", source = "product.id")
    @Mapping(target = "sucursal", source = "sucursal.id")
    StockSucursalResponseDto toDto(StockSucursal stockSucursal);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "sucursal", ignore = true)
    void updateStockSucursal(StockSucursalRequestDto dto, @MappingTarget StockSucursal stockSucursal);

    List<StockSucursalResponseDto> listStockSucursal(List<StockSucursal> stockSucursals);
}
