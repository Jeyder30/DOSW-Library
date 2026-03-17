package edu.eci.dosw.DOSW_Library.tdd.controller.mapper;

import edu.eci.dosw.DOSW_Library.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Loan;

public class LoanMapper {
    public static LoanDTO toDTO(Loan loan) {
        return new LoanDTO(
                loan.getId(),
                loan.getUser().getId(),
                loan.getBook().getId(),
                loan.getStatus().name()
        );
    }
}
