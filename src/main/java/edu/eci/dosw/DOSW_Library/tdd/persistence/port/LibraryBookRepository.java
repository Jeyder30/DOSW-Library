package edu.eci.dosw.DOSW_Library.tdd.persistence.port;

import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;

import java.util.Optional;

public interface LibraryBookRepository extends GenericRepository<Book, String> {

    Optional<Book> findByIsbn(String isbn);
}
