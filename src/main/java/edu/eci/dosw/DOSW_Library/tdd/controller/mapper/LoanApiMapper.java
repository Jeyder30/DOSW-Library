package edu.eci.dosw.DOSW_Library.tdd.controller.mapper;

import edu.eci.dosw.DOSW_Library.tdd.controller.dto.LoanResponseDTO;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanApiMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "status", expression = "java(loan.getStatus().name())")
    @Mapping(target = "loanDate", expression = "java(loan.getLoanDate() != null ? loan.getLoanDate().toString() : null)")
    @Mapping(target = "returnDate", expression = "java(loan.getReturnDate() != null ? loan.getReturnDate().toString() : null)")
    LoanResponseDTO toResponse(Loan loan);
}
