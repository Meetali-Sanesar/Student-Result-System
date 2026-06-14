package com.srms.service;

import com.srms.model.Result;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.OutputStream;
import java.util.List;

/**
 * Service for exporting results to Excel (.xlsx) using Apache POI.
 */
public class ExcelService {

    /**
     * Export all results to an Excel file.
     */
    public void exportResults(List<Result> results, OutputStream outputStream) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Student Results");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Data style
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.CENTER);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Number style
            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.cloneStyleFrom(dataStyle);
            DataFormat format = workbook.createDataFormat();
            numberStyle.setDataFormat(format.getFormat("0.00"));

            // Pass style (green background)
            CellStyle passStyle = workbook.createCellStyle();
            passStyle.cloneStyleFrom(dataStyle);
            passStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            passStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Fail style (red background)
            CellStyle failStyle = workbook.createCellStyle();
            failStyle.cloneStyleFrom(dataStyle);
            failStyle.setFillForegroundColor(IndexedColors.CORAL.getIndex());
            failStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Create header row
            String[] headers = {"Rank", "Student ID", "Student Name", "Department",
                                "Total Marks", "Percentage (%)", "Grade", "Status"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create data rows
            int rowNum = 1;
            for (Result result : results) {
                Row row = sheet.createRow(rowNum++);
                boolean passed = result.getPercentage() >= 60;
                CellStyle statusStyle = passed ? passStyle : failStyle;

                Cell c0 = row.createCell(0); c0.setCellValue(result.getRankPosition()); c0.setCellStyle(dataStyle);
                Cell c1 = row.createCell(1); c1.setCellValue(result.getStudentId()); c1.setCellStyle(dataStyle);
                Cell c2 = row.createCell(2); c2.setCellValue(result.getStudentName()); c2.setCellStyle(dataStyle);
                Cell c3 = row.createCell(3); c3.setCellValue(result.getDepartment()); c3.setCellStyle(dataStyle);
                Cell c4 = row.createCell(4); c4.setCellValue(result.getTotalMarks()); c4.setCellStyle(numberStyle);
                Cell c5 = row.createCell(5); c5.setCellValue(result.getPercentage()); c5.setCellStyle(numberStyle);
                Cell c6 = row.createCell(6); c6.setCellValue(result.getGrade()); c6.setCellStyle(dataStyle);
                Cell c7 = row.createCell(7); c7.setCellValue(passed ? "PASSED" : "FAILED"); c7.setCellStyle(statusStyle);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // Add a bit of extra width
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
            }

            // Freeze header row
            sheet.createFreezePane(0, 1);

            workbook.write(outputStream);
        } catch (Exception e) {
            throw new RuntimeException("Error generating Excel file: " + e.getMessage(), e);
        }
    }
}
