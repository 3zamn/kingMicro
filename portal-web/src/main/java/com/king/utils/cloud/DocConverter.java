package com.king.utils.cloud;  
   
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	
	/**
	 * 文档转pdf
	 * @param srcPath 源路径
	 * @param SavePath //保存路径
	 */
	public static boolean docConvertPdf(File inputFile,String savePath) {
		Boolean result=false;
		if (!inputFile.exists()) {
			System.out.println("源文件不存在！");
			return result;
		}
		// 输出文件目录
		File outputFile = new File(savePath);
		// 连接OpenOffice/LibreOffice服务
		OfficeManager officeManager = LocalOfficeManager.builder().officeHome("C:\\Program Files\\LibreOffice").install().build();
		try {
			if(!officeManager.isRunning()){
				officeManager.start();
			}	
			// 转换文档到pdf
			long time = System.currentTimeMillis();
			JodConverter.convert(inputFile).to(outputFile).execute();
			result=true;
			logger.info("文件：{}转换PDF：{}完成，用时{}毫秒！", inputFile.getName(), savePath, System.currentTimeMillis() - time);
			return result;
		} catch (OfficeException e) {
			e.printStackTrace();
			logger.warn("文件：{}转换PDF：{}失败！", inputFile.getName(), savePath);
			return result;
		} finally {//异步关闭进程
			// 关闭连接
		//	OfficeUtils.stopQuietly(officeManager);
			ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);	
			StopSoffice stopSoffice=new StopSoffice(officeManager);
			fixedThreadPool.execute(stopSoffice);
		}

	}
	
	private static class StopSoffice implements Runnable{
		private OfficeManager officeManager;

		public StopSoffice(OfficeManager officeManager) {
			this.officeManager=officeManager;
		}

		@Override
		public void run() {
			OfficeUtils.stopQuietly(officeManager);
		}
		
	}

  
}  