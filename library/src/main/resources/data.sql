CREATE TABLE IF NOT EXISTS books (
                                     id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    year INTEGER NOT NULL,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
    );

INSERT INTO books (id, title, author, year, isbn, created_at, updated_at)
VALUES
    ('a1b2c3d4-0000-0000-0000-000000000001', 'Мастер и Маргарита', 'Михаил Булгаков', 1967, '978-5-17-090944-4', NOW(), NOW()),
    ('a1b2c3d4-0000-0000-0000-000000000002', '1984', 'Джордж Оруэлл', 1949, '978-5-17-090673-3', NOW(), NOW()),
    ('a1b2c3d4-0000-0000-0000-000000000003', 'Преступление и наказание', 'Фёдор Достоевский', 1866, '978-5-04-116640-7', NOW(), NOW())
    ON CONFLICT (isbn) DO NOTHING;