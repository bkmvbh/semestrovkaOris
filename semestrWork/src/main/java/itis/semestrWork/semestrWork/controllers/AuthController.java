package itis.semestrWork.semestrWork.controllers;

import itis.semestrWork.semestrWork.dto.RegistrationFormDto;
import itis.semestrWork.semestrWork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;


    @GetMapping("/registration")
    public String registerPage() {
        return "registration";
    }

    @PostMapping("/registration/new")
    public String registerForm(Model model, @ModelAttribute RegistrationFormDto form) {
        if (!form.getPassword().equals(form.getRepeatPassword())) {
            model.addAttribute("error", "Пароли не совпадают");
            return "registration";
        }

        try {
            userService.saveUser(form);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "registration";
        }

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null){
            model.addAttribute("message","Пароль или логин неверны");
        }
        return "login";
    }
}
