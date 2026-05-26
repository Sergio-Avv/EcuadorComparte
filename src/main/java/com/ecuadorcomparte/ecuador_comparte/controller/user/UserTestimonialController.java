package com.ecuadorcomparte.ecuador_comparte.controller.user;

import com.ecuadorcomparte.ecuador_comparte.service.TestimonialService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/testimonios")
public class UserTestimonialController {

    private final TestimonialService service;
    public UserTestimonialController(TestimonialService service) {
        this.service = service;
    }

    @GetMapping
    public String getTestimonialList(Model model) {
        model.addAttribute("testimonials", service.findAll());
        return "user/testimonial/list";
    }
}