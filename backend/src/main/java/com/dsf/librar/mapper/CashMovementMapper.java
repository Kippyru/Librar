package com.dsf.librar.mapper;

import com.dsf.librar.dto.response.CashMovementResponseDto;
import com.dsf.librar.entity.CashMovement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CashMovementMapper {

    @Mapping(target = "cashRegisterId", source = "cashRegister.id")
    CashMovementResponseDto toDto(CashMovement cashMovement);
}