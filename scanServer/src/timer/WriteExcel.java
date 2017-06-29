package timer;
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;  
import org.apache.poi.ss.usermodel.Cell;  
import org.apache.poi.ss.usermodel.Row;  
import org.apache.poi.ss.usermodel.Sheet;  
import org.apache.poi.ss.usermodel.Workbook;  
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.ExceptionUtils;  
public class WriteExcel {  
	
	final static Logger logger = LoggerFactory.getLogger("TIMER");
    private static final String EXCEL_XLS = "xls";  
    private static final String EXCEL_XLSX = "xlsx";  
  
    public static void writeExcel(List<Integer> lists){  
    	
    	logger.debug("开始写入Excel");
    	StringBuffer sBuffer = new StringBuffer();
    	for (Integer integer : lists) {
    		sBuffer.append(integer+",");
		}
    	
        OutputStream out = null;  
        //"excel\\NoSendId.xls"
        String finalXlsxPath = "excel\\NoSendId.xls";
        try {  
            // 获取总列数  
            // 读取Excel文档  
            File finalXlsxFile = new File(finalXlsxPath);  
            Workbook workBook = getWorkbok(finalXlsxFile);  
            // sheet 对应一个工作页  
            Sheet sheet = workBook.getSheetAt(0);  
            /** 
             * 删除原有数据，除了属性列 
             */  
            int rowNumber = sheet.getLastRowNum();  // 第一行从0开始算  
            for (int i = 1; i <= rowNumber; i++) {  
                Row row = sheet.getRow(i);  
                sheet.removeRow(row);  
            }  
            // 创建文件输出流，输出电子表格：这个必须有，否则你在sheet上做的任何操作都不会有效  
            out =  new FileOutputStream(finalXlsxPath);  
            workBook.write(out);  
            /** 
             * 往Excel中写新数据 
             */  
            // 创建一行：从第二行开始，跳过属性列  
            Row row = sheet.createRow(0);  
            // 得到要插入的每一条记录  
            Cell first = row.createCell(0);  
            first.setCellValue("id");  
            Cell second = row.createCell(1);  
            second.setCellValue(sBuffer.toString());  
            // 创建文件输出流，准备输出电子表格：这个必须有，否则你在sheet上做的任何操作都不会有效  
            out =  new FileOutputStream(finalXlsxPath);  
            workBook.write(out);  
        } catch (Exception e) { 
        	logger.error("写入数据失败");
        	logger.error(ExceptionUtils.getTrace(e));
        } finally{  
            try {  
                if(out != null){  
                    out.flush();  
                    out.close();  
                }  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        logger.debug("写入Excel，数据导出成功");
        
        //发送邮件
        try {
        	logger.debug("开始发送邮件");
			SendAttachment.Send();
		} catch (Exception e) {
			logger.error("发送邮件失败");
			logger.error(ExceptionUtils.getTrace(e));
		}
    }  
  
    /** 
     * 判断Excel的版本,获取Workbook 
     * @param in 
     * @param filename 
     * @return 
     * @throws IOException 
     */  
    public static Workbook getWorkbok(File file) throws IOException{  
        Workbook wb = null;  
        FileInputStream in = new FileInputStream(file);  
        if(file.getName().endsWith(EXCEL_XLS)){  //Excel 2003  
            wb = new HSSFWorkbook(in);  
        }else if(file.getName().endsWith(EXCEL_XLSX)){  // Excel 2007/2010  
            wb = new XSSFWorkbook(in);  
        }  
        return wb;  
    }  
    
}  