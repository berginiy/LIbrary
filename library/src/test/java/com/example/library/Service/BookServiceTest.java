package com.example.library.Service;

import com.example.library.DTO.BookDTO;
import com.example.library.Exception.BookNotFoundException;
import com.example.library.Exception.IsbnAlreadyExistsException;
import com.example.library.Model.Book;
import com.example.library.Repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private UUID bookId;
    private Book book;
    private BookDTO bookDTO;

    @BeforeEach
    void setUp() {
        bookId = UUID.randomUUID();
        book = new Book(bookId, "Мастер и Маргарита", "Булгаков", 1967, "978-5-17-090944-4");
        bookDTO = new BookDTO(bookId, "Мастер и Маргарита", "Булгаков", 1967, "978-5-17-090944-4");
    }

    @Test
    void findAllBooks_returnsListOfDTO() {
        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<BookDTO> result = bookService.findAllBooks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Мастер и Маргарита");
    }

    @Test
    void findBookById_existingId_returnsDTO() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        BookDTO result = bookService.findBookById(bookId);

        assertThat(result.getId()).isEqualTo(bookId);
        assertThat(result.getIsbn()).isEqualTo("978-5-17-090944-4");
    }

    @Test
    void findBookById_notFound_throwsException() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.findBookById(bookId))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void createBook_uniqueIsbn_savesAndReturnsDTO() {
        when(bookRepository.existsByIsbn(bookDTO.getIsbn())).thenReturn(false);

        BookDTO result = bookService.createBook(bookDTO);

        verify(bookRepository).save(any(Book.class));
        assertThat(result.getTitle()).isEqualTo(bookDTO.getTitle());
    }

    @Test
    void createBook_duplicateIsbn_throwsException() {
        when(bookRepository.existsByIsbn(bookDTO.getIsbn())).thenReturn(true);

        assertThatThrownBy(() -> bookService.createBook(bookDTO))
                .isInstanceOf(IsbnAlreadyExistsException.class);
    }

    @Test
    void deleteBook_notFound_throwsException() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.deleteBook(bookId))
                .isInstanceOf(BookNotFoundException.class);

        verify(bookRepository, never()).delete(any());
    }

    @Test
    void deleteBook_exists_callsDelete() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        bookService.deleteBook(bookId);

        verify(bookRepository).delete(bookId);
    }
}