package lv.turiba.library.service;

import lv.turiba.library.model.Book;
import lv.turiba.library.repository.BookRepository;
import lv.turiba.library.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * CRUD for books.
 * Rules: ISBN is unique; quantity cannot drop below the number of copies on loan;
 * a book with loan history cannot be deleted.
 */
@Service
public class BookService {

    private final BookRepository books;
    private final LoanRepository loans;

    public BookService(BookRepository books, LoanRepository loans) {
        this.books = books;
        this.loans = loans;
    }

    /** All books, or only those whose title/author contains the search text. */
    public List<Book> findAll(String search) {
        if (search == null || search.isBlank()) {
            return books.findAllByOrderByTitleAsc();
        }
        return books.search(search.trim());
    }

    public Book findById(Long id) {
        return books.findById(id).orElseThrow(() -> new NotFoundException("Book", id));
    }

    @Transactional
    public Book create(Book book) {
        if (books.existsByIsbn(book.getIsbn())) {
            throw new BusinessRuleException("isbn", "A book with ISBN " + book.getIsbn() + " already exists");
        }
        book.setId(null);
        return books.save(book);
    }

    @Transactional
    public Book update(Long id, Book changes) {
        Book existing = findById(id);
        if (books.existsByIsbnAndIdNot(changes.getIsbn(), id)) {
            throw new BusinessRuleException("isbn", "A book with ISBN " + changes.getIsbn() + " already exists");
        }
        long onLoan = loans.countByBookIdAndReturnDateIsNull(id);
        if (changes.getQuantity() < onLoan) {
            throw new BusinessRuleException("quantity",
                    "Quantity cannot be less than " + onLoan + " (copies currently on loan)");
        }
        existing.setTitle(changes.getTitle());
        existing.setAuthor(changes.getAuthor());
        existing.setGenre(changes.getGenre());
        existing.setIsbn(changes.getIsbn());
        existing.setPublishedYear(changes.getPublishedYear());
        existing.setQuantity(changes.getQuantity());
        existing.setShelfLocation(changes.getShelfLocation());
        return books.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Book existing = findById(id);
        if (loans.existsByBookId(id)) {
            throw new BusinessRuleException("\"" + existing.getTitle()
                    + "\" has loan records and cannot be deleted");
        }
        books.delete(existing);
    }

    /** Total number of copies across the given books. */
    public int totalCopies(List<Book> list) {
        int total = 0;
        for (Book book : list) {
            total += book.getQuantity();
        }
        return total;
    }
}
