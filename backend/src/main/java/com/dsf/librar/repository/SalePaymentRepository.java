package com.dsf.librar.repository;

import com.dsf.librar.entity.SalePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface SalePaymentRepository extends JpaRepository<SalePayment, Long> {

    @Query(value = """
        SELECT
            COALESCE(SUM(
                CASE WHEN sp.payment_method = 'EFECTIVO'
                THEN sp.amount ELSE 0 END
            ), 0) AS salesCash,
            COALESCE(SUM(
                CASE WHEN sp.payment_method = 'TRANSFERENCIA'
                THEN sp.amount ELSE 0 END
            ), 0) AS salesTransfer,
            COALESCE(SUM(
                CASE WHEN sp.payment_method = 'TARJETA'
                THEN sp.amount ELSE 0 END
            ), 0) AS salesCard
        FROM sale_payment sp
        INNER JOIN sale s ON s.id = sp.sale_id
        WHERE s.cash_register_id = :cashRegisterId
          AND s.status = 'COMPLETED'
        """, nativeQuery = true)
    SalePaymentTotals calculateSalesTotals(
            @Param("cashRegisterId") Long cashRegisterId
    );

    interface SalePaymentTotals {
        BigDecimal getSalesCash();
        BigDecimal getSalesTransfer();
        BigDecimal getSalesCard();
    }
}
