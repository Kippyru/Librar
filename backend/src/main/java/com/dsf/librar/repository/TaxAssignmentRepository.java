package com.dsf.librar.repository;

import com.dsf.librar.entity.TaxAssignment;
import com.dsf.librar.enums.TaxAssignmentScope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaxAssignmentRepository extends JpaRepository<TaxAssignment, Long> {

    List<TaxAssignment> findByTaxId(Long taxId);

    List<TaxAssignment> findByProductId(Long productId);

    List<TaxAssignment> findByCategoryId(Long categoryId);
}