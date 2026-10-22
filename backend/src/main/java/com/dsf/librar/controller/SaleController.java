package com.dsf.librar.controller;

import com.dsf.librar.dto.request.SaleRequestDto;
import com.dsf.librar.dto.response.SaleResponseDto;
import com.dsf.librar.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @PostMapping("/user/{userId}")
    public ResponseEntity<SaleResponseDto> register(
            @PathVariable Long userId,
            @Valid @RequestBody SaleRequestDto request) {
        return new ResponseEntity<>(saleService.register(userId, request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.getById(id));
    }

    @GetMapping("/sucursal/{sucursalId}")
    public ResponseEntity<List<SaleResponseDto>> listBySucursal(@PathVariable Long sucursalId) {
        return ResponseEntity.ok(saleService.listBySucursal(sucursalId));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<SaleResponseDto> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.cancel(id));
    }
}