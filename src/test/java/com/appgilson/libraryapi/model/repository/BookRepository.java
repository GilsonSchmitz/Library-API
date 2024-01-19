package com.appgilson.libraryapi.model.repository;

import com.appgilson.libraryapi.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);

    void delete(Book book);
}
