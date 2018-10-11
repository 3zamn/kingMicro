package test;  
   
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
 * @date 2018年7月18日
 */
public class DocConverter {  
	
	static Logger logger = LoggerFactory.getLogger(DocConverter.class);
	 private List<Process> process = new ArrayList<Process>();
	    public BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();//这里用线程安全的queue管理运行的端口号
	    private final String OpenOffice_HOME = null;

	 public void startAllService() throws IOException, NumberFormatException, InterruptedException{
	    	
	    	String portsStr = null;//我将使用的端口号卸载properties文件中，便于写改
	    	
	    	String[] ports = portsStr.split(",");
			for (String port : ports) {
				//添加到队列 用于线程获取端口 进行连接
				queue.put(Integer.parseInt(port));
				//启动OpenOffice的服务  
		        String command = OpenOffice_HOME  
		                + "/program/soffice.exe -headless -accept=\"socket,host=127.0.0.1,port="+port+";urp;\"";//这里根据port进行进程开启
		        process.add(Runtime.getRuntime().exec(command));
		        logger.debug("[startAllService-port-["+port+"]-success]");
			}
			logger.debug("[startAllService-success]");
	 }
	/**
	 * 文档转pdf
	 * @param srcPath 源路径
	 * @param SavePath //保存路径
	 */
	public static void DcoConvertPdf(String srcPath, String savePath) {

		File inputFile = new File(srcPath);
		if (!inputFile.exists()) {
			System.out.println("源文件不存在！");
			return;
		}
		// 输出文件目录
		File outputFile = new File(savePath);
		if (!outputFile.getParentFile().exists()) {
			outputFile.getParentFile().exists();
		}
		// 连接OpenOffice/LibreOffice服务
		OfficeManager officeManager = LocalOfficeManager.builder().officeHome("C:\\Program Files\\LibreOffice").install().build();
		try {
			System.out.println("状态:"+officeManager.isRunning());
			if(!officeManager.isRunning()){
				officeManager.start();
			}	
			// 转换文档到pdf
			long time = System.currentTimeMillis();
			JodConverter.convert(inputFile).to(outputFile).execute();
			logger.info("文件：{}转换PDF：{}完成，用时{}毫秒！", srcPath, savePath, System.currentTimeMillis() - time);
		} catch (OfficeException e) {
			e.printStackTrace();
			logger.warn("文件：{}转换PDF：{}失败！", srcPath, savePath);
		} finally {
			// 关闭连接
		//	OfficeUtils.stopQuietly(officeManager);
		}

	}
	public static void main(String[] args) {
		String srcPath="C:\\Users\\Administrator\\Desktop\\集团汇总餐饮系统功能-需求说明规格书（20180522）.doc";
		String srcPath1="C:\\Users\\Administrator\\Downloads\\工作模板++%2815%29.pptx";
		String savePath="D:\\pdftest1\\20180122165620.pdf";
		String savePath1="D:\\pdftest1\\201801221656200.pdf";
		  // 源文件目录
	//	DcoConvertPdf(srcPath, savePath);
		DcoConvertPdf(srcPath1, savePath1);
	}
  
}  