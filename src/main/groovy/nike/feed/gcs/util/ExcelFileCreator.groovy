/**
 * 
 */
package nike.feed.gcs.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component;

/**
 * @author kaarthikeyan.palani
 *
 */
public class ExcelFileCreator {

    Workbook workBook;

    public ExcelFileCreator(String fileName){

        FileInputStream fileIpStream = null;

        try {
            fileIpStream = new FileInputStream(new File(fileName));
            this.workBook = new HSSFWorkbook(fileIpStream);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (fileIpStream != null) {
                try {
                    fileIpStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This method is used to get the workbook sheet
     * 
     * @param sheetName
     * @return
     */

    public Sheet getSheet(String sheetName) {
        Sheet sheet = workBook.getSheet(sheetName)
        return sheet
    }

    /*
     * Create a row and populate the content. If it is a header row, name the
     * cell content as bold otherwise it should be normal font.
     */
    private void populateRowContent(Row row, boolean isHeader, String... cellContents) {
        int cellCount = 0
        cellContents.each{cellContent ->
            Cell cell = row.createCell(cellCount)
            cell.setCellValue(cellContent)
            cellCount++
        }
    }

    /**
     * This method can be use to populate row content in workbook sheet
     * 
     * @param sheet
     * @param rowCount
     * @param rowContent
     */
    public void createRow(Sheet sheet, int rowCount, String... rowContent) {
        if (sheet != null && rowContent != null && rowContent.length > 0) {
            Row row = sheet.createRow(rowCount)
            populateRowContent(row, Boolean.FALSE, rowContent)
        }
    }

    /**
     * This method can be use to autosize the column
     * 
     * @param sheet
     * 
     */
    public void autoSizeColumn(Sheet sheet) {
        // set auto size for the columns
        int colSize = sheet.getRow(0).getLastCellNum()
        colSize.times { sheet.autoSizeColumn(it) }


    }

    /**
     * This method creates a byte array from WorkBook content.
     * 
     * @param fileLocation
     * @throws IOException
     */
    public byte[] createExcelFile() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workBook.write(byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        return bytes;
    }
}
