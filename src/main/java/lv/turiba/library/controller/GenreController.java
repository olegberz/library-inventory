package lv.turiba.library.controller;

import jakarta.validation.Valid;
import lv.turiba.library.model.Genre;
import lv.turiba.library.service.BusinessRuleException;
import lv.turiba.library.service.GenreService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Genres: list, add, edit, delete. Same URL pattern as the other entities. */
@Controller
@RequestMapping("/genres")
public class GenreController {

    private final GenreService service;

    public GenreController(GenreService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("genres", service.findAll());
        return "genres/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("genre", new Genre());
        return form(model, "Add genre", "/genres");
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("genre") Genre genre, BindingResult result,
                         Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                service.create(genre);
                redirect.addFlashAttribute("message", "Genre \"" + genre.getName() + "\" added.");
                return "redirect:/genres";
            } catch (BusinessRuleException e) {
                FormErrors.add(result, e);
            }
        }
        return form(model, "Add genre", "/genres");
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("genre", service.findById(id));
        return form(model, "Edit genre", "/genres/" + id);
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("genre") Genre genre,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                service.update(id, genre);
                redirect.addFlashAttribute("message", "Genre \"" + genre.getName() + "\" updated.");
                return "redirect:/genres";
            } catch (BusinessRuleException e) {
                FormErrors.add(result, e);
            }
        }
        return form(model, "Edit genre", "/genres/" + id);
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.delete(id);
            redirect.addFlashAttribute("message", "Genre deleted.");
        } catch (BusinessRuleException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/genres";
    }

    private String form(Model model, String title, String action) {
        model.addAttribute("formTitle", title);
        model.addAttribute("formAction", action);
        return "genres/form";
    }
}
