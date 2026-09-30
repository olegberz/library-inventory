package lv.turiba.library.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/** A registered library reader who can borrow books. Table: members. */
@Entity
@Table(name = "members")
public class Member {

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

    @NotBlank(message = "E-mail is required")
    @Email(message = "E-mail format is not valid")
    @Size(max = 120, message = "E-mail must be at most 120 characters")
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Size(max = 20, message = "Phone must be at most 20 characters")
    @Column(length = 20)
    private String phone;

    /** Filled with today's date automatically if left empty. */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "registered_on", nullable = false)
    private LocalDate registeredOn;

    public Member() {
    }

    public Member(String firstName, String lastName, String email, String phone, LocalDate registeredOn) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.registeredOn = registeredOn;
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

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDate getRegisteredOn() { return registeredOn; }
    public void setRegisteredOn(LocalDate registeredOn) { this.registeredOn = registeredOn; }
}
