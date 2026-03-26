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
@Schema(description = "Representa un libro de la biblioteca")
public class BookDTO {
    @NotBlank(message = "Book id is required")
    @Schema(example = "book-1")
    private String id;

    @NotBlank(message = "Title is required")
    @Schema(example = "Clean Code")
    private String title;

    @NotBlank(message = "Author is required")
    @Schema(example = "Robert C. Martin")
    private String author;
}
