package com.dsf.librar.repository;

import com.dsf.librar.entity.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {
    @Modifying
    @Query(value = "UPDATE sucursal SET active = true WHERE id = :id", nativeQuery = true)
    void restoreById(@Param("id") Long id);
}