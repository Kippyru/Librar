package com.dsf.librar.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SaleDetailRequestDto(
        @NotNull
        Long productId,

        @NotNull
        @Min(1)
        Integer quantity
) {
}