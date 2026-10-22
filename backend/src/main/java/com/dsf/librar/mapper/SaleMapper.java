package com.dsf.librar.mapper;

import com.dsf.librar.dto.response.SaleDetailResponseDto;
import com.dsf.librar.dto.response.SaleResponseDto;
import com.dsf.librar.entity.Sale;
import com.dsf.librar.entity.SaleDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SaleMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "sucursalId", source = "sucursal.id")
    @Mapping(target = "cashRegisterId", source = "cashRegister.id")
    SaleResponseDto toDto(Sale sale);
    @Mapping(target = "productId", source = "product.id")
    SaleDetailResponseDto toDetailDto(SaleDetail detail);
}