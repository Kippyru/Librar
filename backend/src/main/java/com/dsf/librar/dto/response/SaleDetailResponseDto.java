package com.dsf.librar.dto.response;

import java.math.BigDecimal;

public record SaleDetailResponseDto(
        Long id,
        Long productId,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}