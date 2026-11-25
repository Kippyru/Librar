package com.dsf.librar.dto.report;

import java.math.BigDecimal;

public record CashReportSummaryDto(
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal netMovement,
        Long totalMovements
) {}