package com.appgilson.libraryapi.service.impl;

import com.appgilson.libraryapi.exception.BusinessException;
import com.appgilson.libraryapi.model.entity.Book;
import com.appgilson.libraryapi.model.repository.BookRepository;
import com.appgilson.libraryapi.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }


    @Override
    public Book save(Book book) {
        if (repository.existsByIsbn(book.getIsbn())) {
           throw new BusinessException("ISBN JA CADASTRADO!");
        }
   return repository.save(book);
    }
}