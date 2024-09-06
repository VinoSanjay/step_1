package com.step_1.step_1.controller;

import com.step_1.step_1.model.AppUser;
import jakarta.validation.Valid;
import org.aspectj.apache.bcel.classfile.Field;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import com.step_1.step_1.model.RegisterDTO;
import com.step_1.step_1.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;

@Controller
public class AccountController {

    @Autowired
    private AppUserRepository repo;

    @GetMapping("/register")
    public String register(Model model) {
        RegisterDTO registerDTO = new RegisterDTO();
        model.addAttribute("registerDto", registerDTO); // Explicitly naming the attribute
        model.addAttribute("success",false);
        return "register";
    }

    @PostMapping("/register")
    public String register(Model model,
                           @Valid @ModelAttribute RegisterDTO registerDTO,
                           BindingResult result){
        if(!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())){
            result.addError (
                new FieldError("registerDto",
                        "confirmPassword",
                        "Password and Confirm Password do not match")
            );
        }
        AppUser appUser=repo.findByEmail(registerDTO.getEmail());
        if(appUser!=null){
            result.addError(
                    new FieldError("registerDto",
                            "email",
                            "Email already used")
            );
        }

        if(result.hasErrors()){
            return "register";
        }

        try{
            var bCryptEncoder=new BCryptPasswordEncoder();
            AppUser user=new AppUser();
            user.setFirstName(registerDTO.getFirstName());
            user.setLastName(registerDTO.getLastName());
            user.setEmail(registerDTO.getEmail());
            user.setPhone(registerDTO.getPhone());
            user.setAddress(registerDTO.getAddress());
            user.setRole("Client");
            user.setCreatedAt(new Date());
            user.setPassword(bCryptEncoder.encode(registerDTO.getPassword()));

            repo.save(user);

            model.addAttribute("registerDto",new RegisterDTO());
            model.addAttribute("success",true);
        }
        catch (Exception e){
            result.addError(
                    new FieldError("registerDto",
                            "firstName",
                            e.getMessage())
            );
        }

        return "register";

    }
    @GetMapping("/login")
    public String login() {
        // Redirect to index page if already logged in
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/index";
        }
        return "login";
    }

}
