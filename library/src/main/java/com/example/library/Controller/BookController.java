package com.example.library.Controller;

import com.example.library.DTO.BookDTO;
import com.example.library.Exception.ErrorResponse;
import com.example.library.Service.BookService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/books")
@Tag(name = "Books", description = "API для управления книгами библиотеки")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Получить все книги",
            description = "Возвращает все книги. Без фильтров — полный список.")
    @ApiResponse(responseCode = "200", description = "Список успешно получен")
    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        return ResponseEntity.ok(bookService.findAllBooks());
    }

    @Operation(summary = "Получить книгу по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Книга найдена"),
            @ApiResponse(responseCode = "404", description = "Книга не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(
            @Parameter(description = "UUID книги") @PathVariable UUID id) {
        return ResponseEntity.ok(bookService.findBookById(id));
    }

    @Operation(summary = "Создать новую книгу")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Книга создана"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "ISBN уже существует",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })

    @PostMapping
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(bookDTO));
    }

    @Operation(summary = "Обновить книгу по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Книга обновлена"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Книга не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "ISBN уже существует",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(
            @Parameter(description = "UUID книги") @PathVariable UUID id,
            @Valid @RequestBody BookDTO bookDTO) {
        return ResponseEntity.ok(bookService.updateBook(id, bookDTO));
    }

    @Operation(summary = "Удалить книгу по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Книга удалена"),
            @ApiResponse(responseCode = "404", description = "Книга не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "UUID книги") @PathVariable UUID id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Поиск книг",
            description = "Поиск по названию, автору и/или диапазону годов. Параметры комбинируются (AND).")
    @ApiResponse(responseCode = "200", description = "Результаты поиска")
    @GetMapping("/search")
    public ResponseEntity<List<BookDTO>> searchBooks(
            @Parameter(description = "Часть названия")  @RequestParam(required = false) String title,
            @Parameter(description = "Часть имени автора") @RequestParam(required = false) String author,
            @Parameter(description = "Год от")           @RequestParam(required = false) Integer yearFrom,
            @Parameter(description = "Год до")           @RequestParam(required = false) Integer yearTo) {
        return ResponseEntity.ok(bookService.searchBooks(title, author, yearFrom, yearTo));
    }
}