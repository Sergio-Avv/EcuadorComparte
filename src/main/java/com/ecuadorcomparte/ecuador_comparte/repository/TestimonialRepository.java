package com.ecuadorcomparte.ecuador_comparte.repository;

import com.ecuadorcomparte.ecuador_comparte.model.Testimonial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TestimonialRepository extends JpaRepository<Testimonial, Long> {

    @Query("SELECT t FROM Testimonial t WHERE " +
           "(:name IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:dateFrom IS NULL OR t.createdAt >= :dateFrom) AND " +
           "(:dateTo IS NULL OR t.createdAt <= :dateTo)")
    List<Testimonial> filterTestimonials(
            @Param("name") String name,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo);
}
