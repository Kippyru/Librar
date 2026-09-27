package com.dsf.librar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplierResponseDto {
    private Long id;
    private String name;
    private String cellphone;
    private String phone;
    private String email;
    private String note;
    private Boolean active;
}
