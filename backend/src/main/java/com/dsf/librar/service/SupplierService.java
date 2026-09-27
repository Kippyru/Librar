package com.dsf.librar.service;

import com.dsf.librar.dto.SupplierRequestDto;
import com.dsf.librar.dto.SupplierResponseDto;

import java.util.List;

public interface SupplierService {
    void createSupplier(SupplierRequestDto  supplierRequestDto);
    List<SupplierResponseDto> listSupplier();
    SupplierResponseDto getSupplierById(Long id);
    void updateSupplier(Long id, SupplierRequestDto supplierRequestDto);
    void deleteSupplier(Long id);
    void restoreSupplier(Long id);
}
