package com.dsf.librar.dto.request;

import com.dsf.librar.enums.TaxAssignmentEffect;
import com.dsf.librar.enums.TaxAssignmentScope;
import com.dsf.librar.enums.TaxOperation;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

public record TaxAssignmentRequestDto(

        @NotNull
        Long taxId,

        @NotNull
        TaxAssignmentScope scope,

        Long productId,

        Long categoryId,

        @NotNull
        TaxOperation operation,

        @NotNull
        TaxAssignmentEffect effect,

        Integer priority,

        LocalDate validFrom,

        LocalDate validUntil
) {
}
