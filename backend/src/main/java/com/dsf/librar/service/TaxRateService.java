package com.dsf.librar.service;

import com.dsf.librar.dto.request.TaxRateRequestDto;
import com.dsf.librar.dto.response.TaxRateResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface TaxRateService {

    TaxRateResponseDto create(Long taxId, TaxRateRequestDto dto);

    List<TaxRateResponseDto> findByTax(Long taxId);

    TaxRateResponseDto getCurrentRate(Long taxId);

    TaxRateResponseDto closeRate(
            Long taxId,
            Long rateId,
            LocalDate validUntil
    );
}
