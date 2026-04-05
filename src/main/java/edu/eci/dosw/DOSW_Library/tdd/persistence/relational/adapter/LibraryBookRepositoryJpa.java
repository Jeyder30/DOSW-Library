package edu.eci.dosw.DOSW_Library.tdd.persistence.relational.adapter;

import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import edu.eci.dosw.DOSW_Library.tdd.persistence.port.LibraryBookRepository;
import edu.eci.dosw.DOSW_Library.tdd.persistence.relational.entity.BookEntity;
import edu.eci.dosw.DOSW_Library.tdd.persistence.relational.mapper.BookEntityMapper;
import edu.eci.dosw.DOSW_Library.tdd.persistence.relational.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Repository
@Profile("jpa")
@RequiredArgsConstructor
public class LibraryBookRepositoryJpa implements LibraryBookRepository {

    private final BookRepository jpaBookRepository;
    private final BookEntityMapper bookEntityMapper;

    @Override
    public Book save(Book book) {
        BookEntity entity = bookEntityMapper.toEntity(book);
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        return bookEntityMapper.toDomain(jpaBookRepository.save(entity));
    }

    @Override
    public Optional<Book> findById(String id) {
        return jpaBookRepository.findById(UUID.fromString(id)).map(bookEntityMapper::toDomain);
    }

    @Override
    public List<Book> findAll() {
        return StreamSupport.stream(jpaBookRepository.findAll().spliterator(), false)
                .map(bookEntityMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(String id) {
        jpaBookRepository.deleteById(UUID.fromString(id));
    }

    @Override
    public boolean existsById(String id) {
        return jpaBookRepository.existsById(UUID.fromString(id));
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return jpaBookRepository.findByIsbn(isbn).map(bookEntityMapper::toDomain);
    }
}
