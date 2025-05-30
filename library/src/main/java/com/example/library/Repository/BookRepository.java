package com.example.library.Repository;

import com.example.library.Model.Book;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BookRepository {
    private final JdbcTemplate jdbcTemplate;

    public BookRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Book> bookRowMapper = (rs, rowNum) -> {
        Book book = new Book();
        book.setId(UUID.fromString(rs.getString("id")));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setYear(rs.getInt("year"));
        book.setIsbn(rs.getString("isbn"));
        return book;
    };

    public List<Book> findAll() {
        return jdbcTemplate.query("SELECT * FROM books", bookRowMapper);
    }

    public Optional<Book> findById(UUID id) {
        return jdbcTemplate.query("SELECT * FROM books WHERE id = ?", bookRowMapper, id)
                .stream()
                .findFirst();
    }

    public void save(Book book) {
        jdbcTemplate.update("INSERT INTO books (id, title, author, year, isbn) VALUES (?, ?, ?, ?, ?)",
                book.getId(), book.getTitle(), book.getAuthor(), book.getYear(), book.getIsbn());
    }

    public void update(Book book) {
        jdbcTemplate.update("UPDATE books SET title = ?, author = ?, year = ?, isbn = ? WHERE id = ?",
                book.getTitle(), book.getAuthor(), book.getYear(), book.getIsbn(), book.getId());
    }

    public void delete(UUID id) {
        jdbcTemplate.update("DELETE FROM books WHERE id = ?", id);
    }
}
