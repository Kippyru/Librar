package com.dsf.librar.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SucursalRequestDto {
    @NotBlank(message = "El nombre de la sucursal no puede estar vacío")
    private String name;
    private String address;
    private String phone;
}
