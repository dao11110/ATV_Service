package com.amkor.common.utils;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Utils {

    public static org.apache.poi.ss.usermodel.Workbook getWorkBook(String filePath) {
        String fileType = filePath.substring(filePath.length() - 4);
        org.apache.poi.ss.usermodel.Workbook workbook;
        try {
            if (fileType.equals("xlsx")) {
                workbook = new XSSFWorkbook(new FileInputStream(filePath));
                return workbook;
            } else if (fileType.equals(".xls")) {
                workbook = new HSSFWorkbook(new FileInputStream(filePath));
                return workbook;
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("-----------getWorkBook-----" + e.toString());
            return null;
        }
    }

    public static org.apache.poi.ss.usermodel.Workbook getWorkBook(FileInputStream fileInputStream, String filePath) {
        String fileType = filePath.substring(filePath.length() - 4);
        org.apache.poi.ss.usermodel.Workbook workbook;
        try {
            if (fileType.equals("xlsx")) {
                workbook = new XSSFWorkbook(fileInputStream);
                return workbook;
            } else if (fileType.equals(".xls")) {
                workbook = new HSSFWorkbook(fileInputStream);
                return workbook;
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("-----------getWorkBook-----" + e.toString());
            return null;
        }
    }

    public static org.apache.poi.ss.usermodel.Workbook getWorkBook(MultipartFile file) {
        if (file.getOriginalFilename() == null) {
            return null;
        }
        String fileType = file.getOriginalFilename().substring(file.getOriginalFilename().length() - 4);
        org.apache.poi.ss.usermodel.Workbook workbook;
        try {
            if (fileType.equals("xlsx")) {
                workbook = new XSSFWorkbook(file.getInputStream());
                return workbook;
            } else if (fileType.equals(".xls")) {
                workbook = new HSSFWorkbook(file.getInputStream());
                return workbook;
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("-----------getWorkBook-----" + e.toString());
            return null;
        }
    }

    public static String getCellStringValue(org.apache.poi.ss.usermodel.Cell cell) {
        String cellValue = null;
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.STRING) {
            cellValue = cell.getStringCellValue().trim();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            cellValue = String.valueOf(Math.round(cell.getNumericCellValue())).trim();
        } else if (cell.getCellType() == CellType.FORMULA) {
            cellValue = cell.getRichStringCellValue().getString().trim();
        }
        return cellValue != null && cellValue.isEmpty() ? null : cellValue;
    }

    public static Integer getCellNumberValue(org.apache.poi.ss.usermodel.Cell cell) {
        Integer cellValue = null;
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            cellValue = (int) Math.round(cell.getNumericCellValue());
        } else if (cell.getCellType() == CellType.STRING) {
            cellValue = Math.round(Integer.parseInt(cell.getStringCellValue().trim()));
        } else if (cell.getCellType() == CellType.FORMULA) {
            cellValue = Math.round(Integer.parseInt(cell.getRichStringCellValue().getString().trim()));
        }
        return cellValue;
    }


    public static List<String> convertStringToListString(String stringList) {
        if (stringList == null || stringList.isEmpty()) {
            return null;
        }
        List<String> result = new ArrayList<>();
        String[] strings = stringList.split(", ");
        if (strings.length == 0) {
            result.add(stringList);
        } else {
            result.addAll(Arrays.asList(strings));
        }
        return result;
    }

    public static LocalDateTime parseDefectReportFileName(String filename) {
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) return null;

        String nameOnly = filename.substring(0, filename.length() - 4); // strip .csv
        String[] parts = nameOnly.split("_");
        if (parts.length < 3) return null;

        String ts = parts[2]; // e.g., 1229152316
        if (ts.length() != 10 || !ts.chars().allMatch(Character::isDigit)) return null;

        try {
            // Build a LocalDateTime with the current year
            DateTimeFormatter fmt = new DateTimeFormatterBuilder()
                    .appendPattern("MMddHHmmss")
                    .parseDefaulting(ChronoField.YEAR, Year.now().getValue())
                    .toFormatter();


            // Handle year boundary: if the parsed time is more than 1 day in the future,
            // assume it belongs to last year (e.g., Dec 31 files processed on Jan 1).
            LocalDateTime fileTime = LocalDateTime.parse(ts, fmt);
            return fileTime;
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }

    }

}
