package com.king.utils.excel;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

/**
 * 加载Excel解析
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年8月23日
 */
public class ExcelReaderUtil {
    public static void read2007Excel(int offset,IExcelRowReader rowReader, File file) throws
            OpenXML4JException, SAXException, IOException {
        Excel2007Reader excel2007Reader = new Excel2007Reader();
        excel2007Reader.setRowReader(offset,rowReader);
        excel2007Reader.load(file);
    }

}