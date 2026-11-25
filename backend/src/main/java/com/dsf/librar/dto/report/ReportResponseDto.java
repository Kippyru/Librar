package com.dsf.librar.dto.report;

import java.math.BigDecimal;
import java.util.List;

public record ReportResponseDto(
        Long totalSales,
        BigDecimal totalSalesAmount,
        BigDecimal averageSale,
        PageResponseDto<SalesReportItemDto> sales,
        List<PaymentSummaryDto> payments,
        CashReportSummaryDto cashSummary,
        PageResponseDto<CashMovementReportItemDto> cashMovements
) {}