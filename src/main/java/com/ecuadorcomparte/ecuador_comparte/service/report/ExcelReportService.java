package com.ecuadorcomparte.ecuador_comparte.service.report;

import com.ecuadorcomparte.ecuador_comparte.model.ContactRequest;
import com.ecuadorcomparte.ecuador_comparte.model.News;
import com.ecuadorcomparte.ecuador_comparte.model.Testimonial;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelReportService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_ONLY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static class Styles {
        CellStyle title, header, subHeader, even, odd, centerEven, centerOdd, total;

        static Styles build(Workbook wb) {
            Styles s = new Styles();

            Font titleFont = wb.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleFont.setColor(IndexedColors.WHITE.getIndex());
            s.title = wb.createCellStyle();
            s.title.setFont(titleFont);
            s.title.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            s.title.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            s.title.setAlignment(HorizontalAlignment.CENTER);
            s.title.setVerticalAlignment(VerticalAlignment.CENTER);

            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            s.header = wb.createCellStyle();
            s.header.setFont(headerFont);
            s.header.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            s.header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            s.header.setAlignment(HorizontalAlignment.CENTER);
            s.header.setBorderBottom(BorderStyle.THIN);
            s.header.setBorderTop(BorderStyle.THIN);
            s.header.setBottomBorderColor(IndexedColors.WHITE.getIndex());

            Font subFont = wb.createFont();
            subFont.setItalic(true);
            subFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
            s.subHeader = wb.createCellStyle();
            s.subHeader.setFont(subFont);
            s.subHeader.setAlignment(HorizontalAlignment.CENTER);

            s.even = wb.createCellStyle();
            s.even.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            s.even.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            s.even.setBorderBottom(BorderStyle.THIN);
            s.even.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

            s.odd = wb.createCellStyle();
            s.odd.setFillForegroundColor(IndexedColors.WHITE.getIndex());
            s.odd.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            s.odd.setBorderBottom(BorderStyle.THIN);
            s.odd.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

            s.centerEven = wb.createCellStyle();
            s.centerEven.cloneStyleFrom(s.even);
            s.centerEven.setAlignment(HorizontalAlignment.CENTER);

            s.centerOdd = wb.createCellStyle();
            s.centerOdd.cloneStyleFrom(s.odd);
            s.centerOdd.setAlignment(HorizontalAlignment.CENTER);

            Font totalFont = wb.createFont();
            totalFont.setBold(true);
            s.total = wb.createCellStyle();
            s.total.setFont(totalFont);
            s.total.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
            s.total.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            s.total.setAlignment(HorizontalAlignment.CENTER);
            s.total.setBorderTop(BorderStyle.MEDIUM);

            return s;
        }
    }

    private int addTitleRows(Sheet sheet, String title, String subtitle, int cols, Styles s) {
        Row titleRow = sheet.createRow(0);
        titleRow.setHeightInPoints(28);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(title);
        titleCell.setCellStyle(s.title);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, cols - 1));

        Row subRow = sheet.createRow(1);
        subRow.setHeightInPoints(18);
        Cell subCell = subRow.createCell(0);
        subCell.setCellValue(subtitle);
        subCell.setCellStyle(s.subHeader);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, cols - 1));

        sheet.createRow(2);
        return 3;
    }

    private void addHeaderRow(Sheet sheet, String[] headers, int rowNum, Styles s) {
        Row headerRow = sheet.createRow(rowNum);
        headerRow.setHeightInPoints(20);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(s.header);
        }
    }

    private void addTotalRow(Sheet sheet, int rowNum, int count, String label, int cols, Styles s) {
        Row totalRow = sheet.createRow(rowNum);
        totalRow.setHeightInPoints(18);
        Cell totalLabel = totalRow.createCell(0);
        totalLabel.setCellValue(label + ": " + count + " registro(s)");
        totalLabel.setCellStyle(s.total);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, cols - 1));
    }


    public byte[] generateWeeklyContactRequestReport(List<ContactRequest> requests,
                                                     LocalDateTime from,
                                                     LocalDateTime to) throws IOException {
        return generateContactRequestReport(requests,
                "Ecuador Comparte — Reporte Semanal de Solicitudes",
                "Período: " + from.format(FORMATTER) + "  →  " + to.format(FORMATTER));
    }

    public byte[] generateContactRequestReport(List<ContactRequest> requests,
                                                String title, String subtitle) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Solicitudes");
            sheet.setColumnWidth(0, 8 * 256);
            sheet.setColumnWidth(1, 22 * 256);
            sheet.setColumnWidth(2, 28 * 256);
            sheet.setColumnWidth(3, 16 * 256);
            sheet.setColumnWidth(4, 22 * 256);
            sheet.setColumnWidth(5, 18 * 256);
            sheet.setColumnWidth(6, 20 * 256);

            Styles s = Styles.build(wb);
            addTitleRows(sheet, title, subtitle, 7, s);
            addHeaderRow(sheet, new String[]{"#", "Nombre", "Correo", "Teléfono", "Finalidad", "Estado", "Fecha"}, 3, s);

            int rowNum = 4;
            for (int i = 0; i < requests.size(); i++) {
                ContactRequest req = requests.get(i);
                Row row = sheet.createRow(rowNum++);
                boolean even = i % 2 == 0;

                row.createCell(0).setCellValue(req.getId()); row.getCell(0).setCellStyle(even ? s.centerEven : s.centerOdd);
                row.createCell(1).setCellValue(req.getName()); row.getCell(1).setCellStyle(even ? s.even : s.odd);
                row.createCell(2).setCellValue(req.getEmail()); row.getCell(2).setCellStyle(even ? s.even : s.odd);
                row.createCell(3).setCellValue(req.getPhone()); row.getCell(3).setCellStyle(even ? s.centerEven : s.centerOdd);
                row.createCell(4).setCellValue(req.getPurpose().getVisualName()); row.getCell(4).setCellStyle(even ? s.even : s.odd);
                row.createCell(5).setCellValue(req.getStatus().getVisualName()); row.getCell(5).setCellStyle(even ? s.centerEven : s.centerOdd);
                String fecha = req.getCreatedAt() != null ? req.getCreatedAt().format(FORMATTER) : "";
                row.createCell(6).setCellValue(fecha); row.getCell(6).setCellStyle(even ? s.centerEven : s.centerOdd);
            }

            addTotalRow(sheet, rowNum, requests.size(), "Total solicitudes", 7, s);
            wb.write(out);
            return out.toByteArray();
        }
    }


    public byte[] generateTestimonialReport(List<Testimonial> testimonials) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Testimonios");
            sheet.setColumnWidth(0, 8 * 256);
            sheet.setColumnWidth(1, 28 * 256);
            sheet.setColumnWidth(2, 36 * 256);
            sheet.setColumnWidth(3, 36 * 256);
            sheet.setColumnWidth(4, 20 * 256);

            Styles s = Styles.build(wb);
            addTitleRows(sheet, "Ecuador Comparte — Testimonios",
                    "Exportado el " + LocalDateTime.now().format(FORMATTER), 5, s);
            addHeaderRow(sheet, new String[]{"#", "Nombre", "Instagram", "Facebook", "Fecha"}, 3, s);

            int rowNum = 4;
            for (int i = 0; i < testimonials.size(); i++) {
                Testimonial t = testimonials.get(i);
                Row row = sheet.createRow(rowNum++);
                boolean even = i % 2 == 0;

                row.createCell(0).setCellValue(t.getId()); row.getCell(0).setCellStyle(even ? s.centerEven : s.centerOdd);
                row.createCell(1).setCellValue(t.getName()); row.getCell(1).setCellStyle(even ? s.even : s.odd);
                row.createCell(2).setCellValue(t.getInstagramUrl() != null ? t.getInstagramUrl() : "—"); row.getCell(2).setCellStyle(even ? s.even : s.odd);
                row.createCell(3).setCellValue(t.getFacebookUrl() != null ? t.getFacebookUrl() : "—"); row.getCell(3).setCellStyle(even ? s.even : s.odd);
                String fecha = t.getCreatedAt() != null ? t.getCreatedAt().format(DATE_ONLY) : "—";
                row.createCell(4).setCellValue(fecha); row.getCell(4).setCellStyle(even ? s.centerEven : s.centerOdd);
            }

            addTotalRow(sheet, rowNum, testimonials.size(), "Total testimonios", 5, s);
            wb.write(out);
            return out.toByteArray();
        }
    }


    public byte[] generateNewsReport(List<News> newsList) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Noticias");
            sheet.setColumnWidth(0, 8 * 256);
            sheet.setColumnWidth(1, 40 * 256);
            sheet.setColumnWidth(2, 22 * 256);
            sheet.setColumnWidth(3, 16 * 256);
            sheet.setColumnWidth(4, 20 * 256);

            Styles s = Styles.build(wb);
            addTitleRows(sheet, "Ecuador Comparte — Noticias",
                    "Exportado el " + LocalDateTime.now().format(FORMATTER), 5, s);
            addHeaderRow(sheet, new String[]{"#", "Título", "Autor", "Estado", "Fecha publicación"}, 3, s);

            int rowNum = 4;
            for (int i = 0; i < newsList.size(); i++) {
                News news = newsList.get(i);
                Row row = sheet.createRow(rowNum++);
                boolean even = i % 2 == 0;

                row.createCell(0).setCellValue(news.getId()); row.getCell(0).setCellStyle(even ? s.centerEven : s.centerOdd);
                row.createCell(1).setCellValue(news.getTitle()); row.getCell(1).setCellStyle(even ? s.even : s.odd);
                row.createCell(2).setCellValue(news.getAuthor() != null ? news.getAuthor() : "—"); row.getCell(2).setCellStyle(even ? s.even : s.odd);
                String estado = news.getStatus() != null ? news.getStatus().getVisualName() : "—";
                row.createCell(3).setCellValue(estado); row.getCell(3).setCellStyle(even ? s.centerEven : s.centerOdd);
                String fecha = news.getPublishedAt() != null ? news.getPublishedAt().format(DATE_ONLY) : "—";
                row.createCell(4).setCellValue(fecha); row.getCell(4).setCellStyle(even ? s.centerEven : s.centerOdd);
            }

            addTotalRow(sheet, rowNum, newsList.size(), "Total noticias", 5, s);
            wb.write(out);
            return out.toByteArray();
        }
    }
}
