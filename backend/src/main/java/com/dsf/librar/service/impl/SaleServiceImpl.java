package com.dsf.librar.service.impl;

import com.dsf.librar.dto.request.SaleRequestDto;
import com.dsf.librar.dto.response.SaleResponseDto;
import com.dsf.librar.entity.*;
import com.dsf.librar.enums.CashMovementReason;
import com.dsf.librar.enums.CashMovementType;
import com.dsf.librar.enums.CashRegisterStatus;
import com.dsf.librar.enums.SaleStatus;
import com.dsf.librar.mapper.SaleMapper;
import com.dsf.librar.repository.*;
import com.dsf.librar.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final UserRepository userRepository;
    private final SucursalRepository sucursalRepository;
    private final CashRegisterRepository cashRegisterRepository;
    private final ProductRepository productRepository;
    private final CashMovementRepository cashMovementRepository;
    private final SaleMapper saleMapper;

    @Override
    @Transactional
    public SaleResponseDto register(Long userId, SaleRequestDto request) {
        // Validations
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Sucursal sucursal = sucursalRepository.findById(request.sucursalId())
                .orElseThrow(() -> new IllegalArgumentException("Sucursal no encontrada"));
        CashRegister cashRegister = cashRegisterRepository.findById(request.cashRegisterId())
                .orElseThrow(() -> new IllegalArgumentException("Caja no encontrada"));

        if (cashRegister.getStatus() == CashRegisterStatus.CLOSED) {
            throw new IllegalStateException("No se puede registrar una venta con la caja cerrada");
        }

        // Initialize sale header
        Sale sale = new Sale();
        sale.setUser(user);
        sale.setSucursal(sucursal);
        sale.setCashRegister(cashRegister);
        sale.setPaymentMethod(request.paymentMethod());
        sale.setCreatedAt(LocalDateTime.now());
        sale.setStatus(SaleStatus.COMPLETED);

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Process details and calculate total
        for (var detailRequest : request.details()) {
            Product product = productRepository.findById(detailRequest.productId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + detailRequest.productId()));

            // Soon we will be subtracting the stock from StockSucursalRepository

            BigDecimal subtotal = product.getSellingPrice().multiply(BigDecimal.valueOf(detailRequest.quantity()));
            totalAmount = totalAmount.add(subtotal);

            SaleDetail detail = new SaleDetail();
            detail.setProduct(product);
            detail.setQuantity(detailRequest.quantity());
            detail.setUnitPrice(product.getSellingPrice());
            detail.setSubtotal(subtotal);

            sale.addDetail(detail);
        }

        sale.setTotalAmount(totalAmount);

        // Save sale and details by Cascade
        Sale savedSale = saleRepository.save(sale);

        // Registry income in register
        CashMovement cashIn = new CashMovement();
        cashIn.setCashRegister(cashRegister);
        cashIn.setType(CashMovementType.CASH_IN);
        cashIn.setReason(CashMovementReason.SALE);
        cashIn.setAmount(totalAmount);
        cashIn.setDescription("Venta #" + savedSale.getId());
        cashIn.setOccurredAt(LocalDateTime.now());
        cashMovementRepository.save(cashIn);

        return saleMapper.toDto(savedSale);
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponseDto getById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));
        return saleMapper.toDto(sale);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponseDto> listBySucursal(Long sucursalId) {
        // Soon, for findBySucursalId in SaleRepository
        return List.of();
    }

    @Override
    @Transactional
    public SaleResponseDto cancel(Long id) {
        return null;
    }
}