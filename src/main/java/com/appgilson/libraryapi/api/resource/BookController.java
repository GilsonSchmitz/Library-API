package com.appgilson.libraryapi.api.resource;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.appgilson.libraryapi.api.dto.BookDto;
import com.appgilson.libraryapi.api.exception.ApiErrors;
import com.appgilson.libraryapi.exception.BusinessException;
import com.appgilson.libraryapi.model.entity.Book;
import com.appgilson.libraryapi.service.BookService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService service;
    private ModelMapper modelMapper;

    public BookController(BookService service, ModelMapper mapper) {

        this.service = service;
        this.modelMapper = mapper;

    }

    private Book updatingBook;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)

    public BookDto create(@RequestBody @Valid BookDto dto) {
        Book entity = modelMapper.map(dto, Book.class);
        entity = service.save(entity);
        return modelMapper.map(entity, BookDto.class);
    }

    @GetMapping("{id}")
    public BookDto get(@PathVariable Long id) {
        return service
                .getById(id)
                .map(book -> modelMapper.map(book, BookDto.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

//        Book book = service.getById(id).get();
//        return modelMapper.map(book, BookDto.class);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Book book = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.delete(book);
    }
@PutMapping("{id}")
    public BookDto update(@PathVariable Long id, BookDto dto) {
        Book book = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        book.setAuthor(dto.getAuthor());
        book.setTitle(dto.getTitle());
        book = service.update(book);
       return modelMapper.map(book, BookDto.class);
    }
    @GetMapping
    public Page<BookDto> find(BookDto  dto, Pageable pageRequest) {
        Book filter = modelMapper.map(dto, Book.class);
        Page<Book> result = service.find(filter, pageRequest);
        List<BookDto> list = result.getContent().stream().map(entity -> modelMapper.map(entity, BookDto.class))
                .collect(Collectors.toList());
    return new PageImpl<BookDto>(list, pageRequest,  result.getTotalElements());
    }
}


