package com.ecuadorcomparte.ecuador_comparte.repository;

import com.ecuadorcomparte.ecuador_comparte.model.ContactRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContactRequestRepository extends JpaRepository<ContactRequest, Long> {
    List<ContactRequest> findByPurpose(ContactRequest.Purpose purpose);
    List<ContactRequest> findByStatus(ContactRequest.Status status);
    List<ContactRequest> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT c FROM ContactRequest c WHERE " +
           "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:email IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:purpose IS NULL OR c.purpose = :purpose) AND " +
           "(:dateFrom IS NULL OR c.createdAt >= :dateFrom) AND " +
           "(:dateTo IS NULL OR c.createdAt <= :dateTo)")
    List<ContactRequest> filterContactRequests(
            @Param("name") String name,
            @Param("email") String email,
            @Param("purpose") ContactRequest.Purpose purpose,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo);
}
