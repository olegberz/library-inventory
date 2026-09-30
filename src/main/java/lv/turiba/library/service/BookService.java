package lv.turiba.library.service;

import lv.turiba.library.model.Book;
import lv.turiba.library.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for the four CRUD operations.
 * The controller never talks to the repository directly.
 */
@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    /** READ: all books, or only those whose title/author contains the search text. */
    public List<Book> findAll(String search) {
        if (search == null || search.isBlank()) {
            return repository.findAllByOrderByTitleAsc();
        }
        String text = search.trim();
        return repository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrderByTitleAsc(text, text);
    }

    /** READ: one book by id. */
    public Book findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    /** CREATE: add a new book. ISBN must be unique. */
    @Transactional
    public Book create(Book book) {
        if (repository.existsByIsbn(book.getIsbn())) {
            throw new DuplicateIsbnException(book.getIsbn());
        }
        book.setId(null);
        return repository.save(book);
    }

    /** UPDATE: overwrite the fields of an existing book. */
    @Transactional
    public Book update(Long id, Book changes) {
        Book existing = findById(id);
        if (repository.existsByIsbnAndIdNot(changes.getIsbn(), id)) {
            throw new DuplicateIsbnException(changes.getIsbn());
        }
        existing.setTitle(changes.getTitle());
        existing.setAuthor(changes.getAuthor());
        existing.setIsbn(changes.getIsbn());
        existing.setGenre(changes.getGenre());
        existing.setPublishedYear(changes.getPublishedYear());
        existing.setQuantity(changes.getQuantity());
        existing.setShelfLocation(changes.getShelfLocation());
        return repository.save(existing);
    }

    /** DELETE: remove a book from the inventory. */
    @Transactional
    public void delete(Long id) {
        Book existing = findById(id);
        repository.delete(existing);
    }

    /** Total number of copies across all titles (shown on the list page). */
    public int totalCopies(List<Book> books) {
        int total = 0;
        for (Book book : books) {
            total += book.getQuantity();
        }
        return total;
    }
}
