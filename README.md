# Library Inventory System "ABC"

Course paper 3 (DAT1080P), Turiba University. Topic #4: library inventory system of company "ABC".

Web app for a library: books, authors, genres, members and loans (who borrowed which book).
Java 21, Spring Boot, Thymeleaf, MySQL 8.4, Docker Compose.

## Run with Docker (only Docker Desktop needed)

```
docker compose --profile full up -d --build
```

Open http://localhost:8080. First start takes a couple of minutes.

Stop: `docker compose --profile full down`
Reset the database to sample data: `docker compose --profile full down -v`

## Run from IntelliJ IDEA

1. Open the project folder, set JDK 21 (File → Project Structure).
2. Start only the database: `docker compose up -d db`
3. Run `LibraryApplication`.
4. Open http://localhost:8080

MySQL is on port 3307 (not 3306) so it doesn't clash with a local MySQL.
User `library_user`, password `library_pass`, database `library_db`.

## Tests

Right-click `src/test/java` → Run 'All Tests', or `mvn test`. No database needed.

## Structure

```
src/main/java/lv/turiba/library/
  model/        entities (Genre, Author, Book, Member, Loan)
  repository/   database access
  service/      business rules
  controller/   web pages
src/main/resources/templates/   HTML pages
docker/mysql/init.sql           tables + sample data
docs/diagrams/                  diagram sources (Mermaid)
```
