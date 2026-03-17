package edu.eci.dosw.DOSW_Library.tdd.core.model;

import java.time.LocalDate;
import java.util.Date;

public class Loan {
    private String id;
    private Book book;
    private User user;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private Status status;

    public Loan(String id, Book book, User user) {
        this.id = id;
        this.book = book;
        this.user = user;
        this.loanDate = LocalDate.now();
        this.status = Status.ACTIVE;
    }

    public void returnBook() {
        this.status = Status.RETURNED;
        this.returnDate = LocalDate.now();
    }

    public String getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }
}
