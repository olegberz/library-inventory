package lv.turiba.library.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * One book copy lent to one member. Table: loans.
 * While returnDate is empty the copy counts as "on loan".
 */
@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Book is required")
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @NotNull(message = "Member is required")
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @NotNull(message = "Loan date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "loan_date", nullable = false)
    private LocalDate loanDate;

    @NotNull(message = "Due date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "return_date")
    private LocalDate returnDate;

    public Loan() {
    }

    public Loan(Book book, Member member, LocalDate loanDate, LocalDate dueDate, LocalDate returnDate) {
        this.book = book;
        this.member = member;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    /** True while the book has not been returned. */
    public boolean isActive() {
        return returnDate == null;
    }

    /** "Returned", "Overdue" or "On loan" – shown in the loans table. */
    public String getStatus() {
        if (returnDate != null) {
            return "Returned";
        }
        if (dueDate != null && dueDate.isBefore(LocalDate.now())) {
            return "Overdue";
        }
        return "On loan";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public LocalDate getLoanDate() { return loanDate; }
    public void setLoanDate(LocalDate loanDate) { this.loanDate = loanDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
}
