package com.library.borrowing.dto;

/** Copie locale de la structure d'un livre renvoyee par Book Service. */
public record BookDto(Long id,
                      String title,
                      String author,
                      String category,
                      Integer publicationYear,
                      boolean available) {

    public BookDto withAvailable(boolean newAvailable) {
        return new BookDto(id, title, author, category, publicationYear, newAvailable);
    }
}
