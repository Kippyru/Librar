package com.dsf.librar.dto.request;

import com.dsf.librar.enums.CashMovementReason;
import com.dsf.librar.enums.CashMovementType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CashMovementRequestDto(
        @NotNull(message = "El tipo de movimiento es requerido")
        CashMovementType type,

        @NotNull(message = "El motivo es requerido")
        CashMovementReason reason,

        @NotNull(message = "El monto es requerido")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
        BigDecimal amount,

        @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
        String description
) {
}