package com.dsf.librar.repository;

import com.dsf.librar.entity.StockSucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockSucursalRepository extends JpaRepository<StockSucursal, Long> {

    Optional<StockSucursal> findByProductIdAndSucursalId(
            Long product,
            Long sucursal
    );
    @Query("""
                SELECT s
                FROM StockSucursal s
                WHERE s.amount <= s.product.minimumStock
            """)
    List<StockSucursal> findLowStock();

}