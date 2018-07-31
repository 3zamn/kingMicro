package test;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
/**
 * java实现爬虫
 */
public class SpiderDemo1 {
	//爬取链接 http://www.8btc.com/what-is-blockchain
	// String regex = "(http|https)://[\\w+\\.?/?]+\\.[A-Za-z]+";
	public static void spiderURL(String url,String regex,String filename){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	
		String time=sdf.format(new Date());
		URL realURL=null;
		URLConnection connection=null;
		BufferedReader br=null;
		PrintWriter pw=null;
		PrintWriter pw1=null;
		
		Pattern pattern=Pattern.compile(regex);
		try{
			realURL=new URL(url);
			connection = realURL.openConnection();
			connection.setRequestProperty("content-language", "zh-CN");
			connection.setRequestProperty("content-type", "text/html;charset=UTF-8");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			connection.connect();
		
			File fileDir = new File("E:/test/"+time);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			  String charset = "UTF-8";
              Pattern p = Pattern.compile("charset=\\S*");
              Matcher m = p.matcher(connection.getContentType());
              if (m.find()) {
                  charset = m.group().replace("charset=", "");
              }
        //      BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
			//将爬取到的内容放到E盘相应目录下   
			pw = new PrintWriter(new FileWriter("E:/test/"+time+"/"+filename+"_content.txt"),true);
			pw1 = new PrintWriter(new FileWriter("E:/test/"+time+"/"+filename+"_URL.txt"),true);
			
			br=new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
			String line=null;
			
			
			while((line=br.readLine())!=null){
				pw.println(line);
				Matcher matcher=pattern.matcher(line);
				while(matcher.find()){
					pw1.println(matcher.group());
				}
				
			}
			
			System.out.println("爬取成功！");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				br.close();
				pw.close();
				pw1.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
    public static void main(String[] args) {
    	String url="https://www.vvic.com/gz/hot/index.html";
    	String regex= "<a target=\"_blank\" href=\"/item+";
    	spiderURL(url,regex,"8btc");
    }
}
