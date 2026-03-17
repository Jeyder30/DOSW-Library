package edu.eci.dosw.DOSW_Library.tdd.core.model;

import java.time.LocalDate;
import java.util.Date;

public class Loan {
    private String id;
    private User user;
    private Book book;
    private Status status;

    public Loan(String id, Book book, User user) {
        this.id = id;
        this.book = book;
        this.user = user;
        this.status = Status.ACTIVE;
    }

    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Book getBook() {
        return book;
    }

    public Status getStatus() {
        return status;
    }

    public void returnBook() {

    }
}
