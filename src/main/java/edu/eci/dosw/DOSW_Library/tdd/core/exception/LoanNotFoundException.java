package edu.eci.dosw.DOSW_Library.tdd.core.exception;

public class LoanNotFoundException extends RuntimeException {

    public LoanNotFoundException(String message) {
        super(message);
    }
}
