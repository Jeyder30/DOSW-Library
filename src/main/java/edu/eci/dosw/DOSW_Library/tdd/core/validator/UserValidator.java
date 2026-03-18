package edu.eci.dosw.DOSW_Library.tdd.core.validator;

import edu.eci.dosw.DOSW_Library.tdd.core.model.User;
import edu.eci.dosw.DOSW_Library.tdd.core.util.ValidationUtil;

public class UserValidator {
    public static void validate(User user) {
        ValidationUtil.requireNonNull(user, "User cannot be null");
        ValidationUtil.requireNonEmpty(user.getId(), "User id is required");
        ValidationUtil.requireNonEmpty(user.getName(), "User name is required");
    }
}
