package com.example.library.Service;

import com.example.library.DTO.BookDTO;
import com.example.library.Exception.*;
import com.example.library.Model.Book;
import com.example.library.Repository.BookRepository;
import com.example.library.Service.BookService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock        private BookRepository repo;
    @InjectMocks private BookService    svc;

    private UUID    id;
    private Book    book;
    private BookDTO dto;

    @BeforeEach
    void setUp() {
        id   = UUID.randomUUID();
        book = new Book(id, "Мастер и Маргарита", "Булгаков", 1967, "978-5-17-0");
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());
        dto  = new BookDTO(id, "Мастер и Маргарита", "Булгаков", 1967, "978-5-17-0");
    }

    @Test @DisplayName("findAllBooks: возвращает список DTO")
    void findAllBooks_returnsList() {
        when(repo.findAll()).thenReturn(List.of(book));
        assertThat(svc.findAllBooks()).hasSize(1);
        verify(repo).findAll();
    }

    @Test @DisplayName("findAllBooks: пустой репозиторий → пустой список")
    void findAllBooks_empty() {
        when(repo.findAll()).thenReturn(List.of());
        assertThat(svc.findAllBooks()).isEmpty();
    }

    @Test @DisplayName("searchBooks: все фильтры — вызывает findByFilters")
    void searchBooks_allFilters() {
        when(repo.findByFilters("Мастер", "Булгаков", 1960, 1970)).thenReturn(List.of(book));
        assertThat(svc.searchBooks("Мастер", "Булгаков", 1960, 1970)).hasSize(1);
    }

    @Test @DisplayName("searchBooks: ничего не найдено → пустой список")
    void searchBooks_noMatch() {
        when(repo.findByFilters("Нет", null, null, null)).thenReturn(List.of());
        assertThat(svc.searchBooks("Нет", null, null, null)).isEmpty();
    }

    @Test @DisplayName("findBookById: книга найдена → DTO")
    void findById_found() {
        when(repo.findById(id)).thenReturn(Optional.of(book));
        assertThat(svc.findBookById(id).getId()).isEqualTo(id);
    }

    @Test @DisplayName("findBookById: не найдена → BookNotFoundException")
    void findById_notFound() {
        UUID uid = UUID.randomUUID();
        when(repo.findById(uid)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> svc.findBookById(uid))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test @DisplayName("createBook: новый ISBN → сохраняет и возвращает DTO")
    void createBook_ok() {
        when(repo.existsByIsbn(dto.getIsbn())).thenReturn(false);
        doAnswer(inv -> { Book b = inv.getArgument(0);
            b.setId(UUID.randomUUID()); b.setCreatedAt(LocalDateTime.now());
            b.setUpdatedAt(LocalDateTime.now()); return null;
        }).when(repo).save(any(Book.class));
        assertThat(svc.createBook(dto).getIsbn()).isEqualTo(dto.getIsbn());
    }

    @Test @DisplayName("createBook: дублирующийся ISBN → IsbnAlreadyExistsException")
    void createBook_duplicateIsbn() {
        when(repo.existsByIsbn(dto.getIsbn())).thenReturn(true);
        assertThatThrownBy(() -> svc.createBook(dto))
                .isInstanceOf(IsbnAlreadyExistsException.class);
        verify(repo, never()).save(any());
    }

    @Test @DisplayName("updateBook: книга не найдена → BookNotFoundException")
    void updateBook_notFound() {
        UUID uid = UUID.randomUUID();
        when(repo.findById(uid)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> svc.updateBook(uid, dto))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test @DisplayName("deleteBook: книга найдена → удаляется")
    void deleteBook_ok() {
        when(repo.findById(id)).thenReturn(Optional.of(book));
        svc.deleteBook(id);
        verify(repo).delete(id);
    }

    @Test @DisplayName("deleteBook: не найдена → BookNotFoundException")
    void deleteBook_notFound() {
        UUID uid = UUID.randomUUID();
        when(repo.findById(uid)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> svc.deleteBook(uid))
                .isInstanceOf(BookNotFoundException.class);
        verify(repo, never()).delete(any());
    }
}