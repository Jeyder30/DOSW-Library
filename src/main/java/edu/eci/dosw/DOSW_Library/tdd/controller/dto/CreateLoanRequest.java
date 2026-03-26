package edu.eci.dosw.DOSW_Library.tdd.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para crear un prestamo")
public class CreateLoanRequest {
    @NotBlank(message = "User id is required")
    @Schema(example = "user-1")
    private String userId;

    @NotBlank(message = "Book id is required")
    @Schema(example = "book-1")
    private String bookId;
}
