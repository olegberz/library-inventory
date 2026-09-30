package lv.turiba.library.service;

import lv.turiba.library.model.Author;
import lv.turiba.library.repository.AuthorRepository;
import lv.turiba.library.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** CRUD for authors. An author who has books in the library cannot be deleted. */
@Service
public class AuthorService {

    private final AuthorRepository authors;
    private final BookRepository books;

    public AuthorService(AuthorRepository authors, BookRepository books) {
        this.authors = authors;
        this.books = books;
    }

    public List<Author> findAll() {
        return authors.findAllByOrderByLastNameAscFirstNameAsc();
    }

    public Author findById(Long id) {
        return authors.findById(id).orElseThrow(() -> new NotFoundException("Author", id));
    }

    @Transactional
    public Author create(Author author) {
        author.setId(null);
        return authors.save(author);
    }

    @Transactional
    public Author update(Long id, Author changes) {
        Author existing = findById(id);
        existing.setFirstName(changes.getFirstName());
        existing.setLastName(changes.getLastName());
        existing.setCountry(changes.getCountry());
        existing.setBirthYear(changes.getBirthYear());
        return authors.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Author existing = findById(id);
        if (books.existsByAuthorId(id)) {
            throw new BusinessRuleException(existing.getFullName()
                    + " has books in the library and cannot be deleted");
        }
        authors.delete(existing);
    }
}
