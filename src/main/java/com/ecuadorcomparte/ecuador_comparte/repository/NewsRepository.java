package com.ecuadorcomparte.ecuador_comparte.repository;

import com.ecuadorcomparte.ecuador_comparte.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findByStatus(News.Status status);

    @Query("SELECT n FROM News n WHERE " +
           "(:title IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:status IS NULL OR n.status = :status) AND " +
           "(:dateFrom IS NULL OR n.publishedAt >= :dateFrom) AND " +
           "(:dateTo IS NULL OR n.publishedAt <= :dateTo)")
    List<News> filterNews(
            @Param("title") String title,
            @Param("status") News.Status status,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo);
}
