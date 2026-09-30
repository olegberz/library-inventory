package lv.turiba.library.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * One book title in the library inventory. Table: books.
 * "quantity" is the number of physical copies the library owns;
 * copies that are on loan are counted from the loans table.
 */
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be at most 200 characters")
    @Column(nullable = false, length = 200)
    private String title;

    @NotNull(message = "Author is required")
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @NotNull(message = "Genre is required")
    @ManyToOne
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "\\d{10}|\\d{13}", message = "ISBN must contain 10 or 13 digits (no dashes)")
    @Column(nullable = false, unique = true, length = 13)
    private String isbn;

    @NotNull(message = "Publication year is required")
    @Min(value = 1450, message = "Year must be 1450 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    @Column(name = "published_year", nullable = false)
    private Integer publishedYear;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    @Column(nullable = false)
    private Integer quantity;

    @NotBlank(message = "Shelf location is required")
    @Size(max = 20, message = "Shelf location must be at most 20 characters")
    @Column(name = "shelf_location", nullable = false, length = 20)
    private String shelfLocation;

    public Book() {
    }

    public Book(String title, Author author, Genre genre, String isbn,
                Integer publishedYear, Integer quantity, String shelfLocation) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.isbn = isbn;
        this.publishedYear = publishedYear;
        this.quantity = quantity;
        this.shelfLocation = shelfLocation;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }

    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public Integer getPublishedYear() { return publishedYear; }
    public void setPublishedYear(Integer publishedYear) { this.publishedYear = publishedYear; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getShelfLocation() { return shelfLocation; }
    public void setShelfLocation(String shelfLocation) { this.shelfLocation = shelfLocation; }
}
