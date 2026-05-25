package com.example.library.Service;

import com.example.library.DTO.BookDTO;
import com.example.library.Exception.BookNotFoundException;
import com.example.library.Exception.IsbnAlreadyExistsException;
import com.example.library.Model.Book;
import com.example.library.Repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookDTO> findAllBooks() {
        log.info("Запрос списка всех книг");
        return bookRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public BookDTO findBookById(UUID id) {
        log.info("Поиск книги по id: {}", id);
        return bookRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    public BookDTO createBook(BookDTO bookDTO) {
        log.info("Создание новой книги: {}", bookDTO.getTitle());

        // Проверка уникальности ISBN
        if (bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            throw new IsbnAlreadyExistsException(bookDTO.getIsbn());
        }

        Book book = convertToEntity(bookDTO);
        bookRepository.save(book);
        log.info("Книга создана с id: {}", book.getId());
        return convertToDTO(book);
    }

    public BookDTO updateBook(UUID id, BookDTO bookDTO) {
        log.info("Обновление книги с id: {}", id);

        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        if (!existingBook.getIsbn().equals(bookDTO.getIsbn())
                && bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            throw new IsbnAlreadyExistsException(bookDTO.getIsbn());
        }

        Book book = convertToEntity(bookDTO);
        book.setId(id);
        book.setCreatedAt(existingBook.getCreatedAt());
        bookRepository.update(book);

        log.info("Книга с id: {} успешно обновлена", id);
        return convertToDTO(book);
    }

    public void deleteBook(UUID id) {
        log.info("Удаление книги с id: {}", id);

        if (bookRepository.findById(id).isEmpty()) {
            throw new BookNotFoundException(id);
        }

        bookRepository.delete(id);
        log.info("Книга с id: {} удалена", id);
    }


    private BookDTO convertToDTO(Book book) {
        return new BookDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getYear(),
                book.getIsbn()
        );
    }

    private Book convertToEntity(BookDTO dto) {
        return new Book(
                dto.getId(),
                dto.getTitle(),
                dto.getAuthor(),
                dto.getYear(),
                dto.getIsbn()
        );
    }

    public List<BookDTO> searchBooks(String title, String author, Integer yearFrom, Integer yearTo) {
        log.info("Поиск книг: title={}, author={}, yearFrom={}, yearTo={}", title, author, yearFrom, yearTo);

        if (title != null && !title.isBlank()) {
            return bookRepository.searchByTitle(title).stream()
                    .map(this::convertToDTO).toList();
        }
        if (author != null && !author.isBlank()) {
            return bookRepository.searchByAuthor(author).stream()
                    .map(this::convertToDTO).toList();
        }
        if (yearFrom != null && yearTo != null) {
            return bookRepository.searchByYearRange(yearFrom, yearTo).stream()
                    .map(this::convertToDTO).toList();
        }
        return findAllBooks();
    }
}