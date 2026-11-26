
package com.dsf.librar.service.impl;

import com.dsf.librar.dto.request.TaxRateRequestDto;
import com.dsf.librar.dto.response.TaxRateResponseDto;
import com.dsf.librar.entity.Tax;
import com.dsf.librar.entity.TaxRate;
import com.dsf.librar.enums.TaxCalculationType;
import com.dsf.librar.mapper.TaxRateMapper;
import com.dsf.librar.repository.TaxRateRepository;
import com.dsf.librar.repository.TaxRepository;
import com.dsf.librar.service.TaxRateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaxRateServiceImpl implements TaxRateService {

    private final TaxRepository taxRepository;
    private final TaxRateRepository taxRateRepository;
    private final TaxRateMapper taxRateMapper;

    @Override
    @Transactional
    public TaxRateResponseDto create(Long taxId, TaxRateRequestDto dto) {

        Tax tax = taxRepository.findByIdForUpdate(taxId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se encontró el impuesto con ID: " + taxId
                ));

        if (!tax.isActive()) {
            throw new IllegalStateException(
                    "No se pueden agregar tasas a un impuesto inactivo"
            );
        }

        validateRate(tax, dto);

        LocalDate validUntil = dto.validUntil();

        if (validUntil == null &&
                taxRateRepository.existsByTaxIdAndValidUntilIsNull(taxId)) {

            throw new IllegalStateException(
                    "El impuesto ya tiene una tasa sin fecha de finalización"
            );
        }

        validateNoOverlap(taxId, dto.validFrom(), validUntil);

        TaxRate rate = new TaxRate();
        rate.setTax(tax);
        rate.setValue(dto.value());
        rate.setValidFrom(dto.validFrom());
        rate.setValidUntil(validUntil);

        TaxRate savedRate = taxRateRepository.save(rate);

        return taxRateMapper.toResponseDto(savedRate);
    }

    @Override
    public List<TaxRateResponseDto> findByTax(Long taxId) {

        findTax(taxId);

        return taxRateRepository.findByTaxIdOrderByValidFromDesc(taxId)
                .stream()
                .map(taxRateMapper::toResponseDto)
                .toList();
    }

    @Override
    public TaxRateResponseDto getCurrentRate(Long taxId) {

        findTax(taxId);

        LocalDate today = LocalDate.now();

        return taxRateRepository.findCurrentRate(taxId, LocalDate.now())
                .map(taxRateMapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "El impuesto no tiene una tasa vigente"
                ));
    }

    @Override
    @Transactional
    public TaxRateResponseDto closeRate(
            Long taxId,
            Long rateId,
            LocalDate validUntil
    ) {

        TaxRate rate = taxRateRepository.findById(rateId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se encontró la tasa con ID: " + rateId
                ));

        if (!rate.getTax().getId().equals(taxId)) {
            throw new IllegalArgumentException(
                    "La tasa no pertenece al impuesto indicado"
            );
        }

        if (rate.getValidUntil() != null) {
            throw new IllegalStateException(
                    "La tasa ya tiene una fecha de finalización"
            );
        }

        if (validUntil == null ||
                validUntil.isBefore(rate.getValidFrom())) {

            throw new IllegalArgumentException(
                    "La fecha de finalización debe ser igual o posterior a la fecha de inicio"
            );
        }

        rate.setValidUntil(validUntil);

        TaxRate updatedRate = taxRateRepository.save(rate);

        return taxRateMapper.toResponseDto(updatedRate);
    }

    private void validateRate(Tax tax, TaxRateRequestDto dto) {

        if (dto.value() == null ||
                dto.value().compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "El valor de la tasa no puede ser negativo"
            );
        }

        if (dto.validFrom() == null) {
            throw new IllegalArgumentException(
                    "La fecha de inicio es obligatoria"
            );
        }

        if (dto.validUntil() != null &&
                dto.validUntil().isBefore(dto.validFrom())) {

            throw new IllegalArgumentException(
                    "La fecha de finalización no puede ser anterior a la fecha de inicio"
            );
        }

        if (tax.getCalculationType() == TaxCalculationType.PERCENTAGE &&
                dto.value().compareTo(new BigDecimal("100")) > 0) {

            throw new IllegalArgumentException(
                    "Una tasa porcentual no puede superar el 100%"
            );
        }
    }

    private void validateNoOverlap(
            Long taxId,
            LocalDate validFrom,
            LocalDate validUntil
    ) {

        boolean overlaps = taxRateRepository
                .findByTaxIdOrderByValidFromDesc(taxId)
                .stream()
                .anyMatch(existing -> {

                    LocalDate existingStart = existing.getValidFrom();
                    LocalDate existingEnd = existing.getValidUntil();

                    boolean startsBeforeExistingEnds =
                            existingEnd == null ||
                                    !validFrom.isAfter(existingEnd);

                    boolean endsAfterExistingStarts =
                            validUntil == null ||
                                    !validUntil.isBefore(existingStart);

                    return startsBeforeExistingEnds &&
                            endsAfterExistingStarts;
                });

        if (overlaps) {
            throw new IllegalStateException(
                    "El período de vigencia se superpone con otra tasa del impuesto"
            );
        }
    }

    private boolean isValidOn(TaxRate rate, LocalDate date) {
        return !date.isBefore(rate.getValidFrom()) &&
                (rate.getValidUntil() == null ||
                        !date.isAfter(rate.getValidUntil()));
    }

    private Tax findTax(Long id) {
        return taxRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se encontró el impuesto con ID: " + id
                ));
    }
}
