package edu.eci.dosw.DOSW_Library.tdd.controller.mapper;

import edu.eci.dosw.DOSW_Library.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Loan;

public class LoanMapper {
    public static LoanDTO toDTO(Loan loan) {
        return LoanDTO.builder()
                .id(loan.getId())
                .userId(loan.getUser().getId())
                .bookId(loan.getBook().getId())
                .status(loan.getStatus().name())
                .loanDate(loan.getLoanDate() != null ? loan.getLoanDate().toString() : null)
                .returnDate(loan.getReturnDate() != null ? loan.getReturnDate().toString() : null)
                .build();
    }
}
