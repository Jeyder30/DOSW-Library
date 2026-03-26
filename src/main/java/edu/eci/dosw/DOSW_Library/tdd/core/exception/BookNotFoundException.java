package edu.eci.dosw.DOSW_Library.tdd.core.exception;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(String message) {
        super(message);
    }
}
