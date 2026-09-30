package lv.turiba.library.controller;

import jakarta.validation.Valid;
import lv.turiba.library.model.Member;
import lv.turiba.library.service.BusinessRuleException;
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

/** Library members (readers): list, add, edit, delete. */
@Controller
@RequestMapping("/members")
public class MemberController {

    private final MemberService service;

    public MemberController(MemberService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("members", service.findAll());
        return "members/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("member", new Member());
        return form(model, "Add member", "/members");
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("member") Member member, BindingResult result,
                         Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                service.create(member);
                redirect.addFlashAttribute("message", "Member " + member.getFullName() + " added.");
                return "redirect:/members";
            } catch (BusinessRuleException e) {
                FormErrors.add(result, e);
            }
        }
        return form(model, "Add member", "/members");
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("member", service.findById(id));
        return form(model, "Edit member", "/members/" + id);
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("member") Member member,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                service.update(id, member);
                redirect.addFlashAttribute("message", "Member " + member.getFullName() + " updated.");
                return "redirect:/members";
            } catch (BusinessRuleException e) {
                FormErrors.add(result, e);
            }
        }
        return form(model, "Edit member", "/members/" + id);
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.delete(id);
            redirect.addFlashAttribute("message", "Member deleted.");
        } catch (BusinessRuleException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/members";
    }

    private String form(Model model, String title, String action) {
        model.addAttribute("formTitle", title);
        model.addAttribute("formAction", action);
        return "members/form";
    }
}
