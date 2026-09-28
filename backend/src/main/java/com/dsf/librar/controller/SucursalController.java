package com.dsf.librar.controller;

import com.dsf.librar.dto.SucursalRequestDto;
import com.dsf.librar.dto.SucursalResponseDto;
import com.dsf.librar.service.SucursalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/sucursal")
@RequiredArgsConstructor
public class SucursalController {
    private final SucursalService sucursalService;

    @PostMapping
    public ResponseEntity<String> createSucursal(@Valid @RequestBody SucursalRequestDto sucursalRequestDto) {
        sucursalService.createSucursal(sucursalRequestDto);
        return ResponseEntity.ok("Sucursal created");
    }

    @GetMapping("/list")
    public List<SucursalResponseDto> listSucursal() {
        return sucursalService.listSucursal();
    }

    @GetMapping("/list/{id}")
    public SucursalResponseDto getSucursal(@PathVariable Long id) {
        return sucursalService.getSucursalById(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateSucursal(@PathVariable Long id, @Valid @RequestBody SucursalRequestDto sucursalRequestDto) {
        sucursalService.updateSucursal(id, sucursalRequestDto);
        return ResponseEntity.ok("Sucursal updated");
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<String> restoreSucursal(@PathVariable Long id) {
        sucursalService.restoreSucursal(id);
        return ResponseEntity.ok("Sucursal restored");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteSucursal(@PathVariable Long id) {
        sucursalService.deleteSucursal(id);
        return ResponseEntity.ok("Sucursal deleted");
    }
}

