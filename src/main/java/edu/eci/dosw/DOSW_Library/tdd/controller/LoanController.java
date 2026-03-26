package edu.eci.dosw.DOSW_Library.tdd.controller;

import edu.eci.dosw.DOSW_Library.security.AppUserPrincipal;
import edu.eci.dosw.DOSW_Library.tdd.controller.dto.LoanRequestDTO;
import edu.eci.dosw.DOSW_Library.tdd.controller.dto.LoanResponseDTO;
import edu.eci.dosw.DOSW_Library.tdd.controller.mapper.LoanApiMapper;
import edu.eci.dosw.DOSW_Library.tdd.core.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
@Tag(name = "Loans", description = "Operaciones sobre prestamos")
public class LoanController {
    private final LoanService loanService;
    private final LoanApiMapper loanApiMapper;

    @Operation(summary = "Crear un prestamo")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'USER')")
    public LoanResponseDTO createLoan(@AuthenticationPrincipal AppUserPrincipal principal, @Valid @RequestBody LoanRequestDTO request) {
        return loanApiMapper.toResponse(loanService.createLoan(
                principal.getUsername(),
                principal.getRole(),
                request.getUserId(),
                request.getBookId()
        ));
    }

    @Operation(summary = "Registrar devolucion de un prestamo")
    @PutMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'USER')")
    public LoanResponseDTO returnBook(@AuthenticationPrincipal AppUserPrincipal principal, @PathVariable String id) {
        return loanApiMapper.toResponse(loanService.returnBook(id, principal.getUsername(), principal.getRole()));
    }

    @Operation(summary = "Listar prestamos")
    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public List<LoanResponseDTO> getLoans() {
        return loanService.getAllLoans().stream()
                .map(loanApiMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Consultar un prestamo por id")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public LoanResponseDTO getLoan(@PathVariable String id) {
        return loanApiMapper.toResponse(loanService.getLoanById(id));
    }

    @Operation(summary = "Consultar los prestamos del usuario autenticado")
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'USER')")
    public List<LoanResponseDTO> getMyLoans(@AuthenticationPrincipal AppUserPrincipal principal) {
        return loanService.getLoansForUser(principal.getUsername()).stream()
                .map(loanApiMapper::toResponse)
                .toList();
    }
}
