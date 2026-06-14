package com.srms.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.srms.model.Mark;
import com.srms.model.Result;
import com.srms.model.Student;

import java.awt.Color;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Service for generating PDF result reports using OpenPDF.
 */
public class PdfService {

    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 20, Font.BOLD, new Color(10, 14, 39));
    private static final Font SUBTITLE_FONT = new Font(Font.HELVETICA, 12, Font.NORMAL, new Color(100, 100, 100));
    private static final Font HEADER_FONT = new Font(Font.HELVETICA, 11, Font.BOLD, Color.WHITE);
    private static final Font BODY_FONT = new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(50, 50, 50));
    private static final Font LABEL_FONT = new Font(Font.HELVETICA, 10, Font.BOLD, new Color(50, 50, 50));
    private static final Font GRADE_FONT = new Font(Font.HELVETICA, 28, Font.BOLD, new Color(59, 130, 246));

    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private static final Color HEADER_BG = new Color(10, 14, 39);
    private static final Color STRIPE_COLOR = new Color(240, 245, 255);

    /**
     * Generate a student result PDF and write it to the output stream.
     */
    public void generateResultPdf(Student student, Result result, List<Mark> marks, OutputStream outputStream) {
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // ===== Header =====
            Paragraph title = new Paragraph("Student Result Management System", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Academic Performance Report", SUBTITLE_FONT);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(5);
            document.add(subtitle);

            // Divider line
            PdfPTable divider = new PdfPTable(1);
            divider.setWidthPercentage(100);
            PdfPCell dividerCell = new PdfPCell();
            dividerCell.setBorderWidth(0);
            dividerCell.setBorderWidthBottom(2);
            dividerCell.setBorderColorBottom(PRIMARY_COLOR);
            dividerCell.setFixedHeight(10);
            divider.addCell(dividerCell);
            divider.setSpacingAfter(15);
            document.add(divider);

            // ===== Student Information =====
            PdfPTable infoTable = new PdfPTable(4);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{1.2f, 2f, 1.2f, 2f});
            infoTable.setSpacingAfter(15);

            addInfoRow(infoTable, "Student ID:", String.valueOf(student.getStudentId()),
                       "Name:", student.getName());
            addInfoRow(infoTable, "Email:", student.getEmail(),
                       "Department:", student.getDepartment());
            addInfoRow(infoTable, "Phone:", student.getPhone() != null ? student.getPhone() : "N/A",
                       "Generated:", new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()));

            document.add(infoTable);

            // ===== Marks Table =====
            Paragraph marksTitle = new Paragraph("Subject-wise Marks", new Font(Font.HELVETICA, 14, Font.BOLD, HEADER_BG));
            marksTitle.setSpacingAfter(8);
            document.add(marksTitle);

            PdfPTable marksTable = new PdfPTable(4);
            marksTable.setWidthPercentage(100);
            marksTable.setWidths(new float[]{0.5f, 1.2f, 2f, 1f});

            // Table headers
            addTableHeader(marksTable, "#");
            addTableHeader(marksTable, "Code");
            addTableHeader(marksTable, "Subject");
            addTableHeader(marksTable, "Marks");

            // Table body
            int index = 1;
            for (Mark mark : marks) {
                Color bgColor = (index % 2 == 0) ? STRIPE_COLOR : Color.WHITE;
                addTableCell(marksTable, String.valueOf(index), bgColor);
                addTableCell(marksTable, mark.getSubjectCode(), bgColor);
                addTableCell(marksTable, mark.getSubjectName(), bgColor);
                addTableCell(marksTable, String.format("%.2f", mark.getMarks()), bgColor);
                index++;
            }

            marksTable.setSpacingAfter(15);
            document.add(marksTable);

            // ===== Result Summary =====
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            summaryTable.setWidths(new float[]{3f, 1f});

            // Left side: details
            PdfPTable detailsTable = new PdfPTable(2);
            detailsTable.setWidths(new float[]{1.5f, 2f});
            addSummaryRow(detailsTable, "Total Marks:", String.format("%.2f / %d", result.getTotalMarks(), marks.size() * 100));
            addSummaryRow(detailsTable, "Percentage:", String.format("%.2f%%", result.getPercentage()));
            addSummaryRow(detailsTable, "Grade:", result.getGrade());
            addSummaryRow(detailsTable, "Class Rank:", String.valueOf(result.getRankPosition()));
            addSummaryRow(detailsTable, "Status:", result.getPercentage() >= 60 ? "PASSED" : "FAILED");

            PdfPCell detailsCell = new PdfPCell(detailsTable);
            detailsCell.setBorderWidth(1);
            detailsCell.setBorderColor(new Color(200, 200, 200));
            detailsCell.setPadding(10);
            summaryTable.addCell(detailsCell);

            // Right side: Grade badge
            PdfPCell gradeCell = new PdfPCell();
            gradeCell.setBorderWidth(1);
            gradeCell.setBorderColor(new Color(200, 200, 200));
            gradeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            gradeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            gradeCell.setPadding(15);

            Paragraph gradeBadge = new Paragraph();
            gradeBadge.setAlignment(Element.ALIGN_CENTER);
            gradeBadge.add(new Chunk("GRADE\n", new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(150, 150, 150))));
            gradeBadge.add(new Chunk(result.getGrade(), GRADE_FONT));
            gradeCell.addElement(gradeBadge);
            summaryTable.addCell(gradeCell);

            document.add(summaryTable);

            // ===== Footer =====
            Paragraph footer = new Paragraph(
                    "\nThis is a system-generated report. No signature is required.",
                    new Font(Font.HELVETICA, 8, Font.ITALIC, new Color(150, 150, 150))
            );
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(25);
            document.add(footer);

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        } finally {
            document.close();
        }
    }

    private void addTableHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
        cell.setBackgroundColor(HEADER_BG);
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text, Color bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, BODY_FONT));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorderColor(new Color(220, 220, 220));
        table.addCell(cell);
    }

    private void addInfoRow(PdfPTable table, String label1, String value1, String label2, String value2) {
        PdfPCell l1 = new PdfPCell(new Phrase(label1, LABEL_FONT));
        l1.setBorderWidth(0); l1.setPadding(4);
        PdfPCell v1 = new PdfPCell(new Phrase(value1, BODY_FONT));
        v1.setBorderWidth(0); v1.setPadding(4);
        PdfPCell l2 = new PdfPCell(new Phrase(label2, LABEL_FONT));
        l2.setBorderWidth(0); l2.setPadding(4);
        PdfPCell v2 = new PdfPCell(new Phrase(value2, BODY_FONT));
        v2.setBorderWidth(0); v2.setPadding(4);
        table.addCell(l1); table.addCell(v1);
        table.addCell(l2); table.addCell(v2);
    }

    private void addSummaryRow(PdfPTable table, String label, String value) {
        PdfPCell l = new PdfPCell(new Phrase(label, LABEL_FONT));
        l.setBorderWidth(0); l.setPadding(5);
        PdfPCell v = new PdfPCell(new Phrase(value, BODY_FONT));
        v.setBorderWidth(0); v.setPadding(5);
        table.addCell(l); table.addCell(v);
    }
}
