package test;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import org.icepdf.core.pobjects.Document;
import org.icepdf.core.util.GraphicsRenderingHints;


/**
 * pdf 转高清图片
 * @author King chen
 * @emai 396885563@qq.com
 * @data 2018年7月17日
 */
public class IcePdf {
    public static void pdf2Pic(String pdfPath, String path){
        Document document = new Document();
        document.setFile(pdfPath);
        //缩放比例、像素
        float scale = 5.5f;
        //旋转角度
        float rotation = 0f;

        for (int i = 0; i < document.getNumberOfPages(); i++) {
            BufferedImage image = (BufferedImage)
                    document.getPageImage(i, GraphicsRenderingHints.SCREEN, org.icepdf.core.pobjects.Page.BOUNDARY_CROPBOX, rotation, scale);
            RenderedImage rendImage = image;
            try {
                String imgName = i + ".png";
                System.out.println(imgName);
                File file = new File(path + imgName);
                ImageIO.write(rendImage, "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            image.flush();
        }
        document.dispose();
    }
    public static void main(String[] args) {
        String filePath = "D:\\pdftest1\\LibreOffice2.pdf";
        pdf2Pic(filePath, "D:\\pdftest1\\LibreOffice\\test2");
    }
}  