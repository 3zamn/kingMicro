package com.king.rest.oss;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.king.common.utils.file.IoUtil;
import com.king.common.utils.network.HttpUtils;
import com.king.common.utils.network.ResponseWrap;
import com.king.utils.pattern.XssHttpServletRequestWrapper;

/**
 * 帮朋友抓取热卖店铺
 * @author King chen
 * @emai 396885563@qq.com
 * @data 2018年7月31日
 */
@RestController
public class CrawingController {
	
	/**
	 * 下载文件
	 */
	@GetMapping("/downlod")
	public void code(HttpServletRequest request, HttpServletResponse response) throws IOException{	
		HttpServletRequest orgRequest = XssHttpServletRequestWrapper.getOrgRequest(request);//不进行xss过滤
		
		try {
			String url = orgRequest.getParameter("urlpath");
	        HttpUtils http = HttpUtils.get(url);
	        ResponseWrap r = http.execute(); //执行请求
	        String line=null;      
			PrintWriter pw=null;
			//  String regex= " href=\"/item/.*?\" ";
			String host_new="<div class=\"goods-list new-list fl clearfix\">";//热销、新款爆款
			String search="<i class=\"isRecommd isRecommd_1\"></i>";//搜索商品
	        String regex= " href=\"/item/.*?\" ";
	        String begin=" href=\"";
	        String end="\" ";
	        Pattern pattern=Pattern.compile(regex);
	        BufferedReader br=r.getBufferedReader();    
	        String tempPath=IoUtil.getFile("gen").getPath()+File.separator+"URL.txt";//临时目录
			pw = new PrintWriter(new FileWriter(tempPath),true);
			boolean start=false;
			int i=1;
			while((line=br.readLine())!=null){
				if(start==false){
					if(line.contains(host_new) || line.contains(search)){
						start=true;
					}
				}else{
					Matcher matcher=pattern.matcher(line);
					
					while(matcher.find()){
						String str=matcher.group();
						str=str.replace(begin, "https://www.vvic.com");
						str=str.replace(end, "");
						if(!str.contains("{=item.item_id}")){
							if(i<=80){//只取80条记录
								i=i+1;
								pw.println(str);							
							}				
						}						
					}
				}									
			}       
			start=false;
	        http.shutdown();
	        InputStream inStream = new FileInputStream(tempPath);// 文件的存放路径
	        response.reset();
	        response.addHeader("Content-Disposition", "attachment; filename=\"" + "德哥" + "\"");
	        // 循环取出流中的数据
	        byte[] b = new byte[100];
	        int len;
	        try {
	            while ((len = inStream.read(b)) > 0)
	                response.getOutputStream().write(b, 0, len);
	            inStream.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        pw.close();
	        pw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}	  
	}
}
