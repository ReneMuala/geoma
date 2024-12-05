package org.descartes.geoma.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/page")
    public String page(Model model) {
        // Add data to the model that will be used to render the page
        model.addAttribute("name", "Descartes!");
        return "page";
    }
}
