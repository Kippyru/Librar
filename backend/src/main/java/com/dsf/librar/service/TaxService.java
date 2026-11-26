package com.dsf.librar.service;

import com.dsf.librar.dto.request.TaxRequestDto;
import com.dsf.librar.dto.response.TaxResponseDto;

import java.util.List;

public interface TaxService {

    TaxResponseDto create(TaxRequestDto dto);

    TaxResponseDto getById(Long id);

    List<TaxResponseDto> findAll();

    TaxResponseDto update(Long id, TaxRequestDto dto);

    void deactivate(Long id);

    void activate(Long id);
}
