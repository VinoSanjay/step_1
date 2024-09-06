package com.step_1.step_1.controller;
import com.step_1.step_1.model.AppUser;
import com.step_1.step_1.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {
    @Autowired
    private AppUserRepository repo;

    @GetMapping({"","/"})
    public String homePage(Model model, Principal principal) {

        AppUser user = repo.findByEmail(principal.getName());
        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("lastName", user.getLastName());

        return "index"; // the name of your HTML page
    }

    @GetMapping("/contact")
    public String contact(){
        return "contact";
    }
}
