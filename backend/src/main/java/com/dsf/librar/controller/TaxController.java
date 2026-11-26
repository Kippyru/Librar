package com.dsf.librar.controller;

import com.dsf.librar.dto.request.TaxRequestDto;
import com.dsf.librar.dto.response.TaxResponseDto;
import com.dsf.librar.service.TaxService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/taxes")
@RequiredArgsConstructor
public class TaxController {

    private final TaxService taxService;

    @PostMapping
    public ResponseEntity<TaxResponseDto> create(
            @Valid @RequestBody TaxRequestDto dto
    ) {
        TaxResponseDto response = taxService.create(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<TaxResponseDto>> findAll() {
        return ResponseEntity.ok(taxService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaxResponseDto> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(taxService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaxResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody TaxRequestDto dto
    ) {
        return ResponseEntity.ok(taxService.update(id, dto));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(
            @PathVariable Long id
    ) {
        taxService.activate(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(
            @PathVariable Long id
    ) {
        taxService.deactivate(id);

        return ResponseEntity.noContent().build();
    }
}
