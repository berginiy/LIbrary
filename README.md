# Library Book Management System

REST API для управления книгами библиотеки на основе Spring Boot.

## Технологии
- Java 17
- Spring Boot 3.2
- PostgreSQL
- JdbcTemplate
- Swagger / OpenAPI 3
- Maven

## Запуск

### 1. Создать базу данных
```sql
CREATE DATABASE library_db;
```

### 2. Настроить `application.properties`
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/library_db
spring.datasource.username=postgres
spring.datasource.password=your_password
```

### 3. Запустить
```bash
./mvnw spring-boot:run
```

## API Endpoints

| Метод  | URL                  | Описание              |
|--------|----------------------|-----------------------|
| GET    | /books               | Все книги             |
| GET    | /books/{id}          | Книга по ID           |
| GET    | /books/search        | Поиск (title/author/year) |
| POST   | /books               | Создать книгу         |
| PUT    | /books/{id}          | Обновить книгу        |
| DELETE | /books/{id}          | Удалить книгу         |

## Swagger UI
После запуска: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
