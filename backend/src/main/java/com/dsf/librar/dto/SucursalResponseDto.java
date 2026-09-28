package com.dsf.librar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SucursalResponseDto {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private Boolean active;
}
