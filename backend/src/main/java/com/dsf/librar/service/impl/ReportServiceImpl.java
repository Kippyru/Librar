package com.dsf.librar.service.impl;

import com.dsf.librar.dto.report.*;
import com.dsf.librar.enums.CashMovementType;
import com.dsf.librar.enums.SaleStatus;
import com.dsf.librar.repository.CashMovementRepository;
import com.dsf.librar.repository.SalePaymentRepository;
import com.dsf.librar.repository.SaleRepository;
import com.dsf.librar.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private static final int MAX_PAGE_SIZE = 100;

    private final SaleRepository saleRepository;
    private final SalePaymentRepository salePaymentRepository;
    private final CashMovementRepository cashMovementRepository;

    @Override
    public ReportResponseDto getReport(
            LocalDate startDate,
            LocalDate endDate,
            Long sucursalId,
            int salesPage,
            int salesSize,
            int movementsPage,
            int movementsSize
    ) {
        validateDates(startDate, endDate);
        validatePagination(salesPage, salesSize);
        validatePagination(movementsPage, movementsSize);

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        Pageable salesPageable = PageRequest.of(salesPage, salesSize);
        Pageable movementsPageable = PageRequest.of(
                movementsPage,
                movementsSize
        );

        // Sales summary
        SaleRepository.SalesReportSummary salesSummary =
                saleRepository.calculateSalesSummary(
                        SaleStatus.COMPLETED,
                        start,
                        end,
                        sucursalId
                );

        // Paginated sales
        Page<SalesReportItemDto> sales =
                saleRepository.findSalesReport(
                        start,
                        end,
                        sucursalId,
                        salesPageable
                );

        // Payment summary for the entire period
        List<PaymentSummaryDto> payments =
                salePaymentRepository.findPaymentSummary(
                        SaleStatus.COMPLETED,
                        start,
                        end,
                        sucursalId
                );

        // Cash movement summary for the entire period
        List<CashMovementRepository.CashMovementTypeTotal> movementTotals =
                cashMovementRepository.calculateReportTotals(
                        start,
                        end,
                        sucursalId
                );

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        for (var movement : movementTotals) {
            if (movement.getType() == CashMovementType.CASH_IN) {
                totalIncome = totalIncome.add(movement.getTotal());
            } else if (movement.getType() == CashMovementType.CASH_OUT) {
                totalExpenses = totalExpenses.add(movement.getTotal());
            }
        }

        // Paginated cash movements
        Page<CashMovementReportItemDto> movements =
                cashMovementRepository.findMovementReport(
                        start,
                        end,
                        sucursalId,
                        movementsPageable
                );

        CashReportSummaryDto cashSummary = new CashReportSummaryDto(
                totalIncome,
                totalExpenses,
                totalIncome.subtract(totalExpenses),
                cashMovementRepository.countReportMovements(
                        start,
                        end,
                        sucursalId
                )
        );

        return new ReportResponseDto(
                salesSummary.getTotalSales(),
                salesSummary.getTotalAmount(),
                salesSummary.getAverageSale(),
                PageResponseDto.from(sales),
                payments,
                cashSummary,
                PageResponseDto.from(movements)
        );
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "Start date and end date are required"
            );
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date"
            );
        }
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page index cannot be negative"
            );
        }

        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and " + MAX_PAGE_SIZE
            );
        }
    }
}