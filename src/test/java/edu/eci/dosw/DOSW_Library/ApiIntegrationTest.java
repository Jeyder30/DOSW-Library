package edu.eci.dosw.DOSW_Library;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Role;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Status;
import edu.eci.dosw.DOSW_Library.tdd.persistence.relational.entity.UserEntity;
import edu.eci.dosw.DOSW_Library.tdd.persistence.relational.repository.BookRepository;
import edu.eci.dosw.DOSW_Library.tdd.persistence.relational.repository.LoanRepository;
import edu.eci.dosw.DOSW_Library.tdd.persistence.relational.repository.UserRepository;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    void postAuthLoginUsesCredentialsResolvedFromDatabase() throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "admin",
                                  "password": "Admin123*"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = readBody(result);
        UUID userId = UUID.fromString(json.get("userId").asText());
        assertTrue(userRepository.findById(userId).isPresent());
        assertEquals("admin", userRepository.findById(userId).orElseThrow().getUsername());
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
    void getUsersAsLibrarianReflectsRowsInDatabase() throws Exception {
        String librarianToken = login("admin", "Admin123*");
        MvcResult result = mockMvc.perform(get("/users")
                        .header(HttpHeaders.AUTHORIZATION, bearer(librarianToken)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode list = objectMapper.readTree(result.getResponse().getContentAsString());
        assertTrue(list.isArray());
        assertEquals(userRepository.count(), list.size());
    }

    @Test
    void getUserByIdAsLibrarianMatchesPersistedRow() throws Exception {
        String librarianToken = login("admin", "Admin123*");
        createUser(librarianToken, "ana", "Ana123*", "Ana Gomez");
        UUID anaId = userRepository.findByUsername("ana").orElseThrow().getId();

        MvcResult result = mockMvc.perform(get("/users/" + anaId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(librarianToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ana Gomez"))
                .andReturn();

        JsonNode body = readBody(result);
        assertEquals("ana", userRepository.findById(UUID.fromString(body.get("id").asText())).orElseThrow().getUsername());
    }

    @Test
    void getCurrentUserMatchesAuthenticatedRowInDatabase() throws Exception {
        String librarianToken = login("admin", "Admin123*");
        createUser(librarianToken, "pedro", "Pedro123*", "Pedro Ruiz");
        String userToken = login("pedro", "Pedro123*");
        UUID pedroId = userRepository.findByUsername("pedro").orElseThrow().getId();

        mockMvc.perform(get("/users/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(userToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pedroId.toString()))
                .andExpect(jsonPath("$.username").value("pedro"));
    }

    @Test
    void getBooksReflectsAllRowsInDatabase() throws Exception {
        String librarianToken = login("admin", "Admin123*");
        createBook(librarianToken, "Libro A", "Autor A", "1111111111111", 1, 1);
        createBook(librarianToken, "Libro B", "Autor B", "2222222222222", 1, 1);

        MvcResult result = mockMvc.perform(get("/books")
                        .header(HttpHeaders.AUTHORIZATION, bearer(librarianToken)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode list = objectMapper.readTree(result.getResponse().getContentAsString());
        assertTrue(list.isArray());
        assertEquals(bookRepository.count(), list.size());
    }

    @Test
    void getAvailableBooksMatchesCopiesAvailableInDatabase() throws Exception {
        String librarianToken = login("admin", "Admin123*");
        createBook(librarianToken, "Agotado", "Autor", "3333333333333", 2, 0);
        createBook(librarianToken, "Disponible", "Autor", "4444444444444", 2, 2);

        long expected = bookRepository.findAll().stream().filter(b -> b.getAvailableCopies() > 0).count();

        MvcResult result = mockMvc.perform(get("/books/available")
                        .header(HttpHeaders.AUTHORIZATION, bearer(librarianToken)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode list = objectMapper.readTree(result.getResponse().getContentAsString());
        assertTrue(list.isArray());
        assertEquals(expected, list.size());
        for (JsonNode book : list) {
            assertTrue(book.get("availableCopies").asInt() > 0);
        }
    }

    @Test
    void getBookByIdMatchesPersistedRow() throws Exception {
        String librarianToken = login("admin", "Admin123*");
        String bookId = createBook(librarianToken, "Refactoring", "Fowler", "5555555555555", 4, 4);

        mockMvc.perform(get("/books/" + bookId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(librarianToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("5555555555555"));

        assertEquals("Refactoring", bookRepository.findById(UUID.fromString(bookId)).orElseThrow().getTitle());
    }

    @Test
    void putBookUpdatesRowInDatabase() throws Exception {
        String librarianToken = login("admin", "Admin123*");
        String bookId = createBook(librarianToken, "Titulo viejo", "Autor", "6666666666666", 1, 1);

        mockMvc.perform(put("/books/" + bookId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(librarianToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Titulo nuevo",
                                  "author": "Autor",
                                  "isbn": "6666666666666",
                                  "totalCopies": 1,
                                  "availableCopies": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Titulo nuevo"));

        assertEquals("Titulo nuevo", bookRepository.findById(UUID.fromString(bookId)).orElseThrow().getTitle());
    }

    @Test
    void getLoansAsLibrarianMatchesRowsInDatabase() throws Exception {
        String librarianToken = login("admin", "Admin123*");
        createUser(librarianToken, "laura", "Laura123*", "Laura Diaz");
        String userToken = login("laura", "Laura123*");
        String bookId = createBook(librarianToken, "Prestamo test", "Autor", "7777777777777", 1, 1);

        mockMvc.perform(post("/loans")
                        .header(HttpHeaders.AUTHORIZATION, bearer(userToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bookId": "%s"
                                }
                                """.formatted(bookId)))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(get("/loans")
                        .header(HttpHeaders.AUTHORIZATION, bearer(librarianToken)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode list = objectMapper.readTree(result.getResponse().getContentAsString());
        assertTrue(list.isArray());
        assertEquals(loanRepository.count(), list.size());
    }

    @Test
    void getLoanByIdAsLibrarianMatchesPersistedRow() throws Exception {
        String librarianToken = login("admin", "Admin123*");
        createUser(librarianToken, "carlos", "Carlos123*", "Carlos Vega");
        String userToken = login("carlos", "Carlos123*");
        String bookId = createBook(librarianToken, "Consulta prestamo", "Autor", "8888888888888", 1, 1);

        MvcResult loanResult = mockMvc.perform(post("/loans")
                        .header(HttpHeaders.AUTHORIZATION, bearer(userToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bookId": "%s"
                                }
                                """.formatted(bookId)))
                .andExpect(status().isCreated())
                .andReturn();

        String loanId = readBody(loanResult).get("id").asText();

        mockMvc.perform(get("/loans/" + loanId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(librarianToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.bookId").value(bookId));

        assertEquals(Status.ACTIVE, loanRepository.findById(UUID.fromString(loanId)).orElseThrow().getStatus());
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

    private String createBook(String token, String title, String author, String isbn, int totalCopies, int availableCopies) throws Exception {
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
