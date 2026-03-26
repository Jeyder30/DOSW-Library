package edu.eci.dosw.DOSW_Library.tdd.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representa un prestamo de libro")
public class LoanDTO {
    @Schema(example = "loan-1")
    private String id;

    @Schema(example = "user-1")
    private String userId;

    @Schema(example = "book-1")
    private String bookId;

    @Schema(example = "ACTIVE")
    private String status;

    @Schema(example = "2026-03-26")
    private String loanDate;

    @Schema(example = "2026-03-30")
    private String returnDate;
}
