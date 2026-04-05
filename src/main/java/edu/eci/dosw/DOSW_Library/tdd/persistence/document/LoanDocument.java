package edu.eci.dosw.DOSW_Library.tdd.persistence.document;

import edu.eci.dosw.DOSW_Library.tdd.core.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "loans")
@CompoundIndex(name = "loan_user_status", def = "{'userId': 1, 'status': 1}")
@CompoundIndex(name = "loan_book_status", def = "{'bookId': 1, 'status': 1}")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDocument {

    @Id
    private String id;

    private String userId;
    private String bookId;

    private Status status;
    private LocalDate loanDate;
    private LocalDate returnDate;

    @Builder.Default
    private List<LoanStatusEvent> history = new ArrayList<>();
}
