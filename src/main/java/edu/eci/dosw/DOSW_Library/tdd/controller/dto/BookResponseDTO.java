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
@Schema(description = "Respuesta con informacion de un libro")
public class BookResponseDTO {
    private String id;
    private String title;
    private String author;
    private String isbn;
    private int totalCopies;
    private int availableCopies;
}
