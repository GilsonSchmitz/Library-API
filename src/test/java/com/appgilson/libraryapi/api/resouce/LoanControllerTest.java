package com.appgilson.libraryapi.api.resouce;

import com.appgilson.libraryapi.api.dto.BookDto;
import com.appgilson.libraryapi.api.dto.LoanDto;
import com.appgilson.libraryapi.api.dto.ReturnedLoanDto;
import com.appgilson.libraryapi.exception.BusinessException;
import com.appgilson.libraryapi.model.entity.Book;
import com.appgilson.libraryapi.model.entity.Loan;
import com.appgilson.libraryapi.service.BookService;
import com.appgilson.libraryapi.service.LoanService;
import com.appgilson.libraryapi.service.LoanServiceTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.appgilson.libraryapi.api.resource.LoanController;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.appgilson.libraryapi.api.dto.LoanFilterDto;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static com.appgilson.libraryapi.api.resouce.BookControllerTest.BOOK_API;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc

public class LoanControllerTest {

    static final String LOAN_API = "/api/loans";

    @Autowired
    MockMvc mvc;
    @MockBean
    private BookService bookService;
    @MockBean
    private LoanService loanService;

    @Test
    @DisplayName("Deve realizar um emprestimo")
    public void createLoanTest() throws Exception {

        LoanDto dto = LoanDto.builder().isbn("123").customerEmail("customer@email;com").customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder().id(1l).isbn("123").build();
        BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.of(Book.builder().id(1l).isbn("123").build()));
        Loan loan = Loan.builder().id(1l).customer("Fulano").book(book).loanDate(LocalDate.now()).build();
        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().string("1"))
        ;

    }

    @Test
    @DisplayName("Deve retornar erro fazer emprestimo")
    public void invalidIsbnCreateLoanTest() throws Exception {

        LoanDto dto = LoanDto.builder().isbn("123").customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.empty());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book not found for passed isbn"));
    }

    @Test
    @DisplayName("Deve retornar erro fazer emprestimo")
    public void loanedBookErrorOnCreateLoanTest() throws Exception {

        LoanDto dto = LoanDto.builder().isbn("123").customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder().id(1l).isbn("123").build();
        BDDMockito.given(bookService.getBookByIsbn("123"))
                .willReturn(Optional.of(book));

        BDDMockito.given(loanService.save(Mockito.any(Loan.class)))
                .willThrow(new BusinessException("Book Already loaned"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book Already loaned"));
    }


    @Test
    @DisplayName("Deve retornar um livro")
    public void returnBookTest() throws Exception {
//        cenario (returned:true)
        ReturnedLoanDto dto = ReturnedLoanDto.builder().returned(true).build();
        Loan loan = Loan.builder().id(1l).build();
        BDDMockito.given(loanService.getById(Mockito.anyLong()))
                .willReturn(Optional.of(Loan.builder().id(1l).build()));

        String json = new ObjectMapper().writeValueAsString(dto);

        mvc.perform(
                patch(LOAN_API.concat("/1"))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)).andExpect(status().isOk());

        Mockito.verify(loanService, Mockito.times(1)).update(loan);

    }

    @Test
    @DisplayName("Deve retornar 404 quando tentar devolver um inexistente")
    public void returnInexistBookTest() throws Exception {
//        cenario (returned:true)
        ReturnedLoanDto dto = ReturnedLoanDto.builder().returned(true).build();
        String json = new ObjectMapper().writeValueAsString(dto);
        BDDMockito.given(loanService.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());
        mvc.perform(
                patch(LOAN_API.concat("/1"))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)).andExpect(status().isNotFound());
    }
    @Test
    @DisplayName("Deve filtrar emprestimos")
    public void findLoansTest() throws Exception {

        Long id = 1L;
        Loan loan = LoanServiceTest.createLoan();
        loan.setId(id);
        Book book = Book.builder().id(1l).isbn("312").build();
        loan.setBook(book);

        BDDMockito.given( loanService.find(Mockito.any(LoanFilterDto.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0, 100), 1));

        String queryString = String.format("?isbn=%s&customer=%s&page=0&size=100",
                book.getIsbn(), book.getAuthor());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(LOAN_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }

}