package lv.turiba.library.repository;

import lv.turiba.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Data access for books. Spring Data JPA generates the SQL for the
 * derived methods; the search method uses a hand-written JPQL query.
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findAllByOrderByTitleAsc();

    /** Books whose title or author's first/last name contains the text (case-insensitive). */
    @Query("""
            select b from Book b
            where lower(b.title) like lower(concat('%', :text, '%'))
               or lower(b.author.firstName) like lower(concat('%', :text, '%'))
               or lower(b.author.lastName) like lower(concat('%', :text, '%'))
            order by b.title
            """)
    List<Book> search(@Param("text") String text);

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);

    boolean existsByAuthorId(Long authorId);

    boolean existsByGenreId(Long genreId);
}
