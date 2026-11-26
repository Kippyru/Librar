package com.dsf.librar.dto.response;

import com.dsf.librar.enums.TaxAssignmentEffect;
import com.dsf.librar.enums.TaxAssignmentScope;
import com.dsf.librar.enums.TaxOperation;

import java.time.LocalDate;

public record TaxAssignmentResponseDto(
        Long id,
        Long taxId,
        String taxName,
        TaxAssignmentScope scope,
        Long productId,
        Long categoryId,
        TaxOperation operation,
        TaxAssignmentEffect effect,
        Integer priority,
        boolean active,
        LocalDate validFrom,
        LocalDate validUntil
) {}
