package com.dsf.librar.service.impl;

import com.dsf.librar.dto.request.CashMovementRequestDto;
import com.dsf.librar.dto.response.CashMovementResponseDto;
import com.dsf.librar.entity.CashMovement;
import com.dsf.librar.entity.CashRegister;
import com.dsf.librar.enums.CashRegisterStatus;
import com.dsf.librar.mapper.CashMovementMapper;
import com.dsf.librar.repository.CashMovementRepository;
import com.dsf.librar.repository.CashRegisterRepository;
import com.dsf.librar.service.CashMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CashMovementServiceImpl implements CashMovementService {

    private final CashMovementRepository cashMovementRepository;
    private final CashRegisterRepository cashRegisterRepository;
    private final CashMovementMapper cashMovementMapper;

    @Override
    @Transactional
    public CashMovementResponseDto registerMovement(Long cashRegisterId, CashMovementRequestDto request) {
        CashRegister cashRegister = cashRegisterRepository.findById(cashRegisterId)
                .orElseThrow(() -> new IllegalArgumentException("Caja no encontrada"));

        // Validamos que la caja esté abierta usando el Enum existente
        if (cashRegister.getStatus() == CashRegisterStatus.CLOSED) {
            throw new IllegalStateException("No se pueden registrar movimientos en una caja cerrada.");
        }

        CashMovement movement = new CashMovement();
        movement.setCashRegister(cashRegister);
        movement.setType(request.type());
        movement.setReason(request.reason());
        movement.setAmount(request.amount());
        movement.setDescription(request.description());
        movement.setOccurredAt(LocalDateTime.now());

        CashMovement saved = cashMovementRepository.save(movement);

        return cashMovementMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CashMovementResponseDto> getMovementsByCashRegister(Long cashRegisterId) {
        return cashMovementRepository.findByCashRegisterId(cashRegisterId).stream()
                .map(cashMovementMapper::toDto)
                .collect(Collectors.toList());
    }
}