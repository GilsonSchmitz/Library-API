package com.appgilson.libraryapi.service;

import com.appgilson.libraryapi.exception.BusinessException;
import com.appgilson.libraryapi.model.entity.Book;
import com.appgilson.libraryapi.model.repository.BookRepository;
import com.appgilson.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

BookService service;
@MockBean
BookRepository repository;

@BeforeEach
public void setUp(){
        this.service = new BookServiceImpl(repository);
    }

@Test
@DisplayName("Deve salvar um livro")
    public void saveBookTest(){
    //cenario
    Book book = Book.builder().isbn("123123").author("Fulano").title("As aventuras").build();
    Mockito.when(repository.save(book)).thenReturn(Book.builder()
            .id(11L)
            .isbn("123")
            .title("As aventuras")
            .author("Fulano")
            .build());
    //execucao
    Book savedBook = service.save(book);

    //verificacao
    assertThat(savedBook.getId()).isNotNull();
    assertThat(savedBook.getIsbn()).isEqualTo("123");
    assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
    assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
}


    private Book createValidBook() {
        return Book.builder().isbn("123123").author("Fulano").title("As aventuras").build();
    }
    @Test
    @DisplayName("Deve lançar erro de negocio, isbn duplicado")
    public void shouldNotSaveABookWithDouble() {
// cenario
Book book = createValidBook();
Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
//execucao
Throwable exception = Assertions.catchThrowable( () -> service.save(book));

//verif
        Assertions.assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("ISBN JA CADASTRADO!");

        Mockito.verify(repository, Mockito.never()).save(book);
}




}
