package com.dsf.librar.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TaxRateRequestDto(

        @NotNull
        @DecimalMin("0.0000")
        @Digits(integer = 8, fraction = 4)
        BigDecimal value,

        @NotNull
        LocalDate validFrom,

        LocalDate validUntil
) {
}
