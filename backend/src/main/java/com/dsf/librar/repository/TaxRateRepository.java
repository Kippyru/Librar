package com.dsf.librar.repository;

import com.dsf.librar.entity.TaxRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaxRateRepository extends JpaRepository<TaxRate, Long> {

    List<TaxRate> findByTaxIdOrderByValidFromDesc(Long taxId);

    Optional<TaxRate> findFirstByTaxIdAndValidUntilIsNullOrderByValidFromDesc(Long taxId);

    boolean existsByTaxIdAndValidUntilIsNull(Long taxId);

    @Query("""
    SELECT tr
    FROM TaxRate tr
    WHERE tr.tax.id = :taxId
      AND tr.validFrom <= :date
      AND (tr.validUntil IS NULL OR tr.validUntil >= :date)
    """)
    Optional<TaxRate> findCurrentRate(
            Long taxId,
            LocalDate date
    );
}
