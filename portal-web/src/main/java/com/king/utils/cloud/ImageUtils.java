package com.king.utils.cloud;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.ImageIcon;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;   
  
 
/**
 *  图片加水印/二维码/文字或去水印
 * @author King chen
 * @emai 396885563@qq.com
 * @data 2018年7月23日
 */
@SuppressWarnings("restriction")
public class ImageUtils {
	  private static List<File> fileList = new ArrayList<File>(); 
	  
    /**  
     * 给图片添加水印、可设置水印图片旋转角度  
     * @param iconPath 水印图片路径  
     * @param srcImgPath 源图片路径  
     * @param targerPath 目标图片路径  
     * @param degree 水印图片旋转角度  
     */  
	public static void addWaterWithIcon(String iconPath, String srcImgPath,String targerPath, Integer degree) {  
    	
    	OutputStream os = null;   
		try {
            Image srcImg = ImageIO.read(new File(srcImgPath));  
			BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null), srcImg.getHeight(null),BufferedImage.TYPE_INT_RGB);
			Graphics2D g = buffImg.createGraphics();
			// 设置对线段的锯齿状边缘处理
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg.getHeight(null), Image.SCALE_SMOOTH), 0,0, null); 
			if (null != degree) {
				// 设置水印旋转
				g.rotate(Math.toRadians(degree), (double) buffImg.getWidth() / 2, (double) buffImg.getHeight() / 2);
			}
            // 水印图象的路径 水印一般为gif或者png的，这样可设置透明度   
            ImageIcon imgIcon = new ImageIcon(iconPath);  
            System.out.println(iconPath);
            // 得到Image对象。   
            Image img = imgIcon.getImage();   
            float alpha = 1f; // 透明度   
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));   
            // .水印只有一张
            g.drawImage(img, 5, 5, null);           
            //.水印存在于多个位置
         /*   for(int i=0;i<srcImg.getWidth(null)/300;i++){
            	g.drawImage(img, 0, i*300, null);   
            }	*/
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));   
            g.dispose();   
            os=new FileOutputStream(targerPath);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);  
            encoder.encode(buffImg);    
  
        } catch (Exception e) {   
            e.printStackTrace();   
        } finally {   
            try {   
                if (null != os)   
                    os.close();   
            } catch (Exception e) {   
                e.printStackTrace();   
            }   
        }   
    } 
 
    /**
     * 文字水印
     * @param srcImgPath 源图片路径
     * @param txt 需要添加水印的文字信息
     * @param realPath 添加水印后存储的路径
     */
	public static void  addWaterWithText(String srcImgPath,String txt,String realPath) {  
       FileOutputStream fos=null;
    	try  { 
            Image image = ImageIO.read(new File(srcImgPath));//文件转化为图片
        	int width=image.getWidth(null);
        	int height=image.getHeight(null);	
            BufferedImage bufferImage = new  BufferedImage(width, height,BufferedImage.TYPE_INT_RGB); 
            Graphics2D g = bufferImage.createGraphics();          
            g.drawImage(image, 0 ,  0 , width, height,  null );  
            g.setColor(Color.RED);  
            g.setFont(new  Font("微软雅黑",Font.BOLD,30));      
            int x=width-30*txt.length();
            int y=height-30;          
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,0.5f));     
            g.drawString(txt, x, y);
            g.dispose();          
           fos=new FileOutputStream(realPath);
           JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fos);  
           encoder.encode(bufferImage);            
        } catch  (Exception e) {  
            System.out.println(e);  
        } finally{
        	if(fos!=null){
        		try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        } 
    }   
    
    
    /**
     * 去水印
     * @param dir 目标地址本地文件或网络地址
     * @param saveDir 本地保存路径
     */
    private static void removeWater(String dir, String saveDir) { 
       if(dir.contains("http")){//判断是本地图片还是网络图片
    	   replaceColor(dir, saveDir); 
       }else{
    	   File dirFile = new File(dir); 
           File saveDirFile = new File(saveDir); 
           dir = dirFile.getAbsolutePath(); 
           saveDir = saveDirFile.getAbsolutePath(); 
           loadImages(new File(dir)); 
           for (File file : fileList) { 
               String filePath = file.getAbsolutePath(); 
     
               String dstPath = saveDir + filePath.substring(filePath.indexOf(dir) + dir.length(), filePath.length()); 
               System.out.println("converting: " + filePath); 
               replaceColor(file.getAbsolutePath(), dstPath); 
           } 
       }
    } 

  
    /**
     * 加载图片
     * @param f
     */
    public static void loadImages(File f) { 
        if (f != null) { 
            if (f.isDirectory()) { 
                File[] fileArray = f.listFiles(); 
                if (fileArray != null) { 
                    for (int i = 0; i < fileArray.length; i++) { 
                        //递归调用 
                        loadImages(fileArray[i]); 
                    } 
                } 
            } else { 
                String name = f.getName(); 
                if (name.endsWith("png") || name.endsWith("jpg")) { 
                    fileList.add(f); 
                } 
            } 
        } 
    } 
  
    private static void replaceFolderImages(String dir) { 
        File dirFile = new File(dir); 
        File[] files = dirFile.listFiles(new FileFilter() { 
            public boolean accept(File file) { 
                String name = file.getName(); 
                if (name.endsWith("png") || name.endsWith("jpg")) { 
                    return true; 
                } 
                return false; 
            } 
        }); 
        for (File img : files) { 
            replaceColor(img.getAbsolutePath(), img.getAbsolutePath()); 
        } 
    } 
  
    private static void replaceColor(String srcFile, String dstFile) { 
        try { 
            Color color = new Color(255, 195, 195); 
            replaceImageColor(srcFile, dstFile, color, Color.WHITE); 
        } catch (IOException e) { 
            e.printStackTrace(); 
        } 
    } 
  
    public static void replaceImageColor(String file, String dstFile, Color srcColor, Color targetColor) throws IOException { 
        URL http; 
        if (file.trim().startsWith("https")) { 
            http = new URL(file); 
            HttpsURLConnection conn = (HttpsURLConnection) http.openConnection(); 
            conn.setRequestMethod("GET"); 
        } else if (file.trim().startsWith("http")) { 
            http = new URL(file); 
            HttpURLConnection conn = (HttpURLConnection) http.openConnection(); 
            conn.setRequestMethod("GET"); 
        } else { 
            http = new File(file).toURI().toURL(); 
        } 
        BufferedImage bi = ImageIO.read(http.openStream()); 
        if(bi == null){ 
            return; 
        } 
  
        Color wColor = new Color(255, 255, 255); 
        for (int i = 0; i < bi.getWidth(); i++) { 
            for (int j = 0; j < bi.getHeight(); j++) { 
             
                int color = bi.getRGB(i, j); 
                Color oriColor = new Color(color); 
                int red = oriColor.getRed(); 
                int greed = oriColor.getGreen(); 
                int blue = oriColor.getBlue(); 
            	if(bi.getRGB(i, j)!=-1){
          //		  System.out.println("x:"+i+",y:"+j+"值"+"("+red+","+greed+","+blue+")"); 
            	}
                //粉色 
                if (greed < 190 || blue < 190) { 
  
                } else { 
                    if (red == 255 && greed > 180 && blue > 180) { 
                        bi.setRGB(i, j, wColor.getRGB()); 
                    } 
                } 
            } 
        } 
        String type = file.substring(file.lastIndexOf(".") + 1, file.length()); 
        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName(type); 
        ImageWriter writer = it.next(); 
        File f = new File(dstFile); 
        f.getParentFile().mkdirs(); 
        ImageOutputStream ios = ImageIO.createImageOutputStream(f); 
        writer.setOutput(ios); 
        writer.write(bi); 
        bi.flush(); 
        ios.flush(); 
        ios.close(); 
    } 
    
	public static void main(String[] args) {
		//去水印测试
		String srcFile = "http://www.oicqzone.com/img/allimg/170620/1_170620092535_1.png";
		String dstFile = "F:\\test\\31.png";
		long begin = System.currentTimeMillis();
		removeWater(srcFile, dstFile);
		long time = System.currentTimeMillis() - begin;
		System.out.println("耗时:" + time + "秒！");

		//加图片水印测试
		String iconPath = "D:\\pdftest1\\aa.png";
		String srcImgPath = "D:\\pdftest1\\20180122165620.jpg";
		String targerPath = "D:\\pdftest1\\20180723.jpg";
		addWaterWithIcon(iconPath, srcImgPath, targerPath, null);
		
		//加文字水印测试
		addWaterWithText(iconPath, "版权所有:http://chenhx.cn", targerPath);
	}
}  
