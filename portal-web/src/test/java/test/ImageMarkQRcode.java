package test;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;   
  
 
/**
 *  图片水印/二维码
 * @author King chen
 * @emai 396885563@qq.com
 * @data 2018年7月23日
 */
public class ImageMarkQRcode {
	
	public static void main(String[] args) {
		String iconPath="D:\\pdftest1\\aa.png";
		String srcImgPath="D:\\pdftest1\\20180122165620.jpg";
		String targerPath="D:\\pdftest1\\20180723.jpg";
		markImageByIcon(iconPath, srcImgPath, targerPath, null);   
	}
  

  
    /**  
     * 给图片添加水印、可设置水印图片旋转角度  
     * @param iconPath 水印图片路径  
     * @param srcImgPath 源图片路径  
     * @param targerPath 目标图片路径  
     * @param degree 水印图片旋转角度  
     */  
    @SuppressWarnings("restriction")
	public static void markImageByIcon(String iconPath, String srcImgPath,   
            String targerPath, Integer degree) {   
        OutputStream os = null;   
        try { 
            Image srcImg = ImageIO.read(new File(srcImgPath));  
            BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null),   
                    srcImg.getHeight(null), BufferedImage.TYPE_INT_RGB);   
  
            // 得到画笔对象   
            // Graphics g= buffImg.getGraphics();   
            Graphics2D g = buffImg.createGraphics();   
  
            // 设置对线段的锯齿状边缘处理   
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,   
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);   
  
            g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), 
            		srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0, null);   
  
            if (null != degree) {   
                // 设置水印旋转   
                g.rotate(Math.toRadians(degree),   
                        (double) buffImg.getWidth() / 2, (double) buffImg   
                                .getHeight() / 2);   
            }   
  
            // 水印图象的路径 水印一般为gif或者png的，这样可设置透明度   
            ImageIcon imgIcon = new ImageIcon(iconPath);  
            System.out.println(iconPath);
  
            // 得到Image对象。   
            Image img = imgIcon.getImage();   
           
  
            float alpha = 1f; // 透明度   
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,   
                    alpha));   
  
            // 表示水印图片的位置   
            // (1).水印只有一张
            g.drawImage(img, 5, 5, null);   
            
            //(2).水印存在于多个位置
         /*   for(int i=0;i<srcImg.getWidth(null)/300;i++){
            	g.drawImage(img, 0, i*300, null);   
            }	*/
  
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));   
  
            g.dispose();   
  
          //  os = new FileOutputStream(targerPath);   
  
            // 生成图片   
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
     * @param 源图片路径
     * @param 需要添加水印的文字信息
     * @param 添加水印后存储的路径
     */
    @SuppressWarnings("restriction")
	public   static   void  pressText(File pic,String txt,String realPath) {  
       FileOutputStream fos=null;
    	try  {  
        	Image image=ImageIO.read(pic);//读取要添加水印的图片
        	
        	//获取图片大小
        	int width=image.getWidth(null);
        	int height=image.getHeight(null);
        	
        	//1.创建一个图片缓存对象
            BufferedImage bufferImage = new  BufferedImage(width, height,  
                    BufferedImage.TYPE_INT_RGB); 
            //2.创建java绘图工具
            Graphics2D g = bufferImage.createGraphics(); 
            
            //3.使用绘图工具对象将原图绘制到缓存对象中
            g.drawImage(image, 0 ,  0 , width, height,  null );  
            // String s="www.qhd.com.cn";  
            
            //4.添加水印
            g.setColor(Color.RED);  
            g.setFont(new  Font("微软雅黑",Font.BOLD,30));  
            
            int x=width-30*txt.length();
            int y=height-30;
            
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,0.5f));
          
            g.drawString(txt, x, y);
            g.dispose();  
            
            //5.创建图片编码类，对图片进行编码处理
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
    
    
}  
