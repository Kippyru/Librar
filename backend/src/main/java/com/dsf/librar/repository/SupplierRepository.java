package com.dsf.librar.repository;

import com.dsf.librar.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    @Modifying
    @Query(value = "UPDATE supplier SET active = true WHERE id = :id", nativeQuery = true)
    void restoreById(@Param("id") Long id);
}