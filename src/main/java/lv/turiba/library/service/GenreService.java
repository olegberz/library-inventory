package lv.turiba.library.service;

import lv.turiba.library.model.Genre;
import lv.turiba.library.repository.BookRepository;
import lv.turiba.library.repository.GenreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** CRUD for genres. A genre that is used by a book cannot be deleted. */
@Service
public class GenreService {

    private final GenreRepository genres;
    private final BookRepository books;

    public GenreService(GenreRepository genres, BookRepository books) {
        this.genres = genres;
        this.books = books;
    }

    public List<Genre> findAll() {
        return genres.findAllByOrderByNameAsc();
    }

    public Genre findById(Long id) {
        return genres.findById(id).orElseThrow(() -> new NotFoundException("Genre", id));
    }

    @Transactional
    public Genre create(Genre genre) {
        if (genres.existsByNameIgnoreCase(genre.getName())) {
            throw new BusinessRuleException("name", "Genre \"" + genre.getName() + "\" already exists");
        }
        genre.setId(null);
        return genres.save(genre);
    }

    @Transactional
    public Genre update(Long id, Genre changes) {
        Genre existing = findById(id);
        if (genres.existsByNameIgnoreCaseAndIdNot(changes.getName(), id)) {
            throw new BusinessRuleException("name", "Genre \"" + changes.getName() + "\" already exists");
        }
        existing.setName(changes.getName());
        existing.setDescription(changes.getDescription());
        return genres.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Genre existing = findById(id);
        if (books.existsByGenreId(id)) {
            throw new BusinessRuleException("Genre \"" + existing.getName()
                    + "\" is used by at least one book and cannot be deleted");
        }
        genres.delete(existing);
    }
}
