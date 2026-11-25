package com.dsf.librar.dto.report;

import com.dsf.librar.enums.CashMovementReason;
import com.dsf.librar.enums.CashMovementType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CashMovementReportItemDto(
        Long id,
        LocalDateTime occurredAt,
        Long cashRegisterId,
        String sucursal,
        String username,
        CashMovementType type,
        CashMovementReason reason,
        String description,
        BigDecimal amount
) {}
