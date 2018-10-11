package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * pdf在线预览
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年7月18日
 */
public class ViewPdf {
	static Logger logger = LoggerFactory.getLogger(ViewPdf.class);
	@RequestMapping(value = "pdfview.do", method = RequestMethod.GET)
	public void  memberPdfViewer(HttpServletRequest request, HttpServletResponse response,String urlpath) {
		 logger.info("urlpath="+urlpath);
		  try
	        {
	            InputStream fileInputStream =  getFile(urlpath);
	            response.setHeader("Content-Disposition", "attachment;fileName=test.pdf");
	            response.setContentType("multipart/form-data");
	            OutputStream outputStream = response.getOutputStream();
	            IOUtils.write(IOUtils.toByteArray(fileInputStream), outputStream);
	        }
	        catch (Exception e)
	        {
	            System.out.println(e.getMessage());
	        }
	}
	public InputStream getFile(String urlPath) {  
        InputStream inputStream = null;  
        try {  
            try {  
                String strUrl = urlPath.trim();  
                URL url=new URL(strUrl);
                //打开请求连接
                URLConnection connection = url.openConnection();
                HttpURLConnection httpURLConnection=(HttpURLConnection) connection;
                httpURLConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
                // 取得输入流，并使用Reader读取
                inputStream = httpURLConnection.getInputStream();
                return inputStream;  
            } catch (IOException e) {  
                System.out.println(e.getMessage());
                inputStream = null;  
            }  
        } catch (Exception e) {  
            System.out.println(e.getMessage());
            inputStream = null;  
        }  
        return inputStream;  
    }


}
