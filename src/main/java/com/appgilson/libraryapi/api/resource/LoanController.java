package com.appgilson.libraryapi.api.resource;

import com.appgilson.libraryapi.api.dto.BookDto;
import com.appgilson.libraryapi.api.dto.LoanDto;
import com.appgilson.libraryapi.api.dto.LoanFilterDto;
import com.appgilson.libraryapi.api.dto.ReturnedLoanDto;
import com.appgilson.libraryapi.model.entity.Book;
import com.appgilson.libraryapi.model.entity.Loan;
import com.appgilson.libraryapi.service.BookService;
import com.appgilson.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;




@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor

public class LoanController {

    private final LoanService service;
    private final BookService bookService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDto dto) {
        Book book = bookService.getBookByIsbn(dto.getIsbn())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));
        Loan entity = Loan.builder()
                .book(book)
                .customer(dto.getCustomer())
                .loanDate(LocalDate.now())
                .build();

        entity = service.save(entity);
        return entity.getId();
    }

    @PatchMapping("{id}")
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDto dto) {

   Loan loan = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
   loan.setReturned(dto.getReturned());
   service.update(loan);

    }

    @GetMapping
    public Page<LoanDto> find(LoanFilterDto dto, Pageable pageRequest){
        Page<Loan> result = service.find(dto, pageRequest);
        List<LoanDto> loans = result
                .getContent()
                .stream()
                .map( entity -> {
                    Book book = entity.getBook();
                   BookDto bookDto = modelMapper.map(book, BookDto.class);
                   LoanDto loanDto = modelMapper.map(entity, LoanDto.class);
                   loanDto.setBook(bookDto);
                   return loanDto;
                }).collect(Collectors.toList());
            return new PageImpl<LoanDto>(loans, pageRequest, result.getTotalElements());

    }

}
