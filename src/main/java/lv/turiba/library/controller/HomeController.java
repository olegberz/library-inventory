package lv.turiba.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Sends the root address straight to the book list. */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/books";
    }
}
