package com.dsf.librar.service;

import com.dsf.librar.dto.request.CashMovementRequestDto;
import com.dsf.librar.dto.response.CashMovementResponseDto;

import java.util.List;

public interface CashMovementService {
    CashMovementResponseDto registerMovement(Long cashRegisterId, CashMovementRequestDto request);
    List<CashMovementResponseDto> getMovementsByCashRegister(Long cashRegisterId);
}