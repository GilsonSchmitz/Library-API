package com.appgilson.libraryapi.service;

import com.appgilson.libraryapi.api.dto.LoanFilterDto;
import com.appgilson.libraryapi.exception.BusinessException;
import com.appgilson.libraryapi.model.entity.Book;
import com.appgilson.libraryapi.model.entity.Loan;
import com.appgilson.libraryapi.model.repository.LoanRepository;
import com.appgilson.libraryapi.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.StatusResultMatchersExtensionsKt.isEqualTo;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {


    LoanService service;

    @MockBean
    LoanRepository repository;

    @BeforeEach
    public void setUp() {
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um emprestimo")
    public void saveLoanTest() {
        String customer = "Fulano";
        Book book = Book.builder().id(1l).build();

        Loan savingLoan = Loan.builder()
                .book(Book.builder().id(1l).build())
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();

        Loan savedLoan = Loan.builder().id(1l)
                .loanDate(LocalDate.now())
                .customer(customer)
                .book(book)
                .build();

        when(repository.existsByBookAndNotReturned(book)).thenReturn(false);
        when(repository.save(savingLoan)).thenReturn(savedLoan);

        Loan loan = service.save(savingLoan);

        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());

    }

    @Test
    @DisplayName("Deve dar erro ao salvar um emprestimo com o livro emprestado")
    public void loanedBookSaveTest() {
        String customer = "Fulano";
        Book book = Book.builder().id(1l).build();

        Loan savingLoan = Loan.builder()
                .book(Book.builder().id(1l).build())
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();
        when(repository.existsByBookAndNotReturned(book)).thenReturn(true);

        Throwable exception = catchThrowable(() -> service.save(savingLoan));

        assertThat(exception).isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned");

        verify(repository, never()).save(savingLoan);
    }

    @Test
    @DisplayName("Deve obter as inf de emprestimo")
    public void getLoanDetaisTest() {
        Long id = 1l;
        Loan loan = createLoan();
        loan.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));

        Optional<Loan> result = service.getById(id);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        verify(repository).findById(id);

    }
    @Test
    @DisplayName("Deve atualizar um emprestimo")
    public void updateLoanTest(){
        Loan loan = createLoan();
        loan.setId(1l);
        loan.setReturned(true);
        when(repository.save(loan)).thenReturn(loan);
        Loan updatedLoan = service.update(loan);

        assertThat(updatedLoan.getReturned()).isTrue();
        verify(repository).save(loan);



}


    @Test
    @DisplayName("Deve filtrar emprestimos pelas propriedades")
    public void findLoanTest() {

        LoanFilterDto loanFilterDto = LoanFilterDto.builder().customer("Fulano").isbn("321").build();
        Loan loan = createLoan();
        loan.setId(1l);
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Loan> lista = Arrays.asList(loan);
        Page<Loan> page = new PageImpl<Loan>(lista, pageRequest,lista.size());
        when(repository.findByBookIsbnOrCostumer(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(PageRequest.class)))


                .thenReturn(page);

        Page<Loan> result = service.find(loanFilterDto,pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }


    public static Loan createLoan(){
        Book book = Book.builder().id(1l).build();
        String customer = "Fulano";
        return Loan.builder()
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();

    }

}
