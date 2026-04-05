package edu.eci.dosw.DOSW_Library.tdd.persistence.document;

import edu.eci.dosw.DOSW_Library.tdd.core.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanStatusEvent {
    private Status status;
    private Instant occurredAt;
}
