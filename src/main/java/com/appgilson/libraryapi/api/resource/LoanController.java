package com.appgilson.libraryapi.api.resource;

import com.appgilson.libraryapi.api.dto.LoanDto;
import com.appgilson.libraryapi.model.entity.Book;
import com.appgilson.libraryapi.model.entity.Loan;
import com.appgilson.libraryapi.service.BookService;
import com.appgilson.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;


@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor

public class LoanController {

    private final LoanService service;
    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDto dto){
        Book book = bookService.getBookByIsbn(dto.getIsbn())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));
        Loan entity = Loan.builder()
                .book(book)
                .customer(dto.getCostumer())
                .loanDate(LocalDate.now())
                .build();

        entity = service.save(entity);
        return entity.getId();

    }



}
