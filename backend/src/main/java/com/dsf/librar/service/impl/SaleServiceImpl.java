package com.dsf.librar.service.impl;

import com.dsf.librar.dto.request.SaleRequestDto;
import com.dsf.librar.dto.response.SaleResponseDto;
import com.dsf.librar.entity.*;
import com.dsf.librar.enums.*;
import com.dsf.librar.mapper.SaleMapper;
import com.dsf.librar.repository.*;
import com.dsf.librar.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

        // Validate user, branch and cash register
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Usuario no encontrado"));

        Sucursal sucursal = sucursalRepository.findById(request.sucursalId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Sucursal no encontrada"));

        CashRegister cashRegister = cashRegisterRepository.findById(request.cashRegisterId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Caja no encontrada"));

        if (cashRegister.getStatus() == CashRegisterStatus.CLOSED) {
            throw new IllegalStateException(
                    "No se puede registrar una venta con la caja cerrada");
        }

        // The cash register must belong to the selected branch
        if (!cashRegister.getSucursal().getId().equals(sucursal.getId())) {
            throw new IllegalArgumentException(
                    "La caja no pertenece a la sucursal seleccionada");
        }

        // Initialize sale
        Sale sale = new Sale();
        sale.setUser(user);
        sale.setSucursal(sucursal);
        sale.setCashRegister(cashRegister);
        sale.setPaymentMethod(request.paymentMethod());
        sale.setCreatedAt(LocalDateTime.now());
        sale.setStatus(SaleStatus.COMPLETED);

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Process details and calculate the total using database prices
        for (var detailRequest : request.details()) {

            Product product = productRepository.findById(detailRequest.productId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Producto no encontrado: "
                                            + detailRequest.productId()));

            // Stock validation and subtraction will be added later
            BigDecimal subtotal = product.getSellingPrice()
                    .multiply(BigDecimal.valueOf(detailRequest.quantity()));

            totalAmount = totalAmount.add(subtotal);

            SaleDetail detail = new SaleDetail();
            detail.setProduct(product);
            detail.setQuantity(detailRequest.quantity());
            detail.setUnitPrice(product.getSellingPrice());
            detail.setSubtotal(subtotal);

            sale.addDetail(detail);
        }

        sale.setTotalAmount(totalAmount);

        // Validate and associate payments
        BigDecimal totalPayments = BigDecimal.ZERO;
        BigDecimal cashAmount = BigDecimal.ZERO;
        java.util.Set<PaymentMethod> paymentMethods =
                new java.util.HashSet<>();

        for (var paymentRequest : request.payments()) {

            PaymentMethod method = paymentRequest.paymentMethod();
            BigDecimal amount = paymentRequest.amount();

            if (method == PaymentMethod.MIXTO) {
                throw new IllegalArgumentException(
                        "MIXTO no puede utilizarse como medio de pago individual");
            }

            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "El importe de cada pago debe ser mayor que cero");
            }

            totalPayments = totalPayments.add(amount);
            paymentMethods.add(method);

            if (method == PaymentMethod.EFECTIVO) {
                cashAmount = cashAmount.add(amount);
            }

            SalePayment payment = new SalePayment();
            payment.setPaymentMethod(method);
            payment.setAmount(amount);

            // Sets the relationship on both sides
            sale.addPayment(payment);
        }

        // The payments must cover the entire sale
        if (totalPayments.compareTo(totalAmount) != 0) {
            throw new IllegalArgumentException(
                    "La suma de los pagos debe coincidir con el total de la venta");
        }

        // Validate the overall payment method
        if (request.paymentMethod() == PaymentMethod.MIXTO) {

            if (paymentMethods.size() < 2) {
                throw new IllegalArgumentException(
                        "Una venta mixta requiere al menos dos medios de pago diferentes");
            }

        } else {

            if (request.payments().size() != 1
                    || !paymentMethods.contains(request.paymentMethod())) {
                throw new IllegalArgumentException(
                        "El medio de pago general debe coincidir con el único pago registrado");
            }
        }

        // Save sale, details and payments through Cascade
        Sale savedSale = saleRepository.save(sale);

        // Register only physical cash received
        if (cashAmount.compareTo(BigDecimal.ZERO) > 0) {

            CashMovement cashIn = new CashMovement();
            cashIn.setCashRegister(cashRegister);
            cashIn.setType(CashMovementType.CASH_IN);
            cashIn.setReason(CashMovementReason.SALE);
            cashIn.setAmount(cashAmount);
            cashIn.setDescription("Venta #" + savedSale.getId());
            cashIn.setOccurredAt(LocalDateTime.now());

            cashMovementRepository.save(cashIn);
        }

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