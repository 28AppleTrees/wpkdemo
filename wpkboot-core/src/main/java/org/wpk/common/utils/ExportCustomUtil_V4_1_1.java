package org.wpk.common.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.util.Base64Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * 基于 org.apache.poi 的自定义导出工具类
 * todo poi 4.x版本未测试
 */
public class ExportCustomUtil_V4_1_1 {

    /**
     * 垂直居中
     * 水平居中
     *
     * @param cellStyle
     */
    public static void allCenter(CellStyle cellStyle) {
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    }

    /**
     * 注意!!! 使用该方法, 必须使合并开始的单元格, 即 (firstRow, firstCol) 单元格!=null && style和value不为null, 使用在 {@link #createCell} 之后
     * 注意!!! 使用该方法, 必须使合并开始的单元格, 即 (firstRow, firstCol) 单元格!=null && style和value不为null, 使用在 {@link #createCell} 之后
     * 注意!!! 使用该方法, 必须使合并开始的单元格, 即 (firstRow, firstCol) 单元格!=null && style和value不为null, 使用在 {@link #createCell} 之后
     * <p>
     * 嵌套for循环进行createCell原因:
     * 合并单元格时, 会导致参与合并的单元格样式丢失, 因为没有创建新的单元格设置style
     * <p>
     * 合并单元格
     * 取值从 0 开始
     * 取值都为开区间, 例: (sheet, 0, 1, 0, 1) 合并4个单元格
     * 横向合并, row 相同
     * 纵向合并, col 相同
     *
     * @param sheet    要操作的sheet对象
     * @param firstRow
     * @param lastRow
     * @param firstCol
     * @param lastCol
     */
    public static void merge(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        if (firstRow > lastRow) {
            throw new RuntimeException("开始行数不能大于结束行数");
        }
        if (firstCol > lastCol) {
            throw new RuntimeException("开始列数不能大于结束列数");
        }
        Row row = sheet.getRow(firstRow);
        Cell cell = row.getCell(firstCol);
        CellStyle cellStyle = cell.getCellStyle();
        String cellValue = cell.getStringCellValue();
        if (firstRow == lastRow) {
            if (firstCol != lastCol) {
                for (int j = 0; j <= lastCol - firstCol; j++) {
                    createCell(sheet.getRow(firstRow), firstCol + j, cellStyle, cellValue);
                }
            }
        } else {
            for (int i = 0; i <= lastRow - firstRow; i++) {
                if (firstCol != lastCol) {
                    for (int j = 0; j <= lastCol - firstCol; j++) {
                        Row tempRow = sheet.getRow(firstRow + i);
//                        未确认多次createRow是否会覆盖数据, 不做创建
//                        if (tempRow == null) {
//                            tempRow = sheet.createRow(firstRow + i);
//                        }
                        createCell(tempRow, firstCol + j, cellStyle, cellValue);
                    }
                }
            }
        }
        CellRangeAddress cellRangeAddress = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        sheet.addMergedRegion(cellRangeAddress);
    }

    /**
     * 常用的四周全细线边框
     *
     * @param cellStyle
     */
    public static void fullBorder(CellStyle cellStyle) {
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
    }

    /**
     * poi对中文字符支持有bug, 使用中文会导致列宽不准确
     * 乘256是因为列宽的单位是 1/256 字符的宽度
     *
     * @param width
     */
    public static int chineseColumnWidth(int width) {
        return (int) (width * 256 + 0.78 * 256);
    }

    /**
     * @param workbook  创建的字体依赖于 Workbook 对象
     * @param fontName
     * @param fontSize  字体大小, 从打开的excel中复制就可以
     * @param fontBold  字体粗细, 有默认值{@link Font# 4.x过期了}
     * @param fontColor 字体颜色, 有默认值{@link Font#COLOR_NORMAL}
     * @param underline 下划线, 有默认值{@link Font#U_NONE}
     * @return
     */
    public static Font createFont(Workbook workbook, String fontName, int fontSize, boolean fontBold, short fontColor, byte underline) {
        Font font = workbook.createFont();
        font.setFontName(fontName);
        font.setFontHeightInPoints((short) fontSize);
        font.setBold(fontBold);
        font.setColor(fontColor);
        font.setUnderline(underline);
        return font;
    }

    public static Font createFont(Workbook workbook, String fontName, int fontSize, boolean fontBold, short fontColor) {
        Font font = workbook.createFont();
        font.setFontName(fontName);
        font.setFontHeightInPoints((short) fontSize);
        font.setBold(fontBold);
        font.setColor(fontColor);
        return font;
    }

    public static Font createFont(Workbook workbook, String fontName, int fontSize, boolean fontBold) {
        Font font = workbook.createFont();
        font.setFontName(fontName);
        font.setFontHeightInPoints((short) fontSize);
        font.setBold(fontBold);
        return font;
    }

    /**
     * 创建一个较为完整的单元格
     *
     * @param row         单元格所属row对象
     * @param columnIndex 单元格索引
     * @param cellStyle   单元格样式
     * @param cellValue   单元格值
     * @return
     */
    public static Cell createCell(Row row, int columnIndex, CellStyle cellStyle, String cellValue) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(cellValue);
        return cell;
    }

    public static Cell createCell(Row row, int columnIndex, CellStyle cellStyle) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellStyle(cellStyle);
        return cell;
    }

    /**
     * 没搞懂位置怎么确定的, 先按照这套实验的逻辑来
     *
     * @param workbook     用于添加图片并获取索引
     * @param sheet        用于获取绘制对象
     * @param base64String base64图片
     * @param dx1
     * @param dy1
     * @param dx2
     * @param dy2
     * @param col1
     * @param row1
     * @param col2
     * @param row2
     */
    public static void addImage(Workbook workbook, Sheet sheet, String base64String,
                                int dx1, int dy1, int dx2, int dy2,
                                int col1, int row1, int col2, int row2) {
        Drawing<?> drawingPatriarch = sheet.createDrawingPatriarch();
        String[] split = base64String.split(";");
        String base64 = split[1].substring(7);
        byte[] imageData = Base64Utils.decodeFromString(base64);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageData);
        BufferedImage image = null;
        try {
            image = ImageIO.read(byteArrayInputStream);
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            double imageRate = 1.0 * imageWidth / imageHeight;
            int pictureIndex = workbook.addPicture(imageData, Workbook.PICTURE_TYPE_JPEG);
            ClientAnchor anchor = drawingPatriarch.createAnchor(dx1 * 10000, dy1 * 10000,
                    (int) ((dx1 + imageWidth) * 10000 * imageRate / 2), (int) ((dy1 + imageHeight) * 10000 * imageRate / 2),
                    col1, row1, col2, row2);
            drawingPatriarch.createPicture(anchor, pictureIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
