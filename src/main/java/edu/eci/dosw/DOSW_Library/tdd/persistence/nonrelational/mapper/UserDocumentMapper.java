package edu.eci.dosw.DOSW_Library.tdd.persistence.nonrelational.mapper;

import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import edu.eci.dosw.DOSW_Library.tdd.persistence.document.MembershipType;
import edu.eci.dosw.DOSW_Library.tdd.persistence.document.UserDocument;

import java.time.LocalDate;

public final class UserDocumentMapper {

    private UserDocumentMapper() {
    }

    public static User toUser(UserDocument d) {
        if (d == null) {
            return null;
        }
        return User.builder()
                .id(d.getId())
                .name(d.getName())
                .username(d.getUsername())
                .password(d.getPassword())
                .role(d.getRole())
                .build();
    }

    public static UserDocument toDocument(User user) {
        if (user == null) {
            return null;
        }
        String email = user.getUsername() + "@dosw.library";
        return UserDocument.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole())
                .email(email)
                .membershipType(MembershipType.STANDARD)
                .registeredAt(LocalDate.now())
                .build();
    }
}
