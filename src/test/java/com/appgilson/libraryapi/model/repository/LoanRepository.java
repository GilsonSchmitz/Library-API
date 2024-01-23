package com.appgilson.libraryapi.model.repository;

import com.appgilson.libraryapi.model.entity.Book;
import com.appgilson.libraryapi.model.entity.Loan;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query(value = " select case when (count(1)>0) then true else false end" +
            " from Loan l where l.book = :book and (l.returned is null or l.returned = false) ")
    boolean existsByBookAndNotReturned(@Param("book")Book book);


    @Query(value = "select l from Loan as l join l.book as b where b.isbn = :isbn or l.customer =:customer" )
    Page<Loan> findByBookIsbnOrCostumer(
            @Param("isbn") String isbn, @Param("customer") String customer, Pageable pageable);

    Page<Loan> findByBook(Book book, Pageable pageable);

    @Query(" select l from Loan l where l.loanDate <= :threeDaysAgo and (l.returned is null or l.returned = false) ")
    List<Loan> findByLoanDateLessThanAndNotReturned(@Param("threeDaysAgo")LocalDate threeDaysAgo);
}
