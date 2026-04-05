package edu.eci.dosw.DOSW_Library.tdd.core.exception;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DomainExceptionsTest {

    @ParameterizedTest(name = "{0} should preserve the provided message")
    @MethodSource("exceptionFactories")
    void exceptionsShouldPreserveMessage(String ignoredName, Function<String, RuntimeException> factory) {
        String message = "expected-message";

        RuntimeException exception = factory.apply(message);

        assertEquals(message, exception.getMessage());
    }

    private static Stream<Object[]> exceptionFactories() {
        return Stream.of(
                new Object[]{"BookNotAvailableException", (Function<String, RuntimeException>) BookNotAvailableException::new},
                new Object[]{"BookNotFoundException", (Function<String, RuntimeException>) BookNotFoundException::new},
                new Object[]{"DuplicateUsernameException", (Function<String, RuntimeException>) DuplicateUsernameException::new},
                new Object[]{"InvalidCredentialsException", (Function<String, RuntimeException>) InvalidCredentialsException::new},
                new Object[]{"InventoryOperationException", (Function<String, RuntimeException>) InventoryOperationException::new},
                new Object[]{"LoanAlreadyReturnedException", (Function<String, RuntimeException>) LoanAlreadyReturnedException::new},
                new Object[]{"LoanLimitExceededException", (Function<String, RuntimeException>) LoanLimitExceededException::new},
                new Object[]{"LoanNotFoundException", (Function<String, RuntimeException>) LoanNotFoundException::new},
                new Object[]{"UserNotFoundException", (Function<String, RuntimeException>) UserNotFoundException::new}
        );
    }
}
