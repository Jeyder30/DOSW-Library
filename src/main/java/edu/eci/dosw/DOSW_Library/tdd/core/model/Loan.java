package edu.eci.dosw.DOSW_Library.tdd.core.model;

import edu.eci.dosw.DOSW_Library.tdd.core.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
    private String id;
    private User user;
    private Book book;
    private Status status;
    private LocalDate loanDate;
    private LocalDate returnDate;

    public Loan(String id, Book book, User user) {
        this.id = id;
        this.book = book;
        this.user = user;
        this.status = Status.ACTIVE;
        this.loanDate = DateUtil.today();
    }

    public void returnBook() {
        this.status = Status.RETURNED;
        this.returnDate = DateUtil.today();
    }
}
