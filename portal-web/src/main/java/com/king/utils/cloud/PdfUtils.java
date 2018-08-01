package com.king.utils.cloud;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.icepdf.core.pobjects.Document;
import org.icepdf.core.util.GraphicsRenderingHints;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

/**
 * pdf水印工具类
 * @author King chen
 * @emai 396885563@qq.com
 * @data 2018年7月23日
 */
public class PdfUtils {

	public enum Position {	
		  LEFT_TOP,LEFT_BUTTOM,RIGHT_TOP,RIGHT_BUTTOM,MIDDLE,	  
	}
	
	
	/**
	* pdf添加图片/二维码水印 默认右下
	 * @param sourceFilePath 源文件路径
	 * @param fileWaterMarkPath 生成后保存路径
	 * @param imageUrl 二维码图片路径
	 * @param imageWidth 图片高
	 * @param imageHeigth 图片宽
	 * @param margin_x 二维码放置的X轴边距
	 * @param margin_y 二维码放置的Y轴边距
	 * @param position 对齐方式：left_top、right_top、left_bottom、right_bottom、middle
	 */
	public static void setImageWater(String sourceFilePath, String fileWaterMarkPath, String imageUrl, int imageWidth,int imageHeigth, int margin_x, int margin_y, Position position) {
		PdfReader reader=null;
		PdfStamper stamp=null;
		try {
			reader = new PdfReader(sourceFilePath);
		    stamp = new PdfStamper(reader, new FileOutputStream(fileWaterMarkPath));
			int total = reader.getNumberOfPages() + 1;// 页数
			PdfContentByte content;
			Image img = Image.getInstance(imageUrl);
			// 图片的大小(width，height)
			img.scaleToFit(imageWidth, imageHeigth);
			for (int i = 1; i < total; i++) {
				float width = reader.getPageSize(i).getWidth();// 每页宽
				float heigth = reader.getPageSize(i).getHeight();// 每页高
				switch (position) {// 水印位置
				case LEFT_TOP:
					img.setAbsolutePosition(margin_x, heigth - imageHeigth - margin_y);
					break;
				case LEFT_BUTTOM:
					img.setAbsolutePosition(margin_x, margin_y);
					break;
				case RIGHT_TOP:
					img.setAbsolutePosition(width - imageWidth - margin_x, heigth - imageHeigth - margin_y);
					break;
				case RIGHT_BUTTOM:
					img.setAbsolutePosition(width - imageWidth - margin_x, margin_y);
					break;
				case MIDDLE:
					img.setAbsolutePosition(width / 2 - imageWidth / 2 - margin_x, heigth / 2 - imageHeigth / 2 - margin_y);
					break;
				default:// 默认右下
					img.setAbsolutePosition(width - imageWidth - margin_x, margin_y);
					break;
				}
				content = stamp.getOverContent(i);// 在内容上方加水印
				content.addImage(img);
			}
			stamp.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		} finally{
        	if(reader!=null){
        		reader.close();
        	}
        	if(stamp!=null){
        		try {
					stamp.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
        	}
        } 
	}
	
	  
	/**
	 * pdf设置文字水印
	 * @param sourceFilePath 源文件路径
	 * @param fileWaterMarkPath 生成后保存路径
	 * @param waterMarkContent 水印内容
	 * @param markContentColor 设置水印颜色
	 * @param fontSize 字体大小
	 * @param position 水印位置
	 * @param offset_x 水印位置偏移量
	 */
	public static void addTextWater(String sourceFilePath, String fileWaterMarkPath, String waterMarkContent,BaseColor markContentColor,float fontSize,Position position,int margin_x) {
		PdfReader reader=null;
		PdfStamper stamp=null;
		try {
			reader = new PdfReader(sourceFilePath);
		    stamp = new PdfStamper(reader, new FileOutputStream(fileWaterMarkPath));
			int total = reader.getNumberOfPages() + 1;// 页数
			PdfContentByte content;
			float x=0;
			float y=0;	
			for (int i = 1; i < total; i++) {
				float width = reader.getPageSize(i).getWidth();// 每页宽
				float heigth = reader.getPageSize(i).getHeight();// 每页高
				switch (position) {// 水印位置
				case LEFT_TOP:
					x= 5;
					y= heigth-15;
					break;
				case LEFT_BUTTOM:
					x= 5;//边距5
					y= 5;//边距5
					break;
				case RIGHT_TOP:
					x= width-margin_x;
					y= heigth-15;
					break;
				case RIGHT_BUTTOM:
					x= width-margin_x;
					y= 5;
					break;
				case MIDDLE:
					x= width/2;
					y= heigth/2;
					break;
				default:// 默认右下
					x= width-margin_x;
					y= 5;
					break;
				}
				content = stamp.getOverContent(i);// 在内容上方加水印
				content.beginText();  
		         //设置字体和大小  
				BaseFont bfChinese = BaseFont.createFont( "STSongStd-Light" ,"UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);//支持中文
				content.setFontAndSize(bfChinese, fontSize);
				//设置字体颜色
				content.setColorFill(markContentColor); 
		         //设置字体的输出位置  
				content.setTextMatrix(x, y);   
		         //要输出的text  
				content.showText(waterMarkContent);    
				content.endText();  
			}
			stamp.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
        	if(reader!=null){
        		reader.close();
        	}
        	if(stamp!=null){
        		try {
					stamp.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
        	}
        } 
	  }
	
	
	/**
	 * pdf转图片
	 * @param pdfPath pdf文件路径 
	 * @param savePath //保存路径
	 */
	public static void pdf2Pic(String pdfPath, String savePath) {
		Document document = new Document();
		File dir= new File(savePath);
		if(!dir.exists()){
			dir.mkdirs();
		}
		document.setFile(pdfPath);
		// 缩放比例、像素
		float scale = 5.5f;
		// 旋转角度
		float rotation = 0f;

		for (int i = 0; i < document.getNumberOfPages(); i++) {
			BufferedImage image = (BufferedImage) document.getPageImage(i, GraphicsRenderingHints.SCREEN,
					org.icepdf.core.pobjects.Page.BOUNDARY_CROPBOX, rotation, scale);
			RenderedImage rendImage = image;
			try {
				String imgName = i + ".png";
				System.out.println(imgName);
				File file = new File(savePath + imgName);
				ImageIO.write(rendImage, "png", file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			image.flush();
		}
		document.dispose();
	}
	
	/**
	 * 转换
	 * @param postion
	 * @return
	 */
	public static Position convert(String postion){
		if(postion.equals(Position.LEFT_TOP.toString())){
			return Position.LEFT_TOP;
		}else if(postion.equals(Position.LEFT_BUTTOM.toString())){
			return Position.LEFT_BUTTOM;
		}else if(postion.equals(Position.RIGHT_TOP.toString())){
			return Position.RIGHT_TOP;
		}else if(postion.equals(Position.MIDDLE.toString())){
			return Position.MIDDLE;
		}else if(postion.equals(Position.RIGHT_BUTTOM.toString())){
			return Position.RIGHT_BUTTOM;
		}else{
			return Position.LEFT_BUTTOM;
		}

	}
	
	public static void main(String[] args) {
		String sourceFilePath="D:\\pdftest1\\LibreOffice2.pdf";
		String fileWaterMarkPath="D:\\pdftest1\\20180122165624.pdf";

        String waterMarkContent="版权所有:http://chenhx.cn";  //水印内容
        addTextWater(sourceFilePath, fileWaterMarkPath, waterMarkContent, BaseColor.BLACK, 16, Position.RIGHT_BUTTOM,180);
	   
        //添加图片水印
		/*String imageUrl="D:\\pdftest1\\qrcode.jpg";
		int imageWidth=60;
		int imageHeigth=60;
		int x=5;
		int y=5;
		setImageWater(sourceFilePath, fileWaterMarkPath, imageUrl, imageWidth, imageHeigth, x, y,Position.RIGHT_TOP);*/
        
        //pdf转图片
      /*  String filePath = "D:\\pdftest1\\LibreOffice2.pdf";
        pdf2Pic(filePath, "D:\\pdftest1\\LibreOffice\\test2");*/
	}
}
