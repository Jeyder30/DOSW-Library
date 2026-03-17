package edu.eci.dosw.DOSW_Library.tdd.core.service;

import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;

import java.util.ArrayList;
import java.util.List;

public class BookService {
    private List<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        books.add(book);
    }

    public List<Book> getAllBooks() {
        return books;
    }

    public Book getBookById(String id) {
        return books.stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }
}
