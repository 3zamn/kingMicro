package com.king.common.utils.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年4月20日
 */
public class IoUtil {

  public static byte[] readInputStream(InputStream inputStream, String inputStreamName) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    byte[] buffer = new byte[16*1024];
    try {
      int bytesRead = inputStream.read(buffer);
      while (bytesRead!=-1) {
        outputStream.write(buffer, 0, bytesRead);
        bytesRead = inputStream.read(buffer);
      }
    } catch (Exception e) {
      throw new RuntimeException("couldn't read input stream "+inputStreamName, e);
    }
    return outputStream.toByteArray();
  }
  
  public static String readFileAsString(String filePath) {
    byte[] buffer = new byte[(int) getFile(filePath).length()];
    BufferedInputStream inputStream = null;
    try {
      inputStream = new BufferedInputStream(new FileInputStream(getFile(filePath)));
      inputStream.read(buffer);
    } catch(Exception e) {
      throw new RuntimeException("Couldn't read file " + filePath + ": " + e.getMessage());
    } finally {
      IoUtil.closeSilently(inputStream);
    }
    return new String(buffer);
  }
  
  public static File getFile(String filePath) {
    URL url = IoUtil.class.getClassLoader().getResource(filePath);
    try {
      return new File(url.toURI());
    } catch (Exception e) {
    	e.printStackTrace();
      throw new RuntimeException("Couldn't get file " + filePath + ": " + e.getMessage());
    }
  }
  
  public static void writeStringToFile(String content, String filePath) {
    BufferedOutputStream outputStream = null;
    try {
      outputStream = new BufferedOutputStream(new FileOutputStream(getFile(filePath)));
      outputStream.write(content.getBytes());
      outputStream.flush();
    } catch(Exception e) {
      throw new RuntimeException("Couldn't write file " + filePath, e);
    } finally {
      IoUtil.closeSilently(outputStream);
    }
  }
  
  /**
   * 将字节数组写入文件
   * 
   * @param content
   * @param filePath
 * @throws IOException 
   */
  public static void writeByteToFile(byte[] content, String filePath) throws IOException {
	    BufferedOutputStream outputStream = null;
	    try {
	      outputStream = new BufferedOutputStream(new FileOutputStream(filePath));
	      outputStream.write(content);
	      outputStream.flush();
	    }  finally {
	      IoUtil.closeSilently(outputStream);
	    }
	  }
  
  /**
   * 从文件读取字节数组
   * 
   * @param content
   * @param filePath
 * @throws IOException 
   */
  public static byte[] readByteFromFile(String filePath) throws IOException {
	    BufferedInputStream inputStream = null;
	    ByteArrayOutputStream byteOut = null;
	    try {
	    	inputStream = new BufferedInputStream(new FileInputStream(filePath));
	    	byteOut = new ByteArrayOutputStream();
	    	byte[] buf = new byte[1024];
            int count=0;  
            while ((count = inputStream.read(buf)) != -1) {  
            	byteOut.write(buf, 0, count);  
            }
            return byteOut.toByteArray();
	    }  finally {
	      IoUtil.closeSilently(inputStream);
	      IoUtil.closeSilently(byteOut);
	    }
	  }
  
  /**
   * Closes the given stream. The same as calling {@link InputStream#close()}, but
   * errors while closing are silently ignored.
   */
  public static void closeSilently(InputStream inputStream) {
    try {
      if(inputStream != null) {
        inputStream.close();
      }
    } catch(IOException ignore) {
      // Exception is silently ignored
    }
  }

  /**
   * Closes the given stream. The same as calling {@link OutputStream#close()}, but
   * errors while closing are silently ignored.
   */
  public static void closeSilently(OutputStream outputStream) {
    try {
      if(outputStream != null) {
        outputStream.close();
      }
    } catch(IOException ignore) {
      // Exception is silently ignored
    }
  }
  
	/**
	 * 获取文件流
	 * @param urlPath
	 * @return
	 */
	public static InputStream getFileStream(String urlPath) {
		InputStream inputStream = null;
		try {
			try {
				String strUrl = urlPath.trim();
				URL url = new URL(strUrl);
				// 打开请求连接
				URLConnection connection = url.openConnection();
				HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
				httpURLConnection.setRequestProperty("User-Agent",
						"Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
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
