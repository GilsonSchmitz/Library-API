package com.appgilson.libraryapi.api.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDto {

    private Long id;
    @NotEmpty
    private String isbn;
    @NotEmpty
    private String customer;
   @NotEmpty
    private String customerEmail;
   @NotEmpty
    private BookDto book;

}
