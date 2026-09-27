package com.dsf.librar.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplierRequestDto {

    @NotBlank(message = "El nombre del proveedor no puede estar vacío")
    private String name;

    private String cellphone;
    private String phone;
    private String email;
    private String note;
}
