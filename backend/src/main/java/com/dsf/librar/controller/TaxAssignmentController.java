package com.dsf.librar.controller;

import com.dsf.librar.dto.request.TaxAssignmentRequestDto;
import com.dsf.librar.dto.response.TaxAssignmentResponseDto;
import com.dsf.librar.service.TaxAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tax-assignments")
@RequiredArgsConstructor
public class TaxAssignmentController {

    private final TaxAssignmentService taxAssignmentService;

    @PostMapping
    public ResponseEntity<TaxAssignmentResponseDto> create(
            @Valid @RequestBody TaxAssignmentRequestDto dto
    ) {
        TaxAssignmentResponseDto response =
                taxAssignmentService.create(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaxAssignmentResponseDto> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                taxAssignmentService.getById(id)
        );
    }

    @GetMapping("/tax/{taxId}")
    public ResponseEntity<List<TaxAssignmentResponseDto>> findByTax(
            @PathVariable Long taxId
    ) {
        return ResponseEntity.ok(
                taxAssignmentService.findByTax(taxId)
        );
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<TaxAssignmentResponseDto>> findByProduct(
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(
                taxAssignmentService.findByProduct(productId)
        );
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<TaxAssignmentResponseDto>> findByCategory(
            @PathVariable Long categoryId
    ) {
        return ResponseEntity.ok(
                taxAssignmentService.findByCategory(categoryId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaxAssignmentResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody TaxAssignmentRequestDto dto
    ) {
        return ResponseEntity.ok(
                taxAssignmentService.update(id, dto)
        );
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(
            @PathVariable Long id
    ) {
        taxAssignmentService.activate(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(
            @PathVariable Long id
    ) {
        taxAssignmentService.deactivate(id);

        return ResponseEntity.noContent().build();
    }
}
