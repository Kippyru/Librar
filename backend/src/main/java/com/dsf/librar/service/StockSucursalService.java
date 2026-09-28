package com.dsf.librar.service;

import com.dsf.librar.dto.StockSucursalRequestDto;
import com.dsf.librar.dto.StockSucursalResponseDto;

import java.util.List;

public interface StockSucursalService {
    StockSucursalResponseDto createStockSucursal(
            StockSucursalRequestDto dto
    );

    StockSucursalResponseDto findById(Long id);

    List<StockSucursalResponseDto> findAll();

    StockSucursalResponseDto update(
            Long id,
            StockSucursalRequestDto dto
    );

    void delete(Long id);

    void decreaseStock(
            Long product,
            Long sucursal,
            int amount
    );

    List<StockSucursalResponseDto> findLowStock();
}
