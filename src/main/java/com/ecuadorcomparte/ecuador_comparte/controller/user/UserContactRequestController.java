package com.ecuadorcomparte.ecuador_comparte.controller.user;

import com.ecuadorcomparte.ecuador_comparte.dto.ContactRequestDTO;
import com.ecuadorcomparte.ecuador_comparte.service.ContactRequestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contacto")
public class UserContactRequestController {

    private final ContactRequestService service;

    public UserContactRequestController(ContactRequestService service) {
        this.service = service;
    }

    @GetMapping
    public String getContactPage(Model model) {
        model.addAttribute("contactRequest", new ContactRequestDTO());
        return "contact";
    }

    @PostMapping
    public String createContactRequest(@ModelAttribute ContactRequestDTO dto, Model model) {
        service.save(dto);
        return "redirect:/contacto?success";
    }
}