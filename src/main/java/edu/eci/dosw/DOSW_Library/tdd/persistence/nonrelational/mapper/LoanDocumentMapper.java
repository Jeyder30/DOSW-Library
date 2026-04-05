package edu.eci.dosw.DOSW_Library.tdd.persistence.nonrelational.mapper;

import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Loan;
import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import edu.eci.dosw.DOSW_Library.tdd.persistence.document.LoanDocument;

import java.util.ArrayList;

public final class LoanDocumentMapper {

    private LoanDocumentMapper() {
    }

    public static Loan toLoan(LoanDocument d, User user, Book book) {
        if (d == null) {
            return null;
        }
        return Loan.builder()
                .id(d.getId())
                .user(user)
                .book(book)
                .status(d.getStatus())
                .loanDate(d.getLoanDate())
                .returnDate(d.getReturnDate())
                .build();
    }

    public static LoanDocument toDocument(Loan loan) {
        if (loan == null) {
            return null;
        }
        return LoanDocument.builder()
                .id(loan.getId())
                .userId(loan.getUser().getId())
                .bookId(loan.getBook().getId())
                .status(loan.getStatus())
                .loanDate(loan.getLoanDate())
                .returnDate(loan.getReturnDate())
                .history(new ArrayList<>())
                .build();
    }
}
