package edu.eci.dosw.DOSW_Library.tdd.core.validator;

import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import edu.eci.dosw.DOSW_Library.tdd.core.util.ValidationUtil;

public class UserValidator {
    private UserValidator() {
    }

    public static void validate(User user) {
        ValidationUtil.requireNonNull(user, "User cannot be null");
        ValidationUtil.requireNonEmpty(user.getName(), "User name is required");
        ValidationUtil.requireNonEmpty(user.getUsername(), "Username is required");
        ValidationUtil.requireNonEmpty(user.getPassword(), "Password is required");
        ValidationUtil.requireNonNull(user.getRole(), "Role is required");
    }
}
