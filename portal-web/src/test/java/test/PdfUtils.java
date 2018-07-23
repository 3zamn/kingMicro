package test;

import java.io.FileOutputStream;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

/**
 * pdf工具类
 * @author King chen
 * @emai 396885563@qq.com
 * @data 2018年7月23日
 */
public class PdfUtils {

	public enum Position {	
		  LEFT_TOP,LEFT_BUTTOM,RIGHT_TOP,RIGHT_BUTTOM,MIDDLE,	  
}
	/**
	* 添加二维码
	 * @param sourceFilePath 源文件路径
	 * @param fileWaterMarkPath 目标路径
	 * @param imageUrl 二维码图片路径
	 * @param imageWidth 图片高
	 * @param imageHeigth 图片宽
	 * @param x 二维码放置的X轴边距
	 * @param y 二维码放置的Y轴边距
	 * @param position 对齐方式：left_top、right_top、left_bottom、right_bottom、middle
	 */
	public static void setWaterForPDF(String sourceFilePath, String fileWaterMarkPath,String imageUrl,int imageWidth,int imageHeigth,int x,int y,Position position) {
	    	
		try{
	        	PdfReader reader = new PdfReader(sourceFilePath);
		        PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(fileWaterMarkPath));
		        int total = reader.getNumberOfPages() + 1;//页数
		        PdfContentByte content;
		        Image img = Image.getInstance(imageUrl);
		        //图片的大小(width，height)
		        img.scaleToFit(imageWidth, imageHeigth);	       
		        for (int i = 1; i < total; i++) {
		        	float width=reader.getPageSize(i).getWidth();//每页宽
		        	float heigth=reader.getPageSize(i).getHeight();//每页高
		        	 switch (position) {//水印位置
						case LEFT_TOP:		
							img.setAbsolutePosition(width-imageWidth-x, y);
							break;
						case LEFT_BUTTOM:		
							img.setAbsolutePosition(width-imageWidth-x, y);
							break;
						case RIGHT_TOP:		
							img.setAbsolutePosition(width-imageWidth-x, y);
							break;
						case RIGHT_BUTTOM:	
							img.setAbsolutePosition(width-imageWidth-x, y);
							break;
						case MIDDLE:		
							img.setAbsolutePosition(width-imageWidth-x, y);
							break;
						default :
							break;
						}
		        	
		            content = stamp.getOverContent(i);// 在内容上方加水印
		            System.out.println("width:"+width);
		            System.out.println("heigth:"+heigth);
		            content.addImage(img);
		        }
		        System.out.println("水印添加成功");
		        stamp.close();
		        reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	
	public void setPosition(int x,int y,Position position) {
		
	}
	
	public static void main(String[] args) {
		String sourceFilePath="D:\\pdftest1\\LibreOffice2.pdf";
		String fileWaterMarkPath="D:\\pdftest1\\20180122165623.pdf";
		String imageUrl="D:\\pdftest1\\qrcode.jpg";
		int imageWidth=60;
		int imageHeigth=60;
		int x=10;
		int y=10;
		setWaterForPDF(sourceFilePath, fileWaterMarkPath, imageUrl, imageWidth, imageHeigth, x, y,Position.RIGHT_BUTTOM);
	}
}
