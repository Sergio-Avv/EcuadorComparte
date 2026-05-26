package com.ecuadorcomparte.ecuador_comparte.controller.admin;

import com.ecuadorcomparte.ecuador_comparte.dto.NewsDTO;
import com.ecuadorcomparte.ecuador_comparte.model.News;
import com.ecuadorcomparte.ecuador_comparte.service.NewsService;
import com.ecuadorcomparte.ecuador_comparte.service.report.ExcelReportService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/news")
public class AdminNewsController {

    private final NewsService service;
    private final ExcelReportService excelReportService;

    public AdminNewsController(NewsService service, ExcelReportService excelReportService) {
        this.service = service;
        this.excelReportService = excelReportService;
    }

    @GetMapping
    public String getNewsList(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) News.Status status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            Model model) {

        boolean hasFilter = (title != null && !title.isBlank())
                || status != null
                || dateFrom != null
                || dateTo != null;

        List<News> results;
        if (hasFilter) {
            LocalDateTime from = dateFrom != null ? dateFrom.atStartOfDay() : null;
            LocalDateTime to   = dateTo   != null ? dateTo.atTime(23, 59, 59) : null;
            results = service.filterNews(title, status, from, to);
        } else {
            results = service.findAll();
        }

        model.addAttribute("newsList", results);
        model.addAttribute("newsStatuses", News.Status.values());
        model.addAttribute("filterTitle", title);
        model.addAttribute("filterStatus", status);
        model.addAttribute("filterDateFrom", dateFrom);
        model.addAttribute("filterDateTo", dateTo);
        model.addAttribute("hasFilter", hasFilter);
        return "admin/news/list";
    }

    @GetMapping("/export/excel")
    public void exportExcel(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) News.Status status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            HttpServletResponse response) throws IOException {

        boolean hasFilter = (title != null && !title.isBlank())
                || status != null
                || dateFrom != null
                || dateTo != null;

        List<News> results;
        if (hasFilter) {
            LocalDateTime from = dateFrom != null ? dateFrom.atStartOfDay() : null;
            LocalDateTime to   = dateTo   != null ? dateTo.atTime(23, 59, 59) : null;
            results = service.filterNews(title, status, from, to);
        } else {
            results = service.findAll();
        }

        byte[] bytes = excelReportService.generateNewsReport(results);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=noticias.xlsx");
        response.getOutputStream().write(bytes);
    }

    @GetMapping("/create")
    public String getCreateNewsTemplate(Model model) {
        model.addAttribute("newsRequest", new NewsDTO());
        model.addAttribute("newsStatuses", News.Status.values());
        return "admin/news/create";
    }

    @PostMapping
    public String createNews(@ModelAttribute("newsRequest") NewsDTO dto) {
        service.save(dto);
        return "redirect:/admin/news";
    }

    @GetMapping("/edit/{id}")
    public String getUpdateNewsTemplate(@PathVariable Long id, Model model) {
        Optional<News> findResult = service.findById(id);
        if (findResult.isEmpty()) {
            model.addAttribute("title", "Editar noticia");
            return "error/not-found";
        }
        News news = findResult.get();
        NewsDTO dto = new NewsDTO();
        dto.setTitle(news.getTitle());
        dto.setSummary(news.getSummary());
        dto.setContent(news.getContent());
        dto.setImageUrl(news.getImageUrl());
        dto.setAuthor(news.getAuthor());
        dto.setStatus(news.getStatus());
        dto.setPublishedAt(news.getPublishedAt());

        model.addAttribute("news", dto);
        model.addAttribute("newsId", id);
        model.addAttribute("newsStatuses", News.Status.values());
        return "admin/news/update";
    }

    @PutMapping("/{id}")
    public String updateNews(@PathVariable Long id,
                             @ModelAttribute NewsDTO dto,
                             Model model) {
        try {
            service.update(id, dto);
        } catch (EntityNotFoundException e) {
            model.addAttribute("title", "Editar noticia");
            return "error/not-found";
        }
        return "redirect:/admin/news";
    }

    @GetMapping("/delete/{id}")
    public String getDeleteNewsTemplate(@PathVariable Long id, Model model) {
        Optional<News> findResult = service.findById(id);
        if (findResult.isEmpty()) {
            model.addAttribute("title", "Eliminar noticia");
            return "error/not-found";
        }
        model.addAttribute("news", findResult.get());
        return "admin/news/delete";
    }

    @DeleteMapping("/{id}")
    public String deleteNews(@PathVariable Long id, Model model) {
        try {
            service.delete(id);
        } catch (EntityNotFoundException e) {
            model.addAttribute("title", "Eliminar noticia");
            return "error/not-found";
        }
        return "redirect:/admin/news";
    }
}
