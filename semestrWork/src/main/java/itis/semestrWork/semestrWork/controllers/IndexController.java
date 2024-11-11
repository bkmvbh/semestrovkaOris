package itis.semestrWork.semestrWork.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class IndexController {

    @GetMapping("/main")
    public String index(){
        return "python";
    }
}
