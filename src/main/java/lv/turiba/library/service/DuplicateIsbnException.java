package lv.turiba.library.service;

/** Thrown when another book already uses the same ISBN. */
public class DuplicateIsbnException extends RuntimeException {

    public DuplicateIsbnException(String isbn) {
        super("A book with ISBN " + isbn + " already exists");
    }
}
