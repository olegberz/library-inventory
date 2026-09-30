package lv.turiba.library.controller;

import jakarta.validation.Valid;
import lv.turiba.library.model.Book;
import lv.turiba.library.service.AuthorService;
import lv.turiba.library.service.BookService;
import lv.turiba.library.service.BusinessRuleException;
import lv.turiba.library.service.GenreService;
import lv.turiba.library.service.LoanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Books.
 *   GET  /books              -> list + search        (READ)
 *   GET  /books/new          -> empty form
 *   POST /books              -> save new book         (CREATE)
 *   GET  /books/{id}/edit    -> filled form
 *   POST /books/{id}         -> save changes          (UPDATE)
 *   POST /books/{id}/delete  -> remove book           (DELETE)
 * The other entities use the same URL pattern.
 *
 * The author and genre drop-downs send an id (e.g. author=3); Spring Data converts
 * that id into the Author / Genre object automatically.
 */
@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService books;
    private final AuthorService authors;
    private final GenreService genres;
    private final LoanService loans;

    public BookController(BookService books, AuthorService authors, GenreService genres, LoanService loans) {
        this.books = books;
        this.authors = authors;
        this.genres = genres;
        this.loans = loans;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        List<Book> list = books.findAll(search);
        model.addAttribute("books", list);
        model.addAttribute("available", loans.availableCopies(list));
        model.addAttribute("search", search);
        model.addAttribute("totalCopies", books.totalCopies(list));
        return "books/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("book", new Book());
        return form(model, "Add book", "/books");
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("book") Book book, BindingResult result,
                         Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                books.create(book);
                redirect.addFlashAttribute("message", "Book \"" + book.getTitle() + "\" added.");
                return "redirect:/books";
            } catch (BusinessRuleException e) {
                FormErrors.add(result, e);
            }
        }
        return form(model, "Add book", "/books");
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("book", books.findById(id));
        return form(model, "Edit book", "/books/" + id);
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("book") Book book,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                books.update(id, book);
                redirect.addFlashAttribute("message", "Book \"" + book.getTitle() + "\" updated.");
                return "redirect:/books";
            } catch (BusinessRuleException e) {
                FormErrors.add(result, e);
            }
        }
        return form(model, "Edit book", "/books/" + id);
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            books.delete(id);
            redirect.addFlashAttribute("message", "Book deleted.");
        } catch (BusinessRuleException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/books";
    }

    private String form(Model model, String title, String action) {
        model.addAttribute("formTitle", title);
        model.addAttribute("formAction", action);
        model.addAttribute("authors", authors.findAll());
        model.addAttribute("genres", genres.findAll());
        return "books/form";
    }
}
