package com.dsf.librar.service.impl;

import com.dsf.librar.dto.request.CashRegisterCloseRequestDto;
import com.dsf.librar.dto.request.CashRegisterOpenRequestDto;
import com.dsf.librar.dto.response.CashRegisterCountResponseDto;
import com.dsf.librar.dto.response.CashRegisterResponseDto;
import com.dsf.librar.entity.CashRegister;
import com.dsf.librar.entity.Sucursal;
import com.dsf.librar.entity.User;
import com.dsf.librar.enums.CashRegisterStatus;
import com.dsf.librar.mapper.CashRegisterMapper;
import com.dsf.librar.repository.*;
import com.dsf.librar.service.CashRegisterService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CashRegisterServiceImpl implements CashRegisterService {

    private final CashRegisterRepository cashRegisterRepository;
    private final CashMovementRepository cashMovementRepository;
    private final CashRegisterMapper cashRegisterMapper;
    private final UserRepository userRepository;
    private final SucursalRepository sucursalRepository;
    private final SalePaymentRepository salePaymentRepository;

    @Override
    public CashRegisterResponseDto open(Long sucursalId, CashRegisterOpenRequestDto request) {

        if (cashRegisterRepository.existsBySucursalIdAndStatus(sucursalId, CashRegisterStatus.OPEN)) {
            throw new IllegalStateException("La sucursal ya tiene una caja abierta");
        }

        CashRegister cashRegister = new CashRegister();

        cashRegister.setOpeningAmount(request.openingAmount());
        cashRegister.setOpenedAt(LocalDateTime.now());
        cashRegister.setStatus(CashRegisterStatus.OPEN);
        cashRegister.setNotes(request.notes());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado")
                );

        cashRegister.setUser(user);

        Sucursal sucursal = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));

        cashRegister.setSucursal(sucursal);

        CashRegister saved = cashRegisterRepository.save(cashRegister);

        return cashRegisterMapper.toDto(saved);
    }

    @Override
    public CashRegisterResponseDto close(Long cashRegisterId, CashRegisterCloseRequestDto request) {

        CashRegister cashRegister = getCashRegister(cashRegisterId);

        if (cashRegister.getStatus() == CashRegisterStatus.CLOSED) {
            throw new IllegalStateException("La caja ya está cerrada");
        }

        CashRegisterCountResponseDto count = calculateCount(cashRegister);

        cashRegister.setClosingAmount(request.closingAmount());
        cashRegister.setClosedAt(LocalDateTime.now());
        cashRegister.setStatus(CashRegisterStatus.CLOSED);

        if (request.notes() != null) {
            cashRegister.setNotes(request.notes());
        }

        CashRegister saved = cashRegisterRepository.save(cashRegister);

        CashRegisterResponseDto response = cashRegisterMapper.toDto(saved);

        return new CashRegisterResponseDto(
                response.id(),
                response.sucursalId(),
                response.userId(),
                response.openingAmount(),
                response.closingAmount(),
                count.expectedAmount(),
                request.closingAmount()
                        .subtract(count.expectedAmount()),
                response.openedAt(),
                response.closedAt(),
                response.status(),
                response.notes()
        );
    }

    @Override
    @Transactional
    public CashRegisterResponseDto getCurrent(Long sucursalId) {

        CashRegister cashRegister = cashRegisterRepository
                .findBySucursalIdAndStatus(sucursalId, CashRegisterStatus.OPEN)
                .orElseThrow(() -> new IllegalStateException("No hay una caja abierta"));

        CashRegisterCountResponseDto count = calculateCount(cashRegister);
        CashRegisterResponseDto response = cashRegisterMapper.toDto(cashRegister);

        return new CashRegisterResponseDto(
                response.id(),
                response.sucursalId(),
                response.userId(),
                response.openingAmount(),
                response.closingAmount(),
                count.expectedAmount(),
                BigDecimal.ZERO,
                response.openedAt(),
                response.closedAt(),
                response.status(),
                response.notes()
        );
    }

    @Override
    @Transactional
    public CashRegisterCountResponseDto count(Long cashRegisterId) {

        CashRegister cashRegister = getCashRegister(cashRegisterId);

        return calculateCount(cashRegister);
    }

    private CashRegister getCashRegister(Long id) {

        return cashRegisterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Caja no encontrada"));
    }

    private CashRegisterCountResponseDto calculateCount(CashRegister cashRegister) {
        CashMovementRepository.CashMovementTotals movementTotals =
                cashMovementRepository.calculateTotals(cashRegister.getId());

        SalePaymentRepository.SalePaymentTotals salesTotals =
                salePaymentRepository.calculateSalesTotals(cashRegister.getId());

        BigDecimal cashIn = movementTotals.getCashIn();
        BigDecimal cashOut = movementTotals.getCashOut();

        BigDecimal expectedAmount = cashRegister.getOpeningAmount()
                .add(cashIn)
                .subtract(cashOut);

        BigDecimal actualAmount = cashRegister.getClosingAmount();

        BigDecimal difference = actualAmount != null
                ? actualAmount.subtract(expectedAmount)
                : null;

        return new CashRegisterCountResponseDto(
                cashRegister.getOpeningAmount(),
                cashIn,
                cashOut,
                expectedAmount,
                actualAmount,
                difference,
                salesTotals.getSalesCash(),
                salesTotals.getSalesTransfer(),
                salesTotals.getSalesCard()
        );
    }
}