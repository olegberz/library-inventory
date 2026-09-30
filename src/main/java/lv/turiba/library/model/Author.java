package lv.turiba.library.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** A book author. Table: authors. */
@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(max = 60, message = "First name must be at most 60 characters")
    @Column(name = "first_name", nullable = false, length = 60)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 60, message = "Last name must be at most 60 characters")
    @Column(name = "last_name", nullable = false, length = 60)
    private String lastName;

    @Size(max = 60, message = "Country must be at most 60 characters")
    @Column(length = 60)
    private String country;

    @Min(value = 1000, message = "Birth year must be 1000 or later")
    @Max(value = 2100, message = "Birth year must be 2100 or earlier")
    @Column(name = "birth_year")
    private Integer birthYear;

    public Author() {
    }

    public Author(String firstName, String lastName, String country, Integer birthYear) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.birthYear = birthYear;
    }

    /** "First Last", used in tables and drop-down lists. */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Integer getBirthYear() { return birthYear; }
    public void setBirthYear(Integer birthYear) { this.birthYear = birthYear; }
}
