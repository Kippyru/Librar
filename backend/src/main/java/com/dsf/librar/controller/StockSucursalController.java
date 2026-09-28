package com.dsf.librar.controller;

import com.dsf.librar.dto.StockSucursalRequestDto;
import com.dsf.librar.dto.StockSucursalResponseDto;
import com.dsf.librar.service.StockSucursalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/stocksucursal")
@RequiredArgsConstructor
public class StockSucursalController {

    private final StockSucursalService stockSucursalService;

    @PostMapping
    public ResponseEntity<StockSucursalResponseDto> create(@RequestBody StockSucursalRequestDto dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(stockSucursalService.createStockSucursal(dto));
    }

    @GetMapping("/list/{id}")
    public ResponseEntity<StockSucursalResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(
                stockSucursalService.findById(id)
        );
    }

    @GetMapping("/list")
    public ResponseEntity<List<StockSucursalResponseDto>> findAll() {
        return ResponseEntity.ok(
                stockSucursalService.findAll()
        );
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<StockSucursalResponseDto>> findLowStock() {
        return ResponseEntity.ok(
                stockSucursalService.findLowStock()
        );
    }

    @PutMapping("update/{id}")
    public ResponseEntity<StockSucursalResponseDto> update(@PathVariable Long id, @RequestBody StockSucursalRequestDto dto) {
        return ResponseEntity.ok(
                stockSucursalService.update(id, dto)
        );
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stockSucursalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}