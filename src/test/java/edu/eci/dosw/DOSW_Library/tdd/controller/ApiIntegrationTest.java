package edu.eci.dosw.DOSW_Library.tdd.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.dosw.DOSW_Library.persistence.entity.UserEntity;
import edu.eci.dosw.DOSW_Library.persistence.repository.BookRepository;
import edu.eci.dosw.DOSW_Library.persistence.repository.LoanRepository;
import edu.eci.dosw.DOSW_Library.persistence.repository.UserRepository;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .name("Default Librarian")
                .username("admin")
                .password(passwordEncoder.encode("Admin123*"))
                .role(Role.LIBRARIAN)
                .build());
    }

    @Test
    void shouldAuthenticateAndReturnJwt() throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "admin",
                                  "password": "Admin123*"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("LIBRARIAN"))
                .andReturn();

        JsonNode json = readBody(result);
        assertNotNull(json.get("token").asText());
    }

    @Test
    void shouldRejectAccessWithoutToken() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectAccessWithInvalidToken() throws Exception {
        mockMvc.perform(get("/books").header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void librarianShouldCreateUserAndBookPersistingThem() throws Exception {
        String librarianToken = login("admin", "Admin123*");

        MvcResult userResult = mockMvc.perform(post("/users")
                        .header(HttpHeaders.AUTHORIZATION, bearer(librarianToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Juan Perez",
                                  "username": "juan",
                                  "password": "User123*",
                                  "role": "USER"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("juan"))
                .andReturn();

        MvcResult bookResult = mockMvc.perform(post("/books")
                        .header(HttpHeaders.AUTHORIZATION, bearer(librarianToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Clean Code",
                                  "author": "Robert C. Martin",
                                  "isbn": "9780132350884",
                                  "totalCopies": 5,
                                  "availableCopies": 5
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isbn").value("9780132350884"))
                .andReturn();

        JsonNode createdUser = readBody(userResult);
        JsonNode createdBook = readBody(bookResult);

        assertEquals(2, userRepository.count());
        assertEquals(1, bookRepository.count());
        assertFalse(userRepository.findById(UUID.fromString(createdUser.get("id").asText())).isEmpty());
        assertFalse(bookRepository.findById(UUID.fromString(createdBook.get("id").asText())).isEmpty());
    }

    @Test
    void userShouldNotBeAbleToCreateBooks() throws Exception {
        String librarianToken = login("admin", "Admin123*");
        createUser(librarianToken, "maria", "Maria123*", "Maria Lopez");
        String userToken = login("maria", "Maria123*");

        mockMvc.perform(post("/books")
                        .header(HttpHeaders.AUTHORIZATION, bearer(userToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Domain-Driven Design",
                                  "author": "Eric Evans",
                                  "isbn": "9780321125217",
                                  "totalCopies": 3,
                                  "availableCopies": 3
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void userShouldBorrowAndReturnBookUpdatingInventoryInDatabase() throws Exception {
        String librarianToken = login("admin", "Admin123*");
        createUser(librarianToken, "juan", "User123*", "Juan Perez");
        String userToken = login("juan", "User123*");
        String bookId = createBook(librarianToken, "Clean Code", "Robert C. Martin", "9780132350884", 2, 2);

        MvcResult loanResult = mockMvc.perform(post("/loans")
                        .header(HttpHeaders.AUTHORIZATION, bearer(userToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bookId": "%s"
                                }
                                """.formatted(bookId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andReturn();

        String loanId = readBody(loanResult).get("id").asText();

        mockMvc.perform(get("/loans/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(userToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookId").value(bookId));

        assertEquals(1, loanRepository.count());
        assertEquals(1, bookRepository.findById(UUID.fromString(bookId)).orElseThrow().getAvailableCopies());

        mockMvc.perform(put("/loans/" + loanId + "/return")
                        .header(HttpHeaders.AUTHORIZATION, bearer(userToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETURNED"));

        assertEquals(2, bookRepository.findById(UUID.fromString(bookId)).orElseThrow().getAvailableCopies());
    }

    private String login(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "%s"
                                }
                                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andReturn();
        return readBody(result).get("token").asText();
    }

    private void createUser(String librarianToken, String username, String password, String name) throws Exception {
        mockMvc.perform(post("/users")
                        .header(HttpHeaders.AUTHORIZATION, bearer(librarianToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "%s",
                                  "username": "%s",
                                  "password": "%s",
                                  "role": "USER"
                                }
                                """.formatted(name, username, password)))
                .andExpect(status().isCreated());
    }

    private String createBook(String token, String title, String author, String isbn, int totalCopies, int availableCopies)
            throws Exception {
        MvcResult result = mockMvc.perform(post("/books")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "%s",
                                  "author": "%s",
                                  "isbn": "%s",
                                  "totalCopies": %d,
                                  "availableCopies": %d
                                }
                                """.formatted(title, author, isbn, totalCopies, availableCopies)))
                .andExpect(status().isCreated())
                .andReturn();
        return readBody(result).get("id").asText();
    }

    private JsonNode readBody(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
