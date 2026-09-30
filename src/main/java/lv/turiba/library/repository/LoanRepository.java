package lv.turiba.library.repository;

import lv.turiba.library.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findAllByOrderByLoanDateDescIdDesc();

    /** Copies of one book that are currently on loan. */
    long countByBookIdAndReturnDateIsNull(Long bookId);

    /** Same, but ignoring one loan (used when that loan itself is being edited). */
    long countByBookIdAndReturnDateIsNullAndIdNot(Long bookId, Long loanId);

    /** Rows of [bookId, number of active loans] for every book that has active loans. */
    @Query("select l.book.id, count(l) from Loan l where l.returnDate is null group by l.book.id")
    List<Object[]> countActiveLoansPerBook();

    boolean existsByBookId(Long bookId);

    boolean existsByMemberId(Long memberId);
}
