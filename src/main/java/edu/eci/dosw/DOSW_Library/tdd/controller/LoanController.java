package edu.eci.dosw.DOSW_Library.tdd.controller;

import edu.eci.dosw.DOSW_Library.tdd.controller.dto.CreateLoanRequest;
import edu.eci.dosw.DOSW_Library.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.DOSW_Library.tdd.controller.mapper.LoanMapper;
import edu.eci.dosw.DOSW_Library.tdd.core.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
@Tag(name = "Loans", description = "Operaciones sobre prestamos")
public class LoanController {
    private final LoanService loanService;

    @Operation(summary = "Crear un prestamo")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanDTO createLoan(@Valid @RequestBody CreateLoanRequest request) {
        return LoanMapper.toDTO(loanService.createLoan(request.getUserId(), request.getBookId()));
    }

    @Operation(summary = "Registrar devolucion de un prestamo")
    @PutMapping("/{id}/return")
    public LoanDTO returnBook(@PathVariable String id) {
        return LoanMapper.toDTO(loanService.returnBook(id));
    }

    @Operation(summary = "Listar prestamos")
    @GetMapping
    public List<LoanDTO> getLoans() {
        return loanService.getAllLoans().stream()
                .map(LoanMapper::toDTO)
                .toList();
    }

    @Operation(summary = "Consultar un prestamo por id")
    @GetMapping("/{id}")
    public LoanDTO getLoan(@PathVariable String id) {
        return LoanMapper.toDTO(loanService.getLoanById(id));
    }
}
