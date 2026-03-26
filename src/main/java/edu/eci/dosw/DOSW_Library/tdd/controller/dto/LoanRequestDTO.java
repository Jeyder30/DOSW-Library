package edu.eci.dosw.DOSW_Library.tdd.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestDTO {
    @NotBlank(message = "Book id is required")
    private String bookId;

    private String userId;
}
