package com.dsf.librar.controller;

import com.dsf.librar.dto.request.TaxRateRequestDto;
import com.dsf.librar.dto.response.TaxRateResponseDto;
import com.dsf.librar.service.TaxRateService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/taxes/{taxId}/rates")
@RequiredArgsConstructor
public class TaxRateController {

    private final TaxRateService taxRateService;

    @PostMapping
    public ResponseEntity<TaxRateResponseDto> create(
            @PathVariable Long taxId,
            @Valid @RequestBody TaxRateRequestDto dto
    ) {
        TaxRateResponseDto response =
                taxRateService.create(taxId, dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<TaxRateResponseDto>> findByTax(
            @PathVariable Long taxId
    ) {
        return ResponseEntity.ok(
                taxRateService.findByTax(taxId)
        );
    }

    @GetMapping("/current")
    public ResponseEntity<TaxRateResponseDto> getCurrentRate(
            @PathVariable Long taxId
    ) {
        return ResponseEntity.ok(
                taxRateService.getCurrentRate(taxId)
        );
    }

    @PatchMapping("/{rateId}/close")
    public ResponseEntity<TaxRateResponseDto> closeRate(
            @PathVariable Long taxId,
            @PathVariable Long rateId,
            @Valid @RequestBody CloseTaxRateRequestDto dto
    ) {
        TaxRateResponseDto response =
                taxRateService.closeRate(taxId, rateId, dto.validUntil());

        return ResponseEntity.ok(response);
    }

    public record CloseTaxRateRequestDto(
            @NotNull LocalDate validUntil
    ) {}
}
