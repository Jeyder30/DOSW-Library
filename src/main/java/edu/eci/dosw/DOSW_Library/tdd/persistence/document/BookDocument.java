package edu.eci.dosw.DOSW_Library.tdd.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "books")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDocument {

    @Id
    private String id;

    private String title;
    private String author;

    @Indexed(unique = true)
    private String isbn;

    @Builder.Default
    private List<String> categories = new ArrayList<>();

    private PublicationType publicationType;
    private LocalDate publishedAt;
    private BookMetadata metadata;
    private BookAvailability availability;
    private LocalDate catalogAddedAt;
}
