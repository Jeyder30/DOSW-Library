package edu.eci.dosw.DOSW_Library.tdd.controller;

import edu.eci.dosw.DOSW_Library.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.DOSW_Library.tdd.controller.mapper.LoanMapper;
import edu.eci.dosw.DOSW_Library.tdd.core.model.Loan;
import edu.eci.dosw.DOSW_Library.tdd.core.service.BookService;
import edu.eci.dosw.DOSW_Library.tdd.core.service.LoanService;
import edu.eci.dosw.DOSW_Library.tdd.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @PostMapping
    public LoanDTO createLoan(@RequestParam String userId, @RequestParam String bookId) {
        Loan loan = loanService.createLoan(userId, bookId);
        return LoanMapper.toDTO(loan);
    }

    @PutMapping("/{id}/return")
    public void returnBook(@PathVariable String id) {
        loanService.returnBook(id);
    }

    @GetMapping
    public List<Loan> getLoans() {
        return loanService.getAllLoans();
    }
}
