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
public class BookAvailability {
    private BookInventoryStatus status;
    private int totalCopies;
    private int availableCopies;
    private int loanedCopies;
}
