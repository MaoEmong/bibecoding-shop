package com.example.myproject.common;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("products", List.of(
            Map.of("name", "머그컵", "price", "5,000", "badge", "NEW"),
            Map.of("name", "텀블러", "price", "12,000", "badge", "HOT"),
            Map.of("name", "에코백", "price", "8,000", "badge", ""),
            Map.of("name", "키링", "price", "3,000", "badge", "" )
        ));
        return "index";
    }
}
