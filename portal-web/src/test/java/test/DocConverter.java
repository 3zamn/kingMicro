package test;  
   
import java.io.File;

import org.jodconverter.JodConverter;
import org.jodconverter.office.LocalOfficeManager;
import org.jodconverter.office.OfficeException;
import org.jodconverter.office.OfficeManager;
import org.jodconverter.office.OfficeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * office、png转pdf
 * @author King chen
 * @emai 396885563@qq.com
 * @data 2018年7月18日
 */
public class DocConverter {  
	static Logger logger = LoggerFactory.getLogger(DocConverter.class);
	public static void main(String[] args) {
	//	String srcPath="C:\\Users\\Administrator\\Downloads\\工作模板++%2815%29.pptx";
		String srcPath="D:\\pdftest1\\test0.png";
		String desPath="D:\\pdftest1\\20180122165620.pdf";
		  // 源文件目录
	    File inputFile = new File(srcPath);
	    if (!inputFile.exists()) {
	        System.out.println("源文件不存在！");
	        return;
	    }
	    // 输出文件目录
	    File outputFile = new File(desPath);
	    if (!outputFile.getParentFile().exists()) {
	        outputFile.getParentFile().exists();
	    }
	    // 连接OpenOffice/LibreOffice服务
	    OfficeManager officeManager = LocalOfficeManager.builder().officeHome("C:\\Program Files\\LibreOffice").install().build();
	    try {
	        officeManager.start();
	        // 转换文档到pdf
	        long time = System.currentTimeMillis();
	        JodConverter.convert(inputFile).to(outputFile).execute();
	        logger.info("文件：{}转换PDF：{}完成，用时{}毫秒！", srcPath, desPath, System.currentTimeMillis() - time);
	    } catch (OfficeException e) {
	        e.printStackTrace();
	        logger.warn("文件：{}转换PDF：{}失败！", srcPath, desPath);
	    } finally {
	        // 关闭连接
	        OfficeUtils.stopQuietly(officeManager);
	    }
	}
  
}  