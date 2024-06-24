package com.trvankiet.app.util;

import org.apache.poi.ss.usermodel.*;

public class ExcelUtil {
    public static boolean isCellBold(CellStyle style, Workbook workbook) {
        if (style != null) {
            Font font = workbook.getFontAt(style.getFontIndex());
            return font.getBold();
        }
        return false;
    }
    public static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return Double.toString(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            case FORMULA:
                return getFormulaValue(cell, evaluator);
            default:
                return "";
        }
    }

    private static String getFormulaValue(Cell cell, FormulaEvaluator evaluator) {
        CellValue cellValue = evaluator.evaluate(cell);
        switch (cellValue.getCellType()) {
            case STRING:
                return cellValue.getStringValue();
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return Double.toString(cellValue.getNumberValue());
                }
            case BOOLEAN:
                return Boolean.toString(cellValue.getBooleanValue());
            case ERROR:
                return FormulaError.forInt(cellValue.getErrorValue()).getString();
            default:
                return "";
        }
    }

}
