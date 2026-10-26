package com.dsf.librar.dto.request;

import com.dsf.librar.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SaleRequestDto(
        @NotNull
        Long sucursalId,

        @NotNull
        Long cashRegisterId,

        @NotNull
        PaymentMethod paymentMethod,

        @NotEmpty
        @Valid
        List<SaleDetailRequestDto> details,

        @NotEmpty(message = "Debe registrar al menos un pago")
        @Valid
        List<SalePaymentRequestDto> payments
) {
}