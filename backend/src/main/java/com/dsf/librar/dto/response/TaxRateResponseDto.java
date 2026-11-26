package com.dsf.librar.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaxRateResponseDto(
        Long id,
        Long taxId,
        BigDecimal value,
        LocalDate validFrom,
        LocalDate validUntil,
        LocalDateTime createdAt
) {
}