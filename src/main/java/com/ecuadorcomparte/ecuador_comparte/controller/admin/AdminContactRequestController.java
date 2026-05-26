package com.ecuadorcomparte.ecuador_comparte.controller.admin;

import com.ecuadorcomparte.ecuador_comparte.dto.ContactRequestStatusDTO;
import com.ecuadorcomparte.ecuador_comparte.model.ContactRequest;
import com.ecuadorcomparte.ecuador_comparte.service.ContactRequestService;
import com.ecuadorcomparte.ecuador_comparte.service.report.ExcelReportService;
import com.ecuadorcomparte.ecuador_comparte.service.report.WeeklyReportScheduler;
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
@RequestMapping("/admin/contact-requests")
public class AdminContactRequestController {

    private final ContactRequestService service;
    private final WeeklyReportScheduler weeklyReportScheduler;
    private final ExcelReportService excelReportService;

    public AdminContactRequestController(ContactRequestService service,
                                         WeeklyReportScheduler weeklyReportScheduler,
                                         ExcelReportService excelReportService) {
        this.service = service;
        this.weeklyReportScheduler = weeklyReportScheduler;
        this.excelReportService = excelReportService;
    }

    @GetMapping
    public String getContactRequestList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) ContactRequest.Purpose purpose,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            Model model) {

        boolean hasFilter = (name != null && !name.isBlank())
                || (email != null && !email.isBlank())
                || purpose != null
                || dateFrom != null
                || dateTo != null;

        List<ContactRequest> results;
        if (hasFilter) {
            LocalDateTime from = dateFrom != null ? dateFrom.atStartOfDay() : null;
            LocalDateTime to   = dateTo   != null ? dateTo.atTime(23, 59, 59) : null;
            results = service.filterContactRequests(name, email, purpose, from, to);
        } else {
            results = service.findAll();
        }

        model.addAttribute("contactRequests", results);
        model.addAttribute("statuses", ContactRequest.Status.values());
        model.addAttribute("purposes", ContactRequest.Purpose.values());
        // mantener filtros en el form
        model.addAttribute("filterName", name);
        model.addAttribute("filterEmail", email);
        model.addAttribute("filterPurpose", purpose);
        model.addAttribute("filterDateFrom", dateFrom);
        model.addAttribute("filterDateTo", dateTo);
        model.addAttribute("hasFilter", hasFilter);
        return "admin/contact-requests/list";
    }

    @GetMapping("/export/excel")
    public void exportExcel(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) ContactRequest.Purpose purpose,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            HttpServletResponse response) throws IOException {

        boolean hasFilter = (name != null && !name.isBlank())
                || (email != null && !email.isBlank())
                || purpose != null
                || dateFrom != null
                || dateTo != null;

        List<ContactRequest> results;
        LocalDateTime from, to;
        if (hasFilter) {
            from = dateFrom != null ? dateFrom.atStartOfDay() : null;
            to   = dateTo   != null ? dateTo.atTime(23, 59, 59) : null;
            results = service.filterContactRequests(name, email, purpose, from, to);
        } else {
            results = service.findAll();
            from = LocalDateTime.now().minusDays(365);
            to   = LocalDateTime.now();
        }

        String subtitle = (from != null && to != null)
                ? "Período: " + from.toLocalDate() + "  →  " + to.toLocalDate()
                : "Exportado el " + LocalDateTime.now().toLocalDate();

        byte[] bytes = excelReportService.generateContactRequestReport(results,
                "Ecuador Comparte — Solicitudes de Contacto", subtitle);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=solicitudes.xlsx");
        response.getOutputStream().write(bytes);
    }

    @GetMapping("/{id}")
    public String getContactRequestDetail(@PathVariable Long id, Model model) {
        Optional<ContactRequest> findResult = service.findById(id);
        if (findResult.isEmpty()) {
            model.addAttribute("title", "Detalle de solicitud");
            return "error/not-found";
        }
        model.addAttribute("contactRequest", findResult.get());
        model.addAttribute("allStatuses", ContactRequest.Status.values());
        return "admin/contact-requests/detail";
    }

    @PatchMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @ModelAttribute ContactRequestStatusDTO dto,
                               Model model) {
        try {
            service.updateStatus(id, dto);
        } catch (EntityNotFoundException e) {
            model.addAttribute("title", "Actualizar estado");
            return "error/not-found";
        }
        return "redirect:/admin/contact-requests/" + id + "?updated";
    }

    @GetMapping("/delete/{id}")
    public String getDeleteContactRequestTemplate(@PathVariable Long id, Model model) {
        Optional<ContactRequest> findResult = service.findById(id);
        if (findResult.isEmpty()) {
            model.addAttribute("title", "Eliminar solicitud");
            return "error/not-found";
        }
        model.addAttribute("contactRequest", findResult.get());
        return "admin/contact-requests/delete";
    }

    @DeleteMapping("/{id}")
    public String deleteContactRequest(@PathVariable Long id, Model model) {
        try {
            service.delete(id);
        } catch (EntityNotFoundException e) {
            model.addAttribute("title", "Eliminar solicitud");
            return "error/not-found";
        }
        return "redirect:/admin/contact-requests";
    }

    @GetMapping("/report/send-now")
    public String sendReportNow() {
        weeklyReportScheduler.sendWeeklyReport();
        return "redirect:/admin/contact-requests?reportSent";
    }
}
