package edu.eci.dosw.DOSW_Library.tdd.core.exception;

public class LoanLimitExceededException extends RuntimeException {

    public LoanLimitExceededException(String message) {
        super(message);
    }
}
