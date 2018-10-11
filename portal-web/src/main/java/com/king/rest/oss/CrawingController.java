package com.king.rest.oss;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.king.common.utils.file.IoUtil;
import com.king.common.utils.network.HttpUtils;
import com.king.common.utils.network.ResponseWrap;
import com.king.utils.XssHttpServletRequestWrapper;

/**
 * 帮朋友抓取热卖店铺
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年7月31日
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
			String queryString=orgRequest.getQueryString();
			String url = queryString.replace("urlpath=", "");
	        HttpUtils http = HttpUtils.get(url);
	        ResponseWrap r = http.execute(); //执行请求
	    	PrintWriter pw=null;
	    	String tempPath=IoUtil.getFile("gen").getPath()+File.separator+"URL.txt";//临时目录
	        if(r.getContentType().toString().contains("application/json")){
	        	BufferedReader br=r.getBufferedReader();    
	        	JSONObject j1= JSONObject.parseObject(br.readLine());
	        	JSONObject j2=JSONObject.parseObject(j1.getString("data"));
	        	JSONObject j3=JSONObject.parseObject(j2.getString("search_page"));
	        	JSONArray array = JSONArray.parseArray(j3.getString("recordList"));
	        	pw = new PrintWriter(new FileWriter(tempPath),true);
	        	 for (@SuppressWarnings("rawtypes")
				Iterator iterator = array.iterator(); iterator.hasNext();) { 
	                 JSONObject jsonObject = (JSONObject) iterator.next();                 
	                 String item= "https://www.vvic.com/item/"+ jsonObject.getIntValue("item_id");
	                 pw.println(item);			
	        	 } 
	        }else{
	        	 String line=null;      
	 		
	 			//  String regex= " href=\"/item/.*?\" ";
	 			String host_new="<div class=\"goods-list new-list fl clearfix\">";//热销、新款爆款
	 			String search="<i class=\"isRecommd isRecommd_1\"></i>";//搜索商品
	 	        String regex= " href=\"/item/.*?\" ";
	 	        String begin=" href=\"";
	 	        String end="\" ";
	 	        Pattern pattern=Pattern.compile(regex);
	 	        BufferedReader br=r.getBufferedReader();    
	 	      
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
	        }
	       
	        http.shutdown();
	        InputStream inStream = new FileInputStream(tempPath);// 文件的存放路径
	        StringBuffer filename = new StringBuffer("download");
			filename.append(".txt");
	        response.reset();
	        response.setHeader("Content-Disposition", "attachment; filename="+"\""+filename+"\"");  
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
