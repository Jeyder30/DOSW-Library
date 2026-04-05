package edu.eci.dosw.DOSW_Library.tdd.persistence.nonrelational.adapter;

import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import edu.eci.dosw.DOSW_Library.tdd.persistence.nonrelational.mapper.BookDocumentMapper;
import edu.eci.dosw.DOSW_Library.tdd.persistence.port.LibraryBookRepository;
import edu.eci.dosw.DOSW_Library.tdd.persistence.repository.mongo.BookMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
@Profile("mongo")
@RequiredArgsConstructor
public class LibraryBookRepositoryMongo implements LibraryBookRepository {

    private final BookMongoRepository bookMongoRepository;

    @Override
    public Book save(Book book) {
        return BookDocumentMapper.toBook(bookMongoRepository.save(BookDocumentMapper.toDocument(book)));
    }

    @Override
    public Optional<Book> findById(String id) {
        return bookMongoRepository.findById(id).map(BookDocumentMapper::toBook);
    }

    @Override
    public List<Book> findAll() {
        return StreamSupport.stream(bookMongoRepository.findAll().spliterator(), false)
                .map(BookDocumentMapper::toBook)
                .toList();
    }

    @Override
    public void deleteById(String id) {
        bookMongoRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return bookMongoRepository.existsById(id);
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return bookMongoRepository.findByIsbn(isbn).map(BookDocumentMapper::toBook);
    }
}
