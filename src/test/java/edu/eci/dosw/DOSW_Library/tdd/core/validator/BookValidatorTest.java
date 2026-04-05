package edu.eci.dosw.DOSW_Library.tdd.core.validator;

import edu.eci.dosw.DOSW_Library.tdd.core.exception.InventoryOperationException;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Book;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookValidatorTest {

    @Test
    void validateShouldAcceptABookWithConsistentInventory() {
        assertDoesNotThrow(() -> BookValidator.validate(book(4, 2)));
    }

    @Test
    void validateShouldRejectNonPositiveTotalCopies() {
        assertThrows(InventoryOperationException.class, () -> BookValidator.validate(book(0, 0)));
    }

    @Test
    void validateShouldRejectNegativeAvailableCopies() {
        assertThrows(InventoryOperationException.class, () -> BookValidator.validate(book(3, -1)));
    }

    @Test
    void validateShouldRejectAvailableCopiesGreaterThanTotalCopies() {
        assertThrows(InventoryOperationException.class, () -> BookValidator.validate(book(2, 3)));
    }

    private Book book(int totalCopies, int availableCopies) {
        return Book.builder()
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("9780132350884")
                .totalCopies(totalCopies)
                .availableCopies(availableCopies)
                .build();
    }
}
