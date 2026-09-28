package com.dsf.librar.dto;

import com.dsf.librar.entity.Product;
import com.dsf.librar.entity.Sucursal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockSucursalRequestDto {
    private Long product;
    private Long sucursal;
    private Integer amount;
}
