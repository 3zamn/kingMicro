package test;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.net.ssl.HttpsURLConnection; 
  

/**
 * 文档图片去水印处理 --本地图片文件/文件夹或网络图片
 * @author King chen
 * @emai 396885563@qq.com
 * @data 2018年7月20日
 */
public class ImageConverter { 
    private static List<File> fileList = new ArrayList<File>(); 
  
    public static void main(String[] args) { 
        /*System.out.println("输入需要去水印的图片所在的根目录回车（支持递归子目录）："); 
        Scanner input = new Scanner(System.in); 
        String dir = input.nextLine().trim(); 
  
        System.out.println("输入转换后的存储目录："); 
        String saveDir = input.nextLine().trim(); 
  
        System.out.println("输入y开始"); 
        String comfrm = input.nextLine().trim(); 
        if (comfrm.equals("y")) { 
            convertAllImages(dir, saveDir); 
        } else { 
            System.out.println("您输入的不是y程序,程序退出"); 
        } */
  
     //   String dir = "E:\\data\\Desktop"; 
      //  String saveDir = "E:\\data\\Desktop-convert"; 
  
      //  replaceFolderImages(dir); 
      //  String srcFile = "F:\\test\\123.png"; 
        String srcFile = "http://www.oicqzone.com/img/allimg/170620/1_170620092535_1.png"; 
        String dstFile = "F:\\test\\310.png"; 
        long begin = System.currentTimeMillis(); 
        convertAllImages(srcFile, dstFile); 
        long time = System.currentTimeMillis() - begin; 
        System.out.println("耗时:"+time+"秒！"); 
    } 
  
    private static void convertAllImages(String dir, String saveDir) { 
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
  /*  private static void convertAllImages(String dir, String saveDir) { 
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
    } */
  
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
          		  System.out.println("x:"+i+",y:"+j+"值"+"("+red+","+greed+","+blue+")"); 
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
} 