package com.dsf.librar.service;

import com.dsf.librar.dto.request.SaleRequestDto;
import com.dsf.librar.dto.response.SaleResponseDto;

import java.util.List;

public interface SaleService {
    SaleResponseDto register(Long userId, SaleRequestDto request);
    SaleResponseDto getById(Long id);
    List<SaleResponseDto> listBySucursal(Long sucursalId);
    SaleResponseDto cancel(Long id);
}