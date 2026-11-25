package com.dsf.librar.dto.report;

import com.dsf.librar.enums.PaymentMethod;

import java.math.BigDecimal;

public record PaymentSummaryDto(
        PaymentMethod paymentMethod,
        BigDecimal totalAmount,
        Long transactions
) {}
