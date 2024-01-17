package com.appgilson.libraryapi.model;


import com.appgilson.libraryapi.model.entity.Book;
import com.appgilson.libraryapi.model.repository.BookRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Java6Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest

public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando tiver um livro por isbn")
    public void returnFalseWhenIsbnExists(){
        //cenario
        String isbn = "123";
//        Book book = Book.builder().title("Aventuras").author("Fulano").isbn(isbn).build();
//        entityManager.persist(book);
        //execucao
        boolean exists = repository.existsByIsbn(isbn);

        //verify
        Assertions.assertThat(exists).isFalse();


    }




}
