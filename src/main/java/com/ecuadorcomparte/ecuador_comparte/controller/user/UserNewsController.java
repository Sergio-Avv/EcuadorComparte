package com.ecuadorcomparte.ecuador_comparte.controller.user;

import com.ecuadorcomparte.ecuador_comparte.model.News;
import com.ecuadorcomparte.ecuador_comparte.service.NewsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/noticias")
public class UserNewsController {

    private final NewsService service;


    public UserNewsController(NewsService service) {
        this.service = service;
    }
    @GetMapping
    public String getNewsList(Model model) {
        model.addAttribute("newsList", service.findByStatus(News.Status.PUBLISHED));
        return "user/news/list";
    }

    @GetMapping("/{id}")
    public String getNewsDetail(@PathVariable Long id, Model model) {
        Optional<News> findResult = service.findById(id);

        if (findResult.isEmpty()) {
            model.addAttribute("title", "Noticia no encontrada");
            return "error/not-found";
        }

        model.addAttribute("news", findResult.get());
        return "user/news/detail";
    }
}