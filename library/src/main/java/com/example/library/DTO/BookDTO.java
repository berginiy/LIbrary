package com.example.library.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class BookDTO {
    private UUID id;

    @NotBlank(message = "Название книги не может быть пустым")
    private String title;

    @NotBlank(message = "Автор не может быть пустым")
    private String author;

    @NotNull(message = "Год издания обязателен")
    @Min(value = 1, message = "Год издания должен быть больше 0")
    private Integer year;

    @NotBlank(message = "ISBN не может быть пустым")
    private String isbn;

    public BookDTO() {}

    public BookDTO(UUID id, String title, String author, Integer year, String isbn) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.isbn = isbn;
    }

    public BookDTO(String title, String author, Integer year, String isbn) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.isbn = isbn;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @Override
    public String toString() {
        return "BookDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", year=" + year +
                ", isbn='" + isbn + '\'' +
                '}';
    }
}
