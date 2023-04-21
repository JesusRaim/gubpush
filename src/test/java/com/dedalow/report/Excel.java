package com.dedalow.report;

import com.dedalow.SharedDependencies;

import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import com.dedalow.utils.*;

public class Excel {

    public static HSSFWorkbook createExcel(File route, String[] columnsExcel) {
        HSSFWorkbook wk = null;
        try {
            File file = new File(route + SharedDependencies.fileSystem.getSeparator() + "ReportResult.xls");
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                wk = (HSSFWorkbook) WorkbookFactory.create(fis);
                if (fis != null) {
                    fis.close();
                }
            } else {
                wk = new HSSFWorkbook();
                HSSFSheet sheet = wk.createSheet(SharedDependencies.suiteName);
                createHeaderExcel(wk, sheet, columnsExcel);
            }
        } catch (Exception e) {
            SharedDependencies.logger.severe(e.getMessage());
        }
        return wk;
    }

    public static void createHeaderExcel(HSSFWorkbook wk, HSSFSheet sheet, String[] columnsExcel) {
        Font fontHeader = wk.createFont();
        fontHeader.setBold(true);
        fontHeader.setFontHeightInPoints((short) 11);
        CellStyle styleHeader = wk.createCellStyle();
        styleHeader.setFont(fontHeader);
        Row header = sheet.createRow(0);
        for (int i = 0; i < columnsExcel.length; i++) {
            Cell cellHeader = header.createCell(i);
            cellHeader.setCellValue(columnsExcel[i]);
            cellHeader.setCellStyle(styleHeader);
            sheet.autoSizeColumn(i);
        }
    }

    public static CellStyle changeColor(String result, HSSFWorkbook wk) {
        CellStyle styleResult = wk.createCellStyle();
        styleResult.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        switch (result) {
        case "OK":
            styleResult.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
            return styleResult;
        case "KO":
            styleResult.setFillForegroundColor(IndexedColors.CORAL.getIndex());
            return styleResult;
        default:
            styleResult.setFillForegroundColor(IndexedColors.AQUA.getIndex());
            return styleResult;
        }
    }
}