package com.douglaasph.clinic_api.adapter.outbound.repository.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeJpaEntity, Long> {
    @Query("""
        SELECT e FROM EmployeeJpaEntity e 
        WHERE (:name IS NULL OR :name = '' OR LOWER(e.user.name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:position IS NULL OR e.position = :position)
    """)
    List<EmployeeJpaEntity> findByOptionalFilters(
            @Param("name") String name,
            @Param("position") Integer position
    );

    @Query("""
        SELECT e FROM EmployeeJpaEntity e 
        WHERE (:name IS NULL OR :name = '' OR LOWER(e.user.name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:position IS NULL OR e.position = :position)
    """)
    Page<EmployeeJpaEntity> findByOptionalFiltersWithPagination(
            @Param("name") String name,
            @Param("position") Integer position,
            Pageable pageable
    );
}