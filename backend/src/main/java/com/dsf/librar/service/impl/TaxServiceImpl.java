
package com.dsf.librar.service.impl;

import com.dsf.librar.dto.request.TaxRequestDto;
import com.dsf.librar.dto.response.TaxResponseDto;
import com.dsf.librar.entity.Tax;
import com.dsf.librar.mapper.TaxMapper;
import com.dsf.librar.repository.TaxRepository;
import com.dsf.librar.service.TaxService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaxServiceImpl implements TaxService {

    private final TaxRepository taxRepository;
    private final TaxMapper taxMapper;

    @Override
    @Transactional
    public TaxResponseDto create(TaxRequestDto dto) {

        validateCode(dto.code());

        String normalizedCode = normalizeCode(dto.code());

        if (taxRepository.existsByCodeIgnoreCase(normalizedCode)) {
            throw new IllegalArgumentException(
                    "Ya existe un impuesto con el código: " + normalizedCode
            );
        }

        Tax tax = taxMapper.toEntity(dto);

        tax.setCode(normalizedCode);
        tax.setActive(true);

        Tax savedTax = taxRepository.save(tax);

        return taxMapper.toResponseDto(savedTax);
    }

    @Override
    public TaxResponseDto getById(Long id) {
        return taxMapper.toResponseDto(findTax(id));
    }

    @Override
    public List<TaxResponseDto> findAll() {
        return taxRepository.findAllByOrderByNameAsc()
                .stream()
                .map(taxMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public TaxResponseDto update(Long id, TaxRequestDto dto) {

        Tax tax = findTax(id);

        validateCode(dto.code());

        String normalizedCode = normalizeCode(dto.code());

        if (taxRepository.existsByCodeIgnoreCaseAndIdNot(
                normalizedCode,
                id
        )) {
            throw new IllegalArgumentException(
                    "Ya existe otro impuesto con el código: " + normalizedCode
            );
        }

        if (tax.getCalculationType() != dto.calculationType()) {
            throw new IllegalStateException(
                    "No se puede modificar el tipo de cálculo de un impuesto existente"
            );
        }

        taxMapper.updateEntity(dto, tax);

        tax.setCode(normalizedCode);

        Tax updatedTax = taxRepository.save(tax);

        return taxMapper.toResponseDto(updatedTax);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {

        Tax tax = findTax(id);

        tax.setActive(false);

        taxRepository.save(tax);
    }

    @Override
    @Transactional
    public void activate(Long id) {

        Tax tax = findTax(id);

        tax.setActive(true);

        taxRepository.save(tax);
    }

    private Tax findTax(Long id) {
        return taxRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se encontró el impuesto con ID: " + id
                ));
    }

    private void validateCode(String code) {

        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException(
                    "El código del impuesto es obligatorio"
            );
        }
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase();
    }
}
