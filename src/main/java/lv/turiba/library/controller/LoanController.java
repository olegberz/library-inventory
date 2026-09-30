package lv.turiba.library.controller;

import jakarta.validation.Valid;
import lv.turiba.library.model.Book;
import lv.turiba.library.model.Loan;
import lv.turiba.library.service.BookService;
import lv.turiba.library.service.BusinessRuleException;
import lv.turiba.library.service.LoanService;
import lv.turiba.library.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

/** Loans: list, issue a book, edit, mark as returned, delete. */
@Controller
@RequestMapping("/loans")
public class LoanController {

    /** Default loan period suggested in the "Issue book" form. */
    private static final int LOAN_DAYS = 14;

    private final LoanService loans;
    private final BookService books;
    private final MemberService members;

    public LoanController(LoanService loans, BookService books, MemberService members) {
        this.loans = loans;
        this.books = books;
        this.members = members;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("loans", loans.findAll());
        return "loans/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        Loan loan = new Loan();
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(LOAN_DAYS));
        model.addAttribute("loan", loan);
        return form(model, "Issue book", "/loans", false);
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("loan") Loan loan, BindingResult result,
                         Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                loans.create(loan);
                redirect.addFlashAttribute("message", "\"" + loan.getBook().getTitle()
                        + "\" issued to " + loan.getMember().getFullName() + ".");
                return "redirect:/loans";
            } catch (BusinessRuleException e) {
                FormErrors.add(result, e);
            }
        }
        return form(model, "Issue book", "/loans", false);
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("loan", loans.findById(id));
        return form(model, "Edit loan", "/loans/" + id, true);
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("loan") Loan loan,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                loans.update(id, loan);
                redirect.addFlashAttribute("message", "Loan updated.");
                return "redirect:/loans";
            } catch (BusinessRuleException e) {
                FormErrors.add(result, e);
            }
        }
        return form(model, "Edit loan", "/loans/" + id, true);
    }

    @PostMapping("/{id}/return")
    public String returnBook(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            Loan loan = loans.returnBook(id);
            redirect.addFlashAttribute("message", "\"" + loan.getBook().getTitle() + "\" returned.");
        } catch (BusinessRuleException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/loans";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        loans.delete(id);
        redirect.addFlashAttribute("message", "Loan deleted.");
        return "redirect:/loans";
    }

    private String form(Model model, String title, String action, boolean editing) {
        List<Book> allBooks = books.findAll(null);
        model.addAttribute("formTitle", title);
        model.addAttribute("formAction", action);
        model.addAttribute("editing", editing);
        model.addAttribute("books", allBooks);
        model.addAttribute("available", loans.availableCopies(allBooks));
        model.addAttribute("members", members.findAll());
        return "loans/form";
    }
}
