package com.appgilson.libraryapi.model.repository;

import com.appgilson.libraryapi.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);

//    void delete(Book book);

    Optional<Book> findByIsbn(String isbn);

}
