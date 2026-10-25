package com.dsf.librar.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequestDto(
        @NotBlank(message = "El nombre de usuario no puede estar vacío")
        String username,

        @NotBlank(message = "Nombre completo necesario")
        String completeName,

        @NotBlank(message = "La contraseña no puede estar vacía")
        String password,

        @NotNull(message = "El rol es obligatorio")
        Long role,

        Long sucursal
) {}
