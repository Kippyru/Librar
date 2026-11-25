package com.dsf.librar.dto.report;

import com.dsf.librar.enums.PaymentMethod;
import com.dsf.librar.enums.SaleStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SalesReportItemDto(
        Long saleId,
        LocalDateTime date,
        String username,
        PaymentMethod paymentMethod,
        BigDecimal totalAmount,
        SaleStatus status
) {}
