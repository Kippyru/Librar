package com.dsf.librar.service;

import com.dsf.librar.dto.request.TaxAssignmentRequestDto;
import com.dsf.librar.dto.response.TaxAssignmentResponseDto;

import java.util.List;

public interface TaxAssignmentService {

    TaxAssignmentResponseDto create(TaxAssignmentRequestDto dto);

    TaxAssignmentResponseDto getById(Long id);

    List<TaxAssignmentResponseDto> findByTax(Long taxId);

    List<TaxAssignmentResponseDto> findByProduct(Long productId);

    List<TaxAssignmentResponseDto> findByCategory(Long categoryId);

    TaxAssignmentResponseDto update(Long id, TaxAssignmentRequestDto dto);

    void deactivate(Long id);

    void activate(Long id);
}
