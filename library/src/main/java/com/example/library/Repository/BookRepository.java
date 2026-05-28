package com.example.library.Repository;

import com.example.library.Model.Book;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

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
        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) book.setCreatedAt(ca.toLocalDateTime());
        Timestamp ua = rs.getTimestamp("updated_at");
        if (ua != null) book.setUpdatedAt(ua.toLocalDateTime());
        return book;
    };

    public List<Book> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM books ORDER BY created_at DESC", bookRowMapper);
    }


    public List<Book> findByFilters(
            String title, String author, Integer yearFrom, Integer yearTo) {
        StringBuilder sql = new StringBuilder("SELECT * FROM books WHERE 1=1");
        List<Object> p = new ArrayList<>();
        if (title != null && !title.isBlank()) {
            sql.append(" AND LOWER(title) LIKE LOWER(?)");
            p.add("%" + title.trim() + "%");
        }
        if (author != null && !author.isBlank()) {
            sql.append(" AND LOWER(author) LIKE LOWER(?)");
            p.add("%" + author.trim() + "%");
        }
        if (yearFrom != null) { sql.append(" AND year >= ?"); p.add(yearFrom); }
        if (yearTo   != null) { sql.append(" AND year <= ?"); p.add(yearTo);   }
        sql.append(" ORDER BY created_at DESC");
        return jdbcTemplate.query(sql.toString(), bookRowMapper, p.toArray());
    }


    public Optional<Book> findById(UUID id) {
        return jdbcTemplate.query(
                "SELECT * FROM books WHERE id = ?",
                bookRowMapper, id.toString()).stream().findFirst();
    }

    public boolean existsByIsbn(String isbn) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM books WHERE isbn = ?", Integer.class, isbn);
        return count != null && count > 0;
    }

    public void save(Book book) {
        if (book.getId() == null) book.setId(UUID.randomUUID());
        LocalDateTime now = LocalDateTime.now();
        book.setCreatedAt(now); book.setUpdatedAt(now);
        jdbcTemplate.update(
                "INSERT INTO books (id,title,author,year,isbn,created_at,updated_at) VALUES (?,?,?,?,?,?,?)",
                book.getId().toString(), book.getTitle(), book.getAuthor(),
                book.getYear(), book.getIsbn(), book.getCreatedAt(), book.getUpdatedAt());
    }

    public void update(Book book) {
        book.setUpdatedAt(LocalDateTime.now());
        jdbcTemplate.update(
                "UPDATE books SET title=?,author=?,year=?,isbn=?,updated_at=? WHERE id=?",
                book.getTitle(), book.getAuthor(), book.getYear(),
                book.getIsbn(), book.getUpdatedAt(), book.getId().toString());
    }

    public void delete(UUID id) {
        jdbcTemplate.update("DELETE FROM books WHERE id = ?", id.toString());
    }
}