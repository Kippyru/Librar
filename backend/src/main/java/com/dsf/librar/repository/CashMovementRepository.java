package com.dsf.librar.repository;

import com.dsf.librar.dto.report.CashMovementReportItemDto;
import com.dsf.librar.entity.CashMovement;
import com.dsf.librar.enums.CashMovementType;
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
public interface CashMovementRepository extends JpaRepository<CashMovement, Long> {

    //added to find by ID
    List<CashMovement> findByCashRegisterId(Long cashRegisterId);

    @Query(value = """
            SELECT
                COALESCE(SUM(
                    CASE WHEN type = 'CASH_IN' THEN amount ELSE 0 END
                ), 0) AS cashIn,
                COALESCE(SUM(
                    CASE WHEN type = 'CASH_OUT' THEN amount ELSE 0 END
                ), 0) AS cashOut
            FROM cash_movement
            WHERE cash_register_id = :cashRegisterId
            """, nativeQuery = true)
    CashMovementTotals calculateTotals(
            @Param("cashRegisterId") Long cashRegisterId
    );

    interface CashMovementTotals {
        BigDecimal getCashIn();

        BigDecimal getCashOut();
    }

    // Listado de movimientos para el reporte
    @Query(
            value = """
                    SELECT new com.dsf.librar.dto.report.CashMovementReportItemDto(
                        cm.id,
                        cm.occurredAt,
                        cm.cashRegister.id,
                        cm.cashRegister.sucursal.name,
                        cm.cashRegister.user.username,
                        cm.type,
                        cm.reason,
                        cm.description,
                        cm.amount
                    )
                    FROM CashMovement cm
                    WHERE cm.occurredAt >= :startDate
                      AND cm.occurredAt < :endDate
                      AND (:sucursalId IS NULL OR cm.cashRegister.sucursal.id = :sucursalId)
                    ORDER BY cm.occurredAt DESC, cm.id DESC
                    """,
            countQuery = """
                    SELECT COUNT(cm.id)
                    FROM CashMovement cm
                    WHERE cm.occurredAt >= :startDate
                      AND cm.occurredAt < :endDate
                      AND (:sucursalId IS NULL OR cm.cashRegister.sucursal.id = :sucursalId)
                    """
    )
    Page<CashMovementReportItemDto> findMovementReport(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("sucursalId") Long sucursalId,
            Pageable pageable
    );

    // Totales agrupados por CASH_IN / CASH_OUT
    @Query("""
            SELECT
                cm.type AS type,
                SUM(cm.amount) AS total
            FROM CashMovement cm
            WHERE cm.occurredAt >= :startDate
              AND cm.occurredAt < :endDate
              AND (:sucursalId IS NULL OR cm.cashRegister.sucursal.id = :sucursalId)
            GROUP BY cm.type
            """)
    List<CashMovementTypeTotal> calculateReportTotals(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("sucursalId") Long sucursalId
    );

    interface CashMovementTypeTotal {
        CashMovementType getType();

        BigDecimal getTotal();
    }

    @Query("""
            SELECT COUNT(cm.id)
            FROM CashMovement cm
            WHERE cm.occurredAt >= :startDate
              AND cm.occurredAt < :endDate
              AND (:sucursalId IS NULL OR cm.cashRegister.sucursal.id = :sucursalId)
            """)
    long countReportMovements(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("sucursalId") Long sucursalId
    );
}