package lv.turiba.library.controller;

import jakarta.validation.Valid;
import lv.turiba.library.model.Book;
import lv.turiba.library.service.BookNotFoundException;
import lv.turiba.library.service.BookService;
import lv.turiba.library.service.DuplicateIsbnException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Web layer. Maps browser requests to service calls and returns HTML pages.
 *
 *   GET  /books              -> list + search        (READ)
 *   GET  /books/new          -> empty form
 *   POST /books              -> save new book         (CREATE)
 *   GET  /books/{id}/edit    -> filled form
 *   POST /books/{id}         -> save changes          (UPDATE)
 *   POST /books/{id}/delete  -> remove book           (DELETE)
 */
@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        List<Book> books = service.findAll(search);
        model.addAttribute("books", books);
        model.addAttribute("search", search);
        model.addAttribute("totalCopies", service.totalCopies(books));
        return "books/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("formTitle", "Add book");
        model.addAttribute("formAction", "/books");
        return "books/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("book") Book book, BindingResult result,
                         Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                service.create(book);
                redirect.addFlashAttribute("message", "Book \"" + book.getTitle() + "\" added.");
                return "redirect:/books";
            } catch (DuplicateIsbnException e) {
                result.rejectValue("isbn", "duplicate", e.getMessage());
            }
        }
        model.addAttribute("formTitle", "Add book");
        model.addAttribute("formAction", "/books");
        return "books/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("book", service.findById(id));
        model.addAttribute("formTitle", "Edit book");
        model.addAttribute("formAction", "/books/" + id);
        return "books/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("book") Book book,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                service.update(id, book);
                redirect.addFlashAttribute("message", "Book \"" + book.getTitle() + "\" updated.");
                return "redirect:/books";
            } catch (DuplicateIsbnException e) {
                result.rejectValue("isbn", "duplicate", e.getMessage());
            }
        }
        book.setId(id);
        model.addAttribute("formTitle", "Edit book");
        model.addAttribute("formAction", "/books/" + id);
        return "books/form";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        service.delete(id);
        redirect.addFlashAttribute("message", "Book deleted.");
        return "redirect:/books";
    }

    @ExceptionHandler(BookNotFoundException.class)
    public String notFound(BookNotFoundException e, RedirectAttributes redirect) {
        redirect.addFlashAttribute("error", e.getMessage());
        return "redirect:/books";
    }
}
