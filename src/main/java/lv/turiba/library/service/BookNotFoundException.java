package lv.turiba.library.service;

/** Thrown when a book with the requested id does not exist. */
public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Long id) {
        super("Book with id " + id + " was not found");
    }
}
