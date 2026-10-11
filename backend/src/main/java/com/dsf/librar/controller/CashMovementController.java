package com.dsf.librar.controller;

import com.dsf.librar.dto.request.CashMovementRequestDto;
import com.dsf.librar.dto.response.CashMovementResponseDto;
import com.dsf.librar.service.CashMovementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/cash-registers/{cashRegisterId}/movements")
@RequiredArgsConstructor
public class CashMovementController {

    private final CashMovementService cashMovementService;

    @PostMapping
    public ResponseEntity<CashMovementResponseDto> registerMovement(
            @PathVariable Long cashRegisterId,
            @Valid @RequestBody CashMovementRequestDto request) {

        CashMovementResponseDto response = cashMovementService.registerMovement(cashRegisterId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CashMovementResponseDto>> getMovements(
            @PathVariable Long cashRegisterId) {

        List<CashMovementResponseDto> responses = cashMovementService.getMovementsByCashRegister(cashRegisterId);
        return ResponseEntity.ok(responses);
    }
}