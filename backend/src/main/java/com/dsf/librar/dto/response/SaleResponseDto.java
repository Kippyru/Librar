package com.dsf.librar.dto.response;

import com.dsf.librar.enums.PaymentMethod;
import com.dsf.librar.enums.SaleStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SaleResponseDto(
        Long id,
        Long userId,
        Long sucursalId,
        Long cashRegisterId,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        SaleStatus status,
        LocalDateTime createdAt,
        List<SaleDetailResponseDto> details
) {
}