package test.excel;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

public class ExcelReaderUtil {
    public static void read2007Excel(IExcelRowReader rowReader, File file) throws
            OpenXML4JException, SAXException, IOException {
        Excel2007Reader excel2007Reader = new Excel2007Reader();
        excel2007Reader.setRowReader(rowReader);
        excel2007Reader.load(file);
    }

}