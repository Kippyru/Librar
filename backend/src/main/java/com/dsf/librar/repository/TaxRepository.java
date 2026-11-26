package com.dsf.librar.repository;

import com.dsf.librar.entity.Tax;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaxRepository extends JpaRepository<Tax, Long> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

    List<Tax> findAllByOrderByNameAsc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Tax t WHERE t.id = :id")
    Optional<Tax> findByIdForUpdate(@Param("id") Long id);
}
