package lv.turiba.library.service;

import lv.turiba.library.model.Book;
import lv.turiba.library.model.Loan;
import lv.turiba.library.model.Member;
import lv.turiba.library.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Unit tests for the loan rules: free copies, dates, returning, status. */
class LoanServiceTest {

    private static final LocalDate TODAY = LocalDate.now();

    private LoanRepository loans;
    private LoanService service;
    private Book book;
    private Member member;

    @BeforeEach
    void setUp() {
        loans = mock(LoanRepository.class);
        service = new LoanService(loans);
        book = BookServiceTest.sampleBook();   // quantity = 2
        book.setId(1L);
        member = new Member("Anna", "Ozola", "anna@example.com", null, TODAY);
        member.setId(1L);
    }

    private Loan newLoan() {
        return new Loan(book, member, TODAY, TODAY.plusDays(14), null);
    }

    @Test
    void createSavesLoanWhenCopyIsFree() {
        Loan loan = newLoan();
        when(loans.countByBookIdAndReturnDateIsNull(1L)).thenReturn(1L);
        when(loans.save(loan)).thenReturn(loan);

        service.create(loan);

        verify(loans).save(loan);
    }

    @Test
    void createRejectsWhenAllCopiesAreOnLoan() {
        when(loans.countByBookIdAndReturnDateIsNull(1L)).thenReturn(2L);

        BusinessRuleException e = assertThrows(BusinessRuleException.class, () -> service.create(newLoan()));
        assertEquals("book", e.getField());
        verify(loans, never()).save(any());
    }

    @Test
    void createRejectsDueDateBeforeLoanDate() {
        Loan loan = newLoan();
        loan.setDueDate(TODAY.minusDays(1));

        BusinessRuleException e = assertThrows(BusinessRuleException.class, () -> service.create(loan));
        assertEquals("dueDate", e.getField());
    }

    @Test
    void updateDoesNotCountTheEditedLoanItself() {
        Loan existing = newLoan();
        existing.setId(5L);
        Loan changes = newLoan();
        changes.setDueDate(TODAY.plusDays(30));
        when(loans.findById(5L)).thenReturn(Optional.of(existing));
        when(loans.countByBookIdAndReturnDateIsNullAndIdNot(1L, 5L)).thenReturn(1L);
        when(loans.save(existing)).thenReturn(existing);

        Loan updated = service.update(5L, changes);

        assertEquals(TODAY.plusDays(30), updated.getDueDate());
    }

    @Test
    void returnBookSetsTodayAsReturnDate() {
        Loan existing = newLoan();
        existing.setId(5L);
        when(loans.findById(5L)).thenReturn(Optional.of(existing));
        when(loans.save(existing)).thenReturn(existing);

        Loan returned = service.returnBook(5L);

        assertNotNull(returned.getReturnDate());
        assertEquals("Returned", returned.getStatus());
    }

    @Test
    void returnBookRejectsAlreadyReturnedLoan() {
        Loan existing = newLoan();
        existing.setId(5L);
        existing.setReturnDate(TODAY);
        when(loans.findById(5L)).thenReturn(Optional.of(existing));

        assertThrows(BusinessRuleException.class, () -> service.returnBook(5L));
    }

    @Test
    void statusIsOverdueAfterDueDate() {
        Loan loan = new Loan(book, member, TODAY.minusDays(20), TODAY.minusDays(6), null);

        assertEquals("Overdue", loan.getStatus());
    }

    @Test
    void availableCopiesSubtractsActiveLoans() {
        List<Object[]> rows = List.<Object[]>of(new Object[]{1L, 1L});
        when(loans.countActiveLoansPerBook()).thenReturn(rows);

        Map<Long, Integer> available = service.availableCopies(List.of(book));

        assertEquals(1, available.get(1L));
    }
}
