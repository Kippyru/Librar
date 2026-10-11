package com.dsf.librar.dto.response;

import com.dsf.librar.enums.CashMovementReason;
import com.dsf.librar.enums.CashMovementType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CashMovementResponseDto(
        Long id,
        Long cashRegisterId,
        CashMovementType type,
        CashMovementReason reason,
        BigDecimal amount,
        String description,
        LocalDateTime occurredAt
) {
}