package lv.turiba.library.service;

import lv.turiba.library.model.Author;
import lv.turiba.library.model.Book;
import lv.turiba.library.model.Genre;
import lv.turiba.library.repository.BookRepository;
import lv.turiba.library.repository.LoanRepository;
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

/** Unit tests for BookService. Repositories are mocked, no database is needed. */
class BookServiceTest {

    private BookRepository books;
    private LoanRepository loans;
    private BookService service;

    @BeforeEach
    void setUp() {
        books = mock(BookRepository.class);
        loans = mock(LoanRepository.class);
        service = new BookService(books, loans);
    }

    static Book sampleBook() {
        Author author = new Author("Robert C.", "Martin", "USA", 1952);
        Genre genre = new Genre("Programming", null);
        return new Book("Clean Code", author, genre, "9780132350884", 2008, 2, "A-03");
    }

    @Test
    void createSavesNewBook() {
        Book book = sampleBook();
        when(books.existsByIsbn(book.getIsbn())).thenReturn(false);
        when(books.save(book)).thenReturn(book);

        assertEquals("Clean Code", service.create(book).getTitle());
        verify(books).save(book);
    }

    @Test
    void createRejectsDuplicateIsbn() {
        Book book = sampleBook();
        when(books.existsByIsbn(book.getIsbn())).thenReturn(true);

        BusinessRuleException e = assertThrows(BusinessRuleException.class, () -> service.create(book));
        assertEquals("isbn", e.getField());
        verify(books, never()).save(any());
    }

    @Test
    void findAllWithoutSearchReturnsEverything() {
        when(books.findAllByOrderByTitleAsc()).thenReturn(List.of(sampleBook()));

        assertEquals(1, service.findAll("  ").size());
    }

    @Test
    void findAllWithSearchTrimsText() {
        when(books.search("martin")).thenReturn(List.of(sampleBook()));

        assertEquals(1, service.findAll(" martin ").size());
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(books.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void updateChangesFields() {
        Book existing = sampleBook();
        existing.setId(1L);
        Book changes = sampleBook();
        changes.setQuantity(7);
        changes.setShelfLocation("B-02");
        when(books.findById(1L)).thenReturn(Optional.of(existing));
        when(loans.countByBookIdAndReturnDateIsNull(1L)).thenReturn(0L);
        when(books.save(existing)).thenReturn(existing);

        Book updated = service.update(1L, changes);

        assertEquals(7, updated.getQuantity());
        assertEquals("B-02", updated.getShelfLocation());
    }

    @Test
    void updateRejectsQuantityBelowCopiesOnLoan() {
        Book existing = sampleBook();
        existing.setId(1L);
        Book changes = sampleBook();
        changes.setQuantity(1);
        when(books.findById(1L)).thenReturn(Optional.of(existing));
        when(loans.countByBookIdAndReturnDateIsNull(1L)).thenReturn(2L);

        BusinessRuleException e = assertThrows(BusinessRuleException.class, () -> service.update(1L, changes));
        assertEquals("quantity", e.getField());
    }

    @Test
    void deleteRemovesBookWithoutLoans() {
        Book existing = sampleBook();
        existing.setId(1L);
        when(books.findById(1L)).thenReturn(Optional.of(existing));
        when(loans.existsByBookId(1L)).thenReturn(false);

        service.delete(1L);

        verify(books).delete(existing);
    }

    @Test
    void deleteRejectsBookWithLoans() {
        Book existing = sampleBook();
        existing.setId(1L);
        when(books.findById(1L)).thenReturn(Optional.of(existing));
        when(loans.existsByBookId(1L)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> service.delete(1L));
        verify(books, never()).delete(any());
    }

    @Test
    void totalCopiesSumsQuantities() {
        Book a = sampleBook();
        Book b = sampleBook();
        b.setQuantity(5);

        assertEquals(7, service.totalCopies(List.of(a, b)));
    }
}
