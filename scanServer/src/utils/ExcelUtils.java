package utils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @描述：测试excel读取 导入的jar包
 * <p>
 * poi-3.8-beta3-20110606.jar
 * <p>
 * poi-ooxml-3.8-beta3-20110606.jar
 * <p>
 * poi-examples-3.8-beta3-20110606.jar
 * <p>
 * poi-excelant-3.8-beta3-20110606.jar
 * <p>
 * poi-ooxml-schemas-3.8-beta3-20110606.jar
 * <p>
 * poi-scratchpad-3.8-beta3-20110606.jar
 * <p>
 * xmlbeans-2.3.0.jar
 * <p>
 * dom4j-1.6.1.jar
 * <p>
 * jar包官网下载地址：http://poi.apache.org/download.html
 * <p>
 * 下载poi-bin-3.8-beta3-20110606.zipp
 */

public class ExcelUtils {
    private static int totalRows = 0;// 总行数
    private static int totalCells = 0;// 总列数
    static final Logger logger = LoggerFactory.getLogger("SCAN");

    private int getTotalRows() {
        return totalRows;
    }

    private static int getTotalCells() {
        return totalCells;
    }

    /**
     * 获取端口号集合
     * @return
     */
    public static List<Integer> getPortList(){
        List<Integer> portList = new ArrayList<>();
        List<List<String>> list = read("ports.xlsx");
        System.out.println(list);
        if (list != null) {
            for (int i = 2; i < list.size(); i++) {
                List<String> cellList = list.get(i);
                String portStr = cellList.get(1);
                int port = (int) Double.parseDouble(portStr);
                portList.add(port);
            }
            return portList;
        }
        return null;
    }
    /**
     * 获取ip地址
     * @return
     */
    public static String getAddress(){

        List<List<String>> list = read("ports.xlsx");
        if (list != null) {
            List<String> cellList = list.get(0);
            String portStr = cellList.get(1);
            return portStr;
        }
        return null;
    }

    /**
     * 检查文件名是否为空或者是否是Excel格式的文件
     */
    private static boolean validateExcel(String filePath) {

        if (filePath == null || !(isExcel2003(filePath) || isExcel2007(filePath))) {
            logger.warn("文件名不是excel格式");
            return false;
        }
        /** 检查文件是否存在 */
        File file = new File(filePath);
        if (file == null || !file.exists()) {
            logger.warn("文件不存在");
            return false;
        }
        return true;
    }

    /**
     * 获取 Excel 内容
     *
     * @param filePath
     * @return
     */
    private static List<List<String>> read(String filePath) {
        List<List<String>> dataList = new ArrayList<List<String>>();
        InputStream is = null;
        try {
            //验证文件是否合法
            if (!validateExcel(filePath)) {
                return null;
            }
            //判断文件的类型，是2003还是2007
            boolean isExcel2003 = true;
            if (isExcel2007(filePath)) {
                isExcel2003 = false;
            }
            //调用本类提供的根据流读取的方法
            is = new FileInputStream(new File(filePath));
            dataList = read(is, isExcel2003);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getTrace(e));
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    is = null;
                    logger.error(ExceptionUtils.getTrace(e));
                }
            }
        }
        return dataList;
    }

    /**
     * 创建Workbook
     *
     * @param inputStream
     * @param isExcel2003
     * @return
     */
    private static List<List<String>> read(InputStream inputStream, boolean isExcel2003) {
        List<List<String>> dataList = null;
        try {
            //根据版本选择创建Workbook的方式
            Workbook wb = null;
            if (isExcel2003) {
                wb = new HSSFWorkbook(inputStream);
            } else {
                wb = new XSSFWorkbook(inputStream);
            }
            dataList = read(wb);
        } catch (IOException e) {
            logger.error(ExceptionUtils.getTrace(e));
        }
        return dataList;
    }


    private static List<List<String>> read(Workbook wb) {
        List<List<String>> dataLst = new ArrayList<List<String>>();
        // 得到第一个shell
        Sheet sheet = wb.getSheetAt(0);
        // 得到Excel的行数
        totalRows = sheet.getPhysicalNumberOfRows();
        // 得到Excel的列数
        if (totalRows >= 1 && sheet.getRow(0) != null) {
            totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        }
        // 循环Excel的行
        for (int r = 0; r < totalRows; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            List<String> rowList = new ArrayList<String>();
            // 循环Excel的列
            for (int c = 0; c < getTotalCells(); c++) {
                Cell cell = row.getCell(c);
                String cellValue = "";
                if (null != cell) {
                    // 以下是判断数据的类型
                    switch (cell.getCellType()) {
                        case HSSFCell.CELL_TYPE_NUMERIC: // 数字
                            cellValue = cell.getNumericCellValue() + "";
                            break;
                        case HSSFCell.CELL_TYPE_STRING: // 字符串
                            cellValue = cell.getStringCellValue();
                            break;
                        case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
                            cellValue = cell.getBooleanCellValue() + "";
                            break;
                        case HSSFCell.CELL_TYPE_FORMULA: // 公式
                            cellValue = cell.getCellFormula() + "";
                            break;
                        case HSSFCell.CELL_TYPE_BLANK: // 空值
                            cellValue = "";
                            break;
                        case HSSFCell.CELL_TYPE_ERROR: // 故障
                            cellValue = "非法字符";
                            break;
                        default:
                            cellValue = "未知类型";
                            break;
                    }
                }
                rowList.add(cellValue);
            }
            // 保存第r行的第c列
            dataLst.add(rowList);
        }
        return dataLst;
    }

    private static boolean isExcel2003(String filePath) {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    private static boolean isExcel2007(String filePath) {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }
}
