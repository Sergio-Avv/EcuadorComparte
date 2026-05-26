package com.ecuadorcomparte.ecuador_comparte.service.report;

import com.ecuadorcomparte.ecuador_comparte.model.ContactRequest;
import com.ecuadorcomparte.ecuador_comparte.service.ContactRequestService;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class WeeklyReportScheduler {

    private static final Logger log = LoggerFactory.getLogger(WeeklyReportScheduler.class);
    private static final DateTimeFormatter LABEL = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ContactRequestService contactRequestService;
    private final ExcelReportService excelReportService;
    private final JavaMailSender mailSender;

    @Value("${app.report.recipient}")
    private String recipient;

    @Value("${app.report.sender}")
    private String sender;

    public WeeklyReportScheduler(ContactRequestService contactRequestService,
                                 ExcelReportService excelReportService,
                                 JavaMailSender mailSender) {
        this.contactRequestService = contactRequestService;
        this.excelReportService = excelReportService;
        this.mailSender = mailSender;
    }

    @Scheduled(cron = "0 0 0 * * MON")
    public void sendWeeklyReport() {
        LocalDateTime to   = LocalDateTime.now();
        LocalDateTime from = to.minusDays(7);

        log.info("Generando reporte semanal: {} → {}", from.format(LABEL), to.format(LABEL));

        try {
            List<ContactRequest> requests = contactRequestService.findByCreatedAtBetween(from, to);

            byte[] excel = excelReportService.generateWeeklyContactRequestReport(requests, from, to);

            String filename = "reporte_solicitudes_"
                    + from.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                    + "_"
                    + to.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                    + ".xlsx";

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(sender);
            helper.setTo(recipient);
            helper.setSubject("📊 Reporte Semanal — Solicitudes de Contacto ("
                    + from.format(LABEL) + " al " + to.format(LABEL) + ")");

            helper.setText(
                    "<html><body style='font-family: Arial, sans-serif; color: #03204A;'>"
                    + "<h2 style='color: #034EA2;'>Ecuador Comparte</h2>"
                    + "<p>Hola,</p>"
                    + "<p>Se adjunta el reporte semanal de solicitudes de contacto recibidas "
                    + "del <strong>" + from.format(LABEL) + "</strong> al "
                    + "<strong>" + to.format(LABEL) + "</strong>.</p>"
                    + "<p><strong>Total de solicitudes:</strong> " + requests.size() + "</p>"
                    + "<hr style='border-color: #FFDD00;'/>"
                    + "<p style='font-size: 12px; color: #888;'>Este correo fue generado automáticamente por el sistema Ecuador Comparte.</p>"
                    + "</body></html>",
                    true
            );

            helper.addAttachment(filename, new ByteArrayResource(excel));
            mailSender.send(message);

            log.info("Reporte enviado a {} con {} solicitudes.", recipient, requests.size());

        } catch (Exception e) {
            log.error("Error al generar o enviar el reporte semanal: {}", e.getMessage(), e);
        }
    }
}
