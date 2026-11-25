package com.dsf.librar.repository;

import com.dsf.librar.dto.report.SalesReportItemDto;
import com.dsf.librar.entity.Sale;
import com.dsf.librar.enums.SaleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    // Resumen de ventas completadas
    @Query("""
        SELECT
            COUNT(s.id) AS totalSales,
            COALESCE(SUM(s.totalAmount), 0) AS totalAmount,
            COALESCE(AVG(s.totalAmount), 0) AS averageSale
        FROM Sale s
        WHERE s.status = :status
          AND s.createdAt >= :startDate
          AND s.createdAt < :endDate
          AND (:sucursalId IS NULL OR s.sucursal.id = :sucursalId)
        """)
    SalesReportSummary calculateSalesSummary(
            @Param("status") SaleStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("sucursalId") Long sucursalId
    );

    //Listado de ventas. Incluye COMPLETED y CANCELED
    @Query(
            value = """
            SELECT new com.dsf.librar.dto.report.SalesReportItemDto(
                s.id,
                s.createdAt,
                s.user.username,
                s.paymentMethod,
                s.totalAmount,
                s.status
            )
            FROM Sale s
            WHERE s.createdAt >= :startDate
              AND s.createdAt < :endDate
              AND (:sucursalId IS NULL OR s.sucursal.id = :sucursalId)
            ORDER BY s.createdAt DESC, s.id DESC
            """,
            countQuery = """
            SELECT COUNT(s.id)
            FROM Sale s
            WHERE s.createdAt >= :startDate
              AND s.createdAt < :endDate
              AND (:sucursalId IS NULL OR s.sucursal.id = :sucursalId)
            """
    )
    Page<SalesReportItemDto> findSalesReport(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("sucursalId") Long sucursalId,
            Pageable pageable
    );


    interface SalesReportSummary {
        Long getTotalSales();
        BigDecimal getTotalAmount();
        BigDecimal getAverageSale();
    }
}