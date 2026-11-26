package com.dsf.librar.dto.response;

import com.dsf.librar.enums.TaxCalculationType;

public record TaxResponseDto(
        Long id,
        String code,
        String name,
        String description,
        String classification,
        TaxCalculationType calculationType,
        Boolean active,
        TaxRateResponseDto currentRate
) {}
