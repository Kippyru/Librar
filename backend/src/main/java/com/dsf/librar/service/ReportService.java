package com.dsf.librar.service;

import com.dsf.librar.dto.report.ReportResponseDto;

import java.time.LocalDate;

public interface ReportService {

    ReportResponseDto getReport(
            LocalDate startDate,
            LocalDate endDate,
            Long sucursalId,
            int salesPage,
            int salesSize,
            int movementsPage,
            int movementsSize
    );
}