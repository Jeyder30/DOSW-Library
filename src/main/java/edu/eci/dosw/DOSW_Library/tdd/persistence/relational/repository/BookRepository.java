package edu.eci.dosw.DOSW_Library.tdd.persistence.relational.repository;

import edu.eci.dosw.DOSW_Library.tdd.persistence.relational.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<BookEntity, UUID> {
    Optional<BookEntity> findByIsbn(String isbn);
}
