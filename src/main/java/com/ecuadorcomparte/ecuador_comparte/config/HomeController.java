package com.ecuadorcomparte.ecuador_comparte.config;

import com.ecuadorcomparte.ecuador_comparte.model.News;
import com.ecuadorcomparte.ecuador_comparte.service.NewsService;
import com.ecuadorcomparte.ecuador_comparte.service.TestimonialService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final NewsService newsService;

    private final TestimonialService testimonialService;


    public HomeController(NewsService newsService, TestimonialService testimonialService) {
        this.newsService = newsService;
        this.testimonialService = testimonialService;
    }

    @GetMapping("/")
    public String getHome(Model model) {
        model.addAttribute("newsList", newsService.findByStatus(News.Status.PUBLISHED));
        model.addAttribute("testimonials", testimonialService.findAll());

        return "index";
    }

    @GetMapping("/sobre-nosotros")
    public String getAbout() {
        return "about";
    }


    @GetMapping("/login")
    public String getLogin() {
        return "auth/login";
    }


    @GetMapping("/admin")
    public String getDashboard() {
        return "admin/dashboard";
    }
}