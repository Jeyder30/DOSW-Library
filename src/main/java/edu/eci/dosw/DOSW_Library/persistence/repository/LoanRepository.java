package edu.eci.dosw.DOSW_Library.persistence.repository;

import edu.eci.dosw.DOSW_Library.persistence.entity.LoanEntity;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<LoanEntity, UUID> {
    List<LoanEntity> findAllByUserId(UUID userId);
    long countByUserIdAndStatus(UUID userId, Status status);
    boolean existsByBookIdAndStatus(UUID bookId, Status status);
}
