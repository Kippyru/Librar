package com.dsf.librar.dto;

import com.dsf.librar.entity.Category;
import com.dsf.librar.entity.Supplier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {
    private Long id;
    private String barcode;
    private String name;
    private String description;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private Integer minimumStock;
    private Long category;
    private Long supplier;
    private String imagenUrl;
    private Boolean active;
}
