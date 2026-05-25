package com.example.library.Exception;

public class IsbnAlreadyExistsException extends RuntimeException {
    public IsbnAlreadyExistsException(String isbn) {
        super("Книга с ISBN " + isbn + " уже существует");
    }
}