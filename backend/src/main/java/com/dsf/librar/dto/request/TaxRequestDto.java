package com.dsf.librar.dto.request;

import com.dsf.librar.enums.TaxCalculationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

public record TaxRequestDto(

        @NotBlank
        @Size(max = 30)
        String code,

        @NotBlank
        @Size(max = 100)
        String name,

        @Size(max = 500)
        String description,

        @Size(max = 40)
        String classification,

        @NotNull
        TaxCalculationType calculationType
) {
}
