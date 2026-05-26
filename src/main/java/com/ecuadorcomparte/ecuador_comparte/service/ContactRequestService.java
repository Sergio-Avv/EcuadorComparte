package com.ecuadorcomparte.ecuador_comparte.service;

import com.ecuadorcomparte.ecuador_comparte.dto.ContactRequestDTO;
import com.ecuadorcomparte.ecuador_comparte.dto.ContactRequestStatusDTO;
import com.ecuadorcomparte.ecuador_comparte.model.ContactRequest;
import com.ecuadorcomparte.ecuador_comparte.repository.ContactRequestRepository;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ContactRequestService {

    private static final Logger log = LoggerFactory.getLogger(ContactRequestService.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ContactRequestRepository repository;
    private final JavaMailSender mailSender;

    @Value("${app.report.sender}")
    private String sender;

    public ContactRequestService(ContactRequestRepository repository, JavaMailSender mailSender) {
        this.repository = repository;
        this.mailSender = mailSender;
    }

    public ContactRequest save(ContactRequestDTO dto) {
        ContactRequest entity = new ContactRequest();
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setPurpose(dto.getPurpose());
        entity.setStatus(ContactRequest.Status.PENDING);
        return repository.save(entity);
    }

    public ContactRequest updateStatus(Long id, ContactRequestStatusDTO dto) {
        ContactRequest existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));
        ContactRequest.Status previousStatus = existing.getStatus();
        existing.setStatus(dto.getStatus());
        ContactRequest saved = repository.save(existing);

        // Enviar correo solo si el estado cambió
        if (!dto.getStatus().equals(previousStatus)) {
            sendStatusUpdateEmail(saved);
        }

        return saved;
    }

    private void sendStatusUpdateEmail(ContactRequest req) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(sender.trim());
            helper.setTo(req.getEmail());
            helper.setSubject("📬 Actualización de tu solicitud — Ecuador Comparte");

            String badgeColor = switch (req.getStatus()) {
                case PENDING    -> "#856404";
                case IN_PROGRESS -> "#0a58ca";
                case DONE       -> "#146c43";
            };
            String badgeBg = switch (req.getStatus()) {
                case PENDING    -> "#fff3cd";
                case IN_PROGRESS -> "#cfe2ff";
                case DONE       -> "#d1e7dd";
            };

            String body = "<html><body style='font-family: Arial, sans-serif; color: #03204A; max-width: 600px; margin: 0 auto;'>"
                    + "<div style='background: #034EA2; padding: 24px 32px; border-radius: 8px 8px 0 0;'>"
                    + "<h2 style='color: #FFDD00; margin: 0;'>Ecuador Comparte</h2>"
                    + "</div>"
                    + "<div style='border: 1px solid #e0e0e0; border-top: none; padding: 32px; border-radius: 0 0 8px 8px;'>"
                    + "<p>Hola <strong>" + req.getName() + "</strong>,</p>"
                    + "<p>El estado de tu solicitud de contacto ha sido actualizado.</p>"
                    + "<table style='width: 100%; border-collapse: collapse; margin: 1.5rem 0;'>"
                    + "<tr style='background:#f8f9fa;'><td style='padding:10px 14px; font-weight:600;'>Finalidad</td>"
                    + "<td style='padding:10px 14px;'>" + req.getPurpose().getVisualName() + "</td></tr>"
                    + "<tr><td style='padding:10px 14px; font-weight:600;'>Fecha de solicitud</td>"
                    + "<td style='padding:10px 14px;'>" + (req.getCreatedAt() != null ? req.getCreatedAt().format(FMT) : "—") + "</td></tr>"
                    + "<tr style='background:#f8f9fa;'><td style='padding:10px 14px; font-weight:600;'>Nuevo estado</td>"
                    + "<td style='padding:10px 14px;'><span style='background:" + badgeBg + "; color:" + badgeColor
                    + "; padding: 4px 12px; border-radius: 20px; font-weight:600;'>" + req.getStatus().getVisualName() + "</span></td></tr>"
                    + "</table>"
                    + "<p>Si tienes alguna pregunta, puedes responder a este correo o contactarnos directamente.</p>"
                    + "<hr style='border-color: #FFDD00; margin: 2rem 0;'/>"
                    + "<p style='font-size: 12px; color: #888;'>Este correo fue generado automáticamente por el sistema Ecuador Comparte. Por favor no respondas directamente a este mensaje.</p>"
                    + "</div></body></html>";

            helper.setText(body, true);
            mailSender.send(message);
            log.info("Correo de actualización de estado enviado a {} (solicitud #{})", req.getEmail(), req.getId());

        } catch (Exception e) {
            log.error("Error al enviar correo de actualización de estado para solicitud #{}: {}", req.getId(), e.getMessage(), e);
        }
    }

    public List<ContactRequest> findAll() {
        return repository.findAll();
    }

    public List<ContactRequest> filterContactRequests(String name, String email,
                                                       ContactRequest.Purpose purpose,
                                                       LocalDateTime dateFrom, LocalDateTime dateTo) {
        String nameTrim  = (name  != null && !name.isBlank())  ? name.trim()  : null;
        String emailTrim = (email != null && !email.isBlank()) ? email.trim() : null;
        return repository.filterContactRequests(nameTrim, emailTrim, purpose, dateFrom, dateTo);
    }

    public List<ContactRequest> findByPurpose(ContactRequest.Purpose purpose) {
        return repository.findByPurpose(purpose);
    }

    public List<ContactRequest> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to) {
        return repository.findByCreatedAtBetween(from, to);
    }

    public Optional<ContactRequest> findById(Long id) {
        return repository.findById(id);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
