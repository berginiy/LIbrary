package com.example.library.Exception;

import java.util.UUID;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(UUID id) {
        super("Книга с id " + id + " не найдена");
    }
}