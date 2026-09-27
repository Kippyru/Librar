package com.dsf.librar.controller;

import com.dsf.librar.dto.SupplierRequestDto;
import com.dsf.librar.dto.SupplierResponseDto;
import com.dsf.librar.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/supplier")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierService  supplierService;

    @PostMapping
    public ResponseEntity<String> createSupplier(@Valid @RequestBody SupplierRequestDto supplierRequestDto) {
        supplierService.createSupplier(supplierRequestDto);
        return ResponseEntity.ok("Supplier created");
    }

    @GetMapping("/list")
    public List<SupplierResponseDto> listSupplier() {
        return supplierService.listSupplier();
    }

    @GetMapping("/list/{id}")
    public SupplierResponseDto getSupplier(@PathVariable Long id) {
        return supplierService.getSupplierById(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateSupplier(@PathVariable Long id, @Valid @RequestBody SupplierRequestDto supplierRequestDto) {
        supplierService.updateSupplier(id, supplierRequestDto);
        return ResponseEntity.ok("Supplier updated");
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<String> restoreSupplier(@PathVariable Long id) {
        supplierService.restoreSupplier(id);
        return ResponseEntity.ok("Supplier restored");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok("Supplier deleted");
    }
}
