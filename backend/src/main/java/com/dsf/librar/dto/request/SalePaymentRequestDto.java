package com.dsf.librar.dto.request;

import com.dsf.librar.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SalePaymentRequestDto(
        @NotNull
        PaymentMethod paymentMethod,

        @NotNull
        @DecimalMin(value = "0.01", message = "El importe debe ser mayor que cero")
        BigDecimal amount
) {
}