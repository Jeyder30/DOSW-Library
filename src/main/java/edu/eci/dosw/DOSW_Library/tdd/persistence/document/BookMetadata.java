package edu.eci.dosw.DOSW_Library.tdd.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookMetadata {
    private Integer pages;
    private String language;
    private String publisher;
}
