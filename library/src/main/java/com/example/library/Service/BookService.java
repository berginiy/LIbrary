package com.example.library.Service;

import com.example.library.DTO.BookDTO;
import com.example.library.Model.Book;
import com.example.library.Repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookDTO> findAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public BookDTO findBookById(UUID id) {
        return bookRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    public BookDTO createBook(BookDTO bookDTO) {
        Book book = convertToEntity(bookDTO);
        bookRepository.save(book);
        return convertToDTO(book);
    }

    public BookDTO updateBook(UUID id, BookDTO bookDTO) {
        Book book = convertToEntity(bookDTO);
        book.setId(id);
        bookRepository.update(book);
        return convertToDTO(book);
    }

    public void deleteBook(UUID id) {
        bookRepository.delete(id);
    }

    private BookDTO convertToDTO(Book book) {
        return new BookDTO(book.getId(), book.getTitle(), book.getAuthor(), book.getYear(), book.getIsbn());
    }

    private Book convertToEntity(BookDTO bookDTO) {
        return new Book(bookDTO.getId(), bookDTO.getTitle(), bookDTO.getAuthor(), bookDTO.getYear(), bookDTO.getIsbn());
    }
}
