package com.dsf.librar.dto.response;

import java.math.BigDecimal;

public record CashRegisterCountResponseDto(
        BigDecimal openingAmount,
        BigDecimal cashIn,
        BigDecimal cashOut,
        BigDecimal expectedAmount,
        BigDecimal actualAmount,
        BigDecimal difference,
        BigDecimal salesCash,
        BigDecimal salesTransfer,
        BigDecimal salesCard
) {}
