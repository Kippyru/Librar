package com.dsf.librar.dto;

import com.dsf.librar.entity.Category;
import com.dsf.librar.entity.Supplier;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDto {
    private String barcode;

    @NotBlank(message = "El nombre del producto no puede estar vacío")
    private String name;

    private String description;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private Integer minimumStock;

    @NotNull(message = "La categoria es obligatoria")
    private Long category;

    @NotNull(message = "El proveedor es obligatorio")
    private Long supplier;

    private String imagenUrl;
}
