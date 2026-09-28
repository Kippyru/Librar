package com.dsf.librar.repository;

import com.dsf.librar.entity.StockSucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockSucursalRepository extends JpaRepository<StockSucursal, Long> {

    Optional<StockSucursal> findByProductIdAndSucursalId(
            Long productId,
            Long sucursalId
    );

    boolean existsByProductIdAndSucursalId(
            Long productId,
            Long sucursalId
    );

    List<StockSucursal> findByAmountLessThanEqual(Integer amount);

}