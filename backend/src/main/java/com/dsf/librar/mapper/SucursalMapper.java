package com.dsf.librar.mapper;

import com.dsf.librar.dto.SucursalRequestDto;
import com.dsf.librar.dto.SucursalResponseDto;
import com.dsf.librar.entity.Sucursal;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SucursalMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    Sucursal toEntity(SucursalRequestDto sucursalRequestDto);

    SucursalResponseDto toDto(Sucursal sucursal);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateSucursal(SucursalRequestDto dto, @MappingTarget Sucursal sucursal);

    List<SucursalResponseDto> listSucursal(List<Sucursal> sucursales);
}
