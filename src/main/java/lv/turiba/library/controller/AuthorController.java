package lv.turiba.library.controller;

import jakarta.validation.Valid;
import lv.turiba.library.model.Author;
import lv.turiba.library.service.AuthorService;
import lv.turiba.library.service.BusinessRuleException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Authors: list, add, edit, delete. */
@Controller
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService service;

    public AuthorController(AuthorService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("authors", service.findAll());
        return "authors/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("author", new Author());
        return form(model, "Add author", "/authors");
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("author") Author author, BindingResult result,
                         Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                service.create(author);
                redirect.addFlashAttribute("message", "Author " + author.getFullName() + " added.");
                return "redirect:/authors";
            } catch (BusinessRuleException e) {
                FormErrors.add(result, e);
            }
        }
        return form(model, "Add author", "/authors");
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("author", service.findById(id));
        return form(model, "Edit author", "/authors/" + id);
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("author") Author author,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                service.update(id, author);
                redirect.addFlashAttribute("message", "Author " + author.getFullName() + " updated.");
                return "redirect:/authors";
            } catch (BusinessRuleException e) {
                FormErrors.add(result, e);
            }
        }
        return form(model, "Edit author", "/authors/" + id);
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.delete(id);
            redirect.addFlashAttribute("message", "Author deleted.");
        } catch (BusinessRuleException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/authors";
    }

    private String form(Model model, String title, String action) {
        model.addAttribute("formTitle", title);
        model.addAttribute("formAction", action);
        return "authors/form";
    }
}
