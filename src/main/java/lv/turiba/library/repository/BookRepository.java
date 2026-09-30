package lv.turiba.library.repository;

import lv.turiba.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Data access layer. Spring Data JPA generates the SQL for these methods
 * (INSERT, SELECT, UPDATE, DELETE) at runtime.
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findAllByOrderByTitleAsc();

    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrderByTitleAsc(
            String title, String author);

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);
}
