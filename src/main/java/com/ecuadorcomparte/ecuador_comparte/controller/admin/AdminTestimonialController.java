package com.ecuadorcomparte.ecuador_comparte.controller.admin;

import com.ecuadorcomparte.ecuador_comparte.dto.TestimonialDTO;
import com.ecuadorcomparte.ecuador_comparte.model.Testimonial;
import com.ecuadorcomparte.ecuador_comparte.service.TestimonialService;
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
@RequestMapping("/admin/testimonials")
public class AdminTestimonialController {

    private final TestimonialService service;
    private final ExcelReportService excelReportService;

    public AdminTestimonialController(TestimonialService service, ExcelReportService excelReportService) {
        this.service = service;
        this.excelReportService = excelReportService;
    }

    @GetMapping
    public String getTestimonialList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            Model model) {

        boolean hasFilter = (name != null && !name.isBlank())
                || dateFrom != null
                || dateTo != null;

        List<Testimonial> results;
        if (hasFilter) {
            LocalDateTime from = dateFrom != null ? dateFrom.atStartOfDay() : null;
            LocalDateTime to   = dateTo   != null ? dateTo.atTime(23, 59, 59) : null;
            results = service.filterTestimonials(name, from, to);
        } else {
            results = service.findAll();
        }

        model.addAttribute("testimonials", results);
        model.addAttribute("filterName", name);
        model.addAttribute("filterDateFrom", dateFrom);
        model.addAttribute("filterDateTo", dateTo);
        model.addAttribute("hasFilter", hasFilter);
        return "admin/testimonials/list";
    }

    @GetMapping("/export/excel")
    public void exportExcel(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            HttpServletResponse response) throws IOException {

        boolean hasFilter = (name != null && !name.isBlank())
                || dateFrom != null
                || dateTo != null;

        List<Testimonial> results;
        if (hasFilter) {
            LocalDateTime from = dateFrom != null ? dateFrom.atStartOfDay() : null;
            LocalDateTime to   = dateTo   != null ? dateTo.atTime(23, 59, 59) : null;
            results = service.filterTestimonials(name, from, to);
        } else {
            results = service.findAll();
        }

        byte[] bytes = excelReportService.generateTestimonialReport(results);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=testimonios.xlsx");
        response.getOutputStream().write(bytes);
    }

    @GetMapping("/create")
    public String getCreateTestimonialTemplate(Model model) {
        model.addAttribute("testimonialRequest", new TestimonialDTO());
        return "admin/testimonials/create";
    }

    @PostMapping
    public String createTestimonial(@ModelAttribute("testimonialRequest") TestimonialDTO dto) {
        service.save(dto);
        return "redirect:/admin/testimonials";
    }

    @GetMapping("/edit/{id}")
    public String getUpdateTestimonialTemplate(@PathVariable Long id, Model model) {
        Optional<Testimonial> findResult = service.findById(id);
        if (findResult.isEmpty()) {
            model.addAttribute("title", "Editar testimonio");
            return "error/not-found";
        }
        Testimonial t = findResult.get();
        TestimonialDTO dto = new TestimonialDTO();
        dto.setName(t.getName());
        dto.setPhotoUrl(t.getPhotoUrl());
        dto.setInstagramUrl(t.getInstagramUrl());
        dto.setFacebookUrl(t.getFacebookUrl());

        model.addAttribute("testimonial", dto);
        model.addAttribute("testimonialId", id);
        return "admin/testimonials/update";
    }

    @PutMapping("/{id}")
    public String updateTestimonial(@PathVariable Long id,
                                    @ModelAttribute("testimonial") TestimonialDTO dto,
                                    Model model) {
        try {
            service.update(id, dto);
        } catch (EntityNotFoundException e) {
            model.addAttribute("title", "Editar testimonio");
            return "error/not-found";
        }
        return "redirect:/admin/testimonials";
    }

    @GetMapping("/delete/{id}")
    public String getDeleteTestimonialTemplate(@PathVariable Long id, Model model) {
        Optional<Testimonial> findResult = service.findById(id);
        if (findResult.isEmpty()) {
            model.addAttribute("title", "Eliminar testimonio");
            return "error/not-found";
        }
        model.addAttribute("testimonial", findResult.get());
        return "admin/testimonials/delete";
    }

    @DeleteMapping("/{id}")
    public String deleteTestimonial(@PathVariable Long id, Model model) {
        try {
            service.delete(id);
        } catch (EntityNotFoundException e) {
            model.addAttribute("title", "Eliminar testimonio");
            return "error/not-found";
        }
        return "redirect:/admin/testimonials";
    }
}
