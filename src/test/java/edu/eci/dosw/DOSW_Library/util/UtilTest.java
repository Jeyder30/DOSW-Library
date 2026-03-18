package edu.eci.dosw.DOSW_Library.util;

import edu.eci.dosw.DOSW_Library.tdd.core.util.IdGeneratorUtil;
import edu.eci.dosw.DOSW_Library.tdd.core.util.ValidationUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UtilTest {
    @Test
    void shouldGenerateId() {
        String id = IdGeneratorUtil.generateId();
        assertNotNull(id);
    }

    @Test
    void shouldValidateNonNull() {
        assertDoesNotThrow(() -> ValidationUtil.requireNonNull("test", "error"));
    }

    @Test
    void shouldFailNonNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            ValidationUtil.requireNonNull(null, "error");
        });
    }
}
