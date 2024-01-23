package com.appgilson.libraryapi.api.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data

public class BookDto {

    private Integer id;

    @NotEmpty
    private String author;
    @NotEmpty
    private String title;
    @NotEmpty
    private String isbn;

}


