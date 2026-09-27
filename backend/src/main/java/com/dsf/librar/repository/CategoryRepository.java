package com.dsf.librar.repository;

import com.dsf.librar.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Modifying
    @Query(value = "UPDATE category SET active = true WHERE id = :id", nativeQuery = true)
    void restoreById(@Param("id") Long id);
}