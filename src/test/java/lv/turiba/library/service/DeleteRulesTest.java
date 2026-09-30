package lv.turiba.library.service;

import lv.turiba.library.model.Author;
import lv.turiba.library.model.Genre;
import lv.turiba.library.model.Member;
import lv.turiba.library.repository.AuthorRepository;
import lv.turiba.library.repository.BookRepository;
import lv.turiba.library.repository.GenreRepository;
import lv.turiba.library.repository.LoanRepository;
import lv.turiba.library.repository.MemberRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Records that are still referenced by other records must not be deleted; duplicates are rejected. */
class DeleteRulesTest {

    private final BookRepository books = mock(BookRepository.class);
    private final LoanRepository loans = mock(LoanRepository.class);

    @Test
    void genreUsedByBookCannotBeDeleted() {
        GenreRepository genres = mock(GenreRepository.class);
        when(genres.findById(1L)).thenReturn(Optional.of(new Genre("Fiction", null)));
        when(books.existsByGenreId(1L)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> new GenreService(genres, books).delete(1L));
        verify(genres, never()).delete(any());
    }

    @Test
    void duplicateGenreNameIsRejected() {
        GenreRepository genres = mock(GenreRepository.class);
        when(genres.existsByNameIgnoreCase("fiction")).thenReturn(true);

        BusinessRuleException e = assertThrows(BusinessRuleException.class,
                () -> new GenreService(genres, books).create(new Genre("fiction", null)));
        assertEquals("name", e.getField());
    }

    @Test
    void authorWithBooksCannotBeDeleted() {
        AuthorRepository authors = mock(AuthorRepository.class);
        when(authors.findById(1L)).thenReturn(Optional.of(new Author("George", "Orwell", null, null)));
        when(books.existsByAuthorId(1L)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> new AuthorService(authors, books).delete(1L));
        verify(authors, never()).delete(any());
    }

    @Test
    void memberWithLoansCannotBeDeleted() {
        MemberRepository members = mock(MemberRepository.class);
        when(members.findById(1L)).thenReturn(Optional.of(new Member("Anna", "Ozola", "a@example.com", null, LocalDate.now())));
        when(loans.existsByMemberId(1L)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> new MemberService(members, loans).delete(1L));
        verify(members, never()).delete(any());
    }

    @Test
    void newMemberGetsTodayAsRegistrationDate() {
        MemberRepository members = mock(MemberRepository.class);
        Member member = new Member("Janis", "Kalnins", "j@example.com", null, null);
        when(members.existsByEmailIgnoreCase("j@example.com")).thenReturn(false);
        when(members.save(member)).thenReturn(member);

        Member saved = new MemberService(members, loans).create(member);

        assertEquals(LocalDate.now(), saved.getRegisteredOn());
    }
}
