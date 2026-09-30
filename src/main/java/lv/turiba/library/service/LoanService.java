package lv.turiba.library.service;

import lv.turiba.library.model.Book;
import lv.turiba.library.model.Loan;
import lv.turiba.library.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CRUD for loans (lending a book copy to a member).
 * Rules: the due date and the return date cannot be before the loan date,
 * and an active loan needs a free copy of the book.
 */
@Service
public class LoanService {

    private final LoanRepository loans;

    public LoanService(LoanRepository loans) {
        this.loans = loans;
    }

    public List<Loan> findAll() {
        return loans.findAllByOrderByLoanDateDescIdDesc();
    }

    public Loan findById(Long id) {
        return loans.findById(id).orElseThrow(() -> new NotFoundException("Loan", id));
    }

    @Transactional
    public Loan create(Loan loan) {
        checkDates(loan);
        if (loan.isActive()) {
            long onLoan = loans.countByBookIdAndReturnDateIsNull(loan.getBook().getId());
            checkFreeCopy(loan.getBook(), onLoan);
        }
        loan.setId(null);
        return loans.save(loan);
    }

    @Transactional
    public Loan update(Long id, Loan changes) {
        Loan existing = findById(id);
        checkDates(changes);
        if (changes.isActive()) {
            // do not count this loan itself, it is the one being edited
            long onLoan = loans.countByBookIdAndReturnDateIsNullAndIdNot(changes.getBook().getId(), id);
            checkFreeCopy(changes.getBook(), onLoan);
        }
        existing.setBook(changes.getBook());
        existing.setMember(changes.getMember());
        existing.setLoanDate(changes.getLoanDate());
        existing.setDueDate(changes.getDueDate());
        existing.setReturnDate(changes.getReturnDate());
        return loans.save(existing);
    }

    /** Marks the loan as returned today. */
    @Transactional
    public Loan returnBook(Long id) {
        Loan existing = findById(id);
        if (!existing.isActive()) {
            throw new BusinessRuleException("This loan is already returned");
        }
        existing.setReturnDate(LocalDate.now());
        return loans.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        loans.delete(findById(id));
    }

    /** Free copies per book id: quantity minus copies on loan. */
    public Map<Long, Integer> availableCopies(List<Book> books) {
        Map<Long, Long> onLoan = new HashMap<>();
        for (Object[] row : loans.countActiveLoansPerBook()) {
            onLoan.put((Long) row[0], (Long) row[1]);
        }
        Map<Long, Integer> available = new HashMap<>();
        for (Book book : books) {
            long borrowed = onLoan.getOrDefault(book.getId(), 0L);
            available.put(book.getId(), book.getQuantity() - (int) borrowed);
        }
        return available;
    }

    private void checkDates(Loan loan) {
        if (loan.getDueDate().isBefore(loan.getLoanDate())) {
            throw new BusinessRuleException("dueDate", "Due date cannot be before the loan date");
        }
        if (loan.getReturnDate() != null && loan.getReturnDate().isBefore(loan.getLoanDate())) {
            throw new BusinessRuleException("returnDate", "Return date cannot be before the loan date");
        }
    }

    private void checkFreeCopy(Book book, long onLoan) {
        if (onLoan >= book.getQuantity()) {
            throw new BusinessRuleException("book",
                    "No free copies of \"" + book.getTitle() + "\" (all " + book.getQuantity() + " are on loan)");
        }
    }
}
