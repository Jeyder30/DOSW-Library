package edu.eci.dosw.DOSW_Library.tdd.persistence.document;

import edu.eci.dosw.DOSW_Library.tdd.core.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDocument {

    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String username;

    private String password;

    private Role role;

    @Indexed(unique = true, sparse = true)
    private String email;

    private MembershipType membershipType;
    private LocalDate registeredAt;
}
