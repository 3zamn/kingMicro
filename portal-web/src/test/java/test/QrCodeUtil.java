package test;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;


/**
 *  二维码生成和读取工具
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年7月23日
 */
public class QrCodeUtil {
    
    /**
     * 生成包含字符串信息的二维码图片
     * @param outputStream 文件输出流路径
     * @param content 二维码携带信息
     * @param qrCodeSize 二维码图片大小
     * @param imageFormat 二维码的格式
     * @throws WriterException 
     * @throws IOException 
     */
	public static boolean createQrCode(OutputStream outputStream, String content, int qrCodeSize, String imageFormat)throws WriterException, IOException {
		// 设置二维码纠错级别ＭＡＰ
		Hashtable<EncodeHintType, Object> hintMap = new Hashtable<EncodeHintType, Object>();
		hintMap.put(EncodeHintType.CHARACTER_SET, "utf-8"); // 指定字符编码为“utf-8”
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M); // 指定二维码的纠错等级为中级
		hintMap.put(EncodeHintType.MARGIN, 0);// 边距
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		// 创建比特矩阵(位矩阵)的QR码编码的字符串
		BitMatrix byteMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize, hintMap);
		int matrixWidth = byteMatrix.getWidth();
		BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
		image.createGraphics();
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, matrixWidth, matrixWidth);
		// 使用比特矩阵画并保存图像
		graphics.setColor(Color.BLACK);
		for (int i = 0; i < matrixWidth; i++) {
			for (int j = 0; j < matrixWidth; j++) {
				if (byteMatrix.get(i, j)) {
					graphics.fillRect(i, j, 1, 1);
				}
			}
		}
		return ImageIO.write(image, imageFormat, outputStream);
	}
      

	/**
	 * 读二维码并输出携带的信息
	 * @param inputStream 输入文件流
	 * @return
	 * @throws IOException
	 */
	public static String readQrCode(InputStream inputStream) throws IOException {
		// 从输入流中获取字符串信息
		BufferedImage image = ImageIO.read(inputStream);
		// 将图像转换为二进制位图源
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader = new QRCodeReader();
		Result result = null;
		try {
			result = reader.decode(bitmap);
		} catch (ReaderException e) {
			e.printStackTrace();
		}
		return result.getText();
	}
    
    /**
     * 测试代码
     * @throws WriterException 
     */
    public static void main(String[] args) throws IOException, WriterException {  
        
        createQrCode(new FileOutputStream(new File("D:\\pdftest1\\qrcode.jpg")),"http://chenhx.cn/portal-html/login.html",60,"JPEG");
        System.out.println(readQrCode(new FileInputStream(new File("D:\\pdftest1\\qrcode.jpg"))));  
    }  
  
}