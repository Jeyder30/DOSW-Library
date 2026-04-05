package edu.eci.dosw.DOSW_Library.tdd.controller;

import edu.eci.dosw.DOSW_Library.tdd.controller.dto.ApiErrorDTO;
import edu.eci.dosw.DOSW_Library.tdd.controller.dto.BookRequestDTO;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.DOSW_Library.tdd.core.exception.InvalidCredentialsException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFoundShouldReturnStructuredPayload() {
        ResponseEntity<ApiErrorDTO> response = handler.handleNotFound(
                new BookNotFoundException("Book was not found"),
                request("/books/123")
        );

        assertError(response, HttpStatus.NOT_FOUND, "Book was not found", "/books/123", List.of());
    }

    @Test
    void handleBadRequestShouldReturnStructuredPayload() {
        ResponseEntity<ApiErrorDTO> response = handler.handleBadRequest(
                new IllegalArgumentException("Title is required"),
                request("/books")
        );

        assertError(response, HttpStatus.BAD_REQUEST, "Title is required", "/books", List.of());
    }

    @Test
    void handleUnauthorizedShouldReturnStructuredPayload() {
        ResponseEntity<ApiErrorDTO> response = handler.handleUnauthorized(
                new InvalidCredentialsException("Invalid username or password"),
                request("/auth/login")
        );

        assertError(response, HttpStatus.UNAUTHORIZED, "Invalid username or password", "/auth/login", List.of());
    }

    @Test
    void handleForbiddenShouldMaskInternalAuthorizationDetails() {
        ResponseEntity<ApiErrorDTO> response = handler.handleForbidden(
                new AccessDeniedException("Forbidden"),
                request("/users")
        );

        assertError(
                response,
                HttpStatus.FORBIDDEN,
                "You do not have permission to access this resource",
                "/users",
                List.of()
        );
    }

    @Test
    void handleValidationShouldCollectFieldErrors() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new BookRequestDTO(), "bookRequestDTO");
        bindingResult.addError(new FieldError("bookRequestDTO", "title", "Title is required"));
        bindingResult.addError(new FieldError("bookRequestDTO", "isbn", "ISBN is required"));

        Method method = DummyController.class.getDeclaredMethod("create", BookRequestDTO.class);
        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(new MethodParameter(method, 0), bindingResult);

        ResponseEntity<ApiErrorDTO> response = handler.handleValidation(exception, request("/books"));

        assertError(
                response,
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                "/books",
                List.of("title: Title is required", "isbn: ISBN is required")
        );
    }

    @Test
    void handleUnexpectedShouldReturnInternalServerErrorPayload() {
        ResponseEntity<ApiErrorDTO> response = handler.handleUnexpected(
                new RuntimeException("Boom"),
                request("/loans")
        );

        assertError(response, HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", "/loans", List.of());
    }

    private MockHttpServletRequest request(String path) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(path);
        return request;
    }

    private void assertError(
            ResponseEntity<ApiErrorDTO> response,
            HttpStatus expectedStatus,
            String expectedMessage,
            String expectedPath,
            List<String> expectedDetails
    ) {
        assertEquals(expectedStatus, response.getStatusCode());

        ApiErrorDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(expectedStatus.value(), body.getStatus());
        assertEquals(expectedStatus.getReasonPhrase(), body.getError());
        assertEquals(expectedMessage, body.getMessage());
        assertEquals(expectedPath, body.getPath());
        assertEquals(expectedDetails, body.getDetails());
        assertNotNull(body.getTimestamp());
    }

    private static final class DummyController {
        @SuppressWarnings("unused")
        private void create(BookRequestDTO request) {
        }
    }
}
