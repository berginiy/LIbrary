package com.example.library.Repository;

import com.example.library.Model.Book;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BookRepository {

    private final JdbcTemplate jdbcTemplate;

    public BookRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Book> bookRowMapper = (rs, rowNum) -> {
        Book book = new Book();
        book.setId(UUID.fromString(rs.getString("id")));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setYear(rs.getInt("year"));
        book.setIsbn(rs.getString("isbn"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) book.setCreatedAt(createdAt.toLocalDateTime());

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) book.setUpdatedAt(updatedAt.toLocalDateTime());

        return book;
    };

    public List<Book> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM books ORDER BY created_at DESC",
                bookRowMapper
        );
    }

    public Optional<Book> findById(UUID id) {
        return jdbcTemplate.query(
                "SELECT * FROM books WHERE id = ?",
                bookRowMapper,
                id.toString()
        ).stream().findFirst();
    }

    public boolean existsByIsbn(String isbn) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM books WHERE isbn = ?",
                Integer.class,
                isbn
        );
        return count != null && count > 0;
    }

    public void save(Book book) {
        if (book.getId() == null) {
            book.setId(UUID.randomUUID());
        }
        LocalDateTime now = LocalDateTime.now();
        book.setCreatedAt(now);
        book.setUpdatedAt(now);

        jdbcTemplate.update(
                "INSERT INTO books (id, title, author, year, isbn, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
                book.getId().toString(),
                book.getTitle(),
                book.getAuthor(),
                book.getYear(),
                book.getIsbn(),
                book.getCreatedAt(),
                book.getUpdatedAt()
        );
    }

    public void update(Book book) {
        book.setUpdatedAt(LocalDateTime.now());

        jdbcTemplate.update(
                "UPDATE books SET title = ?, author = ?, year = ?, isbn = ?, updated_at = ? WHERE id = ?",
                book.getTitle(),
                book.getAuthor(),
                book.getYear(),
                book.getIsbn(),
                book.getUpdatedAt(),
                book.getId().toString()
        );
    }

    public void delete(UUID id) {
        jdbcTemplate.update("DELETE FROM books WHERE id = ?", id.toString());
    }

    public List<Book> searchByTitle(String title) {
        return jdbcTemplate.query(
                "SELECT * FROM books WHERE LOWER(title) LIKE LOWER(?) ORDER BY created_at DESC",
                bookRowMapper,
                "%" + title + "%"
        );
    }

    public List<Book> searchByAuthor(String author) {
        return jdbcTemplate.query(
                "SELECT * FROM books WHERE LOWER(author) LIKE LOWER(?) ORDER BY created_at DESC",
                bookRowMapper,
                "%" + author + "%"
        );
    }

    public List<Book> searchByYearRange(int from, int to) {
        return jdbcTemplate.query(
                "SELECT * FROM books WHERE year BETWEEN ? AND ? ORDER BY year DESC",
                bookRowMapper,
                from, to
        );
    }
}