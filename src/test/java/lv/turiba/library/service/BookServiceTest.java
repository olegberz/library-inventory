package lv.turiba.library.service;

import lv.turiba.library.model.Book;
import lv.turiba.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the CRUD business rules.
 * The repository is mocked, so no database is needed to run them.
 */
class BookServiceTest {

    private BookRepository repository;
    private BookService service;

    @BeforeEach
    void setUp() {
        repository = mock(BookRepository.class);
        service = new BookService(repository);
    }

    private Book sampleBook() {
        return new Book("Clean Code", "Robert C. Martin", "9780132350884",
                "Programming", 2008, 2, "A-03");
    }

    @Test
    void createSavesNewBook() {
        Book book = sampleBook();
        when(repository.existsByIsbn(book.getIsbn())).thenReturn(false);
        when(repository.save(book)).thenReturn(book);

        Book saved = service.create(book);

        assertEquals("Clean Code", saved.getTitle());
        verify(repository).save(book);
    }

    @Test
    void createRejectsDuplicateIsbn() {
        Book book = sampleBook();
        when(repository.existsByIsbn(book.getIsbn())).thenReturn(true);

        assertThrows(DuplicateIsbnException.class, () -> service.create(book));
        verify(repository, never()).save(any());
    }

    @Test
    void findAllWithoutSearchReturnsEverything() {
        when(repository.findAllByOrderByTitleAsc()).thenReturn(List.of(sampleBook()));

        assertEquals(1, service.findAll("  ").size());
    }

    @Test
    void findAllWithSearchUsesFilter() {
        when(repository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrderByTitleAsc("martin", "martin"))
                .thenReturn(List.of(sampleBook()));

        assertEquals(1, service.findAll(" martin ").size());
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void updateChangesFields() {
        Book existing = sampleBook();
        existing.setId(1L);
        Book changes = sampleBook();
        changes.setQuantity(7);
        changes.setShelfLocation("B-02");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existsByIsbnAndIdNot(changes.getIsbn(), 1L)).thenReturn(false);
        when(repository.save(existing)).thenReturn(existing);

        Book updated = service.update(1L, changes);

        assertEquals(7, updated.getQuantity());
        assertEquals("B-02", updated.getShelfLocation());
    }

    @Test
    void deleteRemovesExistingBook() {
        Book existing = sampleBook();
        existing.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        service.delete(1L);

        verify(repository).delete(existing);
    }

    @Test
    void totalCopiesSumsQuantities() {
        Book a = sampleBook();
        Book b = sampleBook();
        b.setQuantity(5);

        assertEquals(7, service.totalCopies(List.of(a, b)));
    }
}
