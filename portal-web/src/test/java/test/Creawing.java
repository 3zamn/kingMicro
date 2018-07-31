package test;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.king.common.utils.network.HttpUtils;
import com.king.common.utils.network.ResponseWrap;

public class Creawing {
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		try {
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			
			String time=sdf.format(new Date());
			String url = "https://www.vvic.com/gz/hot/index.html";
	        HttpUtils http = HttpUtils.get(url);
	        ResponseWrap response = http.execute(); //执行请求
	        String line=null;      
			PrintWriter pw=null;
	        String regex= "<a target=\"_blank\" href=\"/item.*?\" class=\"j-vda\"";
	        String begin="<a target=\"_blank\" href=\"";
	        String end="\" class=\"j-vda\"";
	        Pattern pattern=Pattern.compile(regex);
	        BufferedReader br=response.getBufferedReader();    
			pw = new PrintWriter(new FileWriter("E:/test/"+time+"/"+"URL.txt"),true);
			while((line=br.readLine())!=null){
				Matcher matcher=pattern.matcher(line);
				while(matcher.find()){
					String str=matcher.group();
					str=str.replace(begin, "https://www.vvic.com");
					str=str.replace(end, "");
					pw.println(str);
				}			
			}       
	        http.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
		  
		 
	}
}
