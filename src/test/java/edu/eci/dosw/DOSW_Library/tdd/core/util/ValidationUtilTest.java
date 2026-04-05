package edu.eci.dosw.DOSW_Library.tdd.core.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationUtilTest {

    @Test
    void requireNonNullShouldThrowWhenTheObjectIsNull() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> ValidationUtil.requireNonNull(null, "Object is required"));

        assertEquals("Object is required", exception.getMessage());
    }

    @Test
    void requireNonEmptyShouldThrowWhenTheValueIsBlank() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> ValidationUtil.requireNonEmpty("   ", "Value is required"));

        assertEquals("Value is required", exception.getMessage());
    }

    @Test
    void utilityMethodsShouldAcceptValidValues() {
        assertDoesNotThrow(() -> ValidationUtil.requireNonNull(new Object(), "Object is required"));
        assertDoesNotThrow(() -> ValidationUtil.requireNonEmpty("value", "Value is required"));
        assertEquals(LocalDate.now(), DateUtil.today());
        assertEquals(UUID.fromString(IdGeneratorUtil.generateId()).version(), 4);
        assertNotNull(IdGeneratorUtil.generateId());
    }
}
