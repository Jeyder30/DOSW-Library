package edu.eci.dosw.DOSW_Library.tdd.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponseDTO {
    private String id;
    private String userId;
    private String bookId;
    private String status;
    private String loanDate;
    private String returnDate;
}
