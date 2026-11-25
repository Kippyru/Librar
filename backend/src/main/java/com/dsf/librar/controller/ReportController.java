package com.dsf.librar.controller;

import com.dsf.librar.dto.report.ReportResponseDto;
import com.dsf.librar.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<ReportResponseDto> getReport(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) Long sucursalId,

            @RequestParam(defaultValue = "0") int salesPage,
            @RequestParam(defaultValue = "10") int salesSize,

            @RequestParam(defaultValue = "0") int movementsPage,
            @RequestParam(defaultValue = "10") int movementsSize
    ) {
        return ResponseEntity.ok(
                reportService.getReport(
                        startDate,
                        endDate,
                        sucursalId,
                        salesPage,
                        salesSize,
                        movementsPage,
                        movementsSize
                )
        );
    }
}