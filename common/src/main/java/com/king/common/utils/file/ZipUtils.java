package com.king.common.utils.file;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 文件压缩
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年4月20日
 */
public class ZipUtils {
	public static final String JAR_END = ".jar";
	private static Log log = LogFactory.getLog(ZipUtils.class);
	public static final String RAR_END = ".rar";
	public static final String SYS_FILE_SEPARATOR = "/";
	public static final String WAR_END = ".war";
	public static final String ZIP_END = ".zip";

	public static void unzip(String paramString1, String paramString2)
			throws IOException {
		if ((!paramString1.endsWith(".zip"))
				&& (!paramString1.endsWith(".war"))) {
			log.warn("Only zip/war File can be unzipped.");
			return;
		}
		ZipFile localZipFile = new ZipFile(paramString1);
		Enumeration localEnumeration = localZipFile.entries();
		byte[] arrayOfByte = new byte[1048576];
		try {
			while ((localEnumeration != null)
					&& (localEnumeration.hasMoreElements())) {
				ZipEntry localZipEntry = (ZipEntry) localEnumeration
						.nextElement();
				DataInputStream localDataInputStream = new DataInputStream(
						localZipFile.getInputStream(localZipEntry));
				File localFile1 = new File(paramString2,
						localZipEntry.getName());
				File localFile2 = new File(localFile1.getParent());
				String str1 = localZipEntry.toString();
				String str2 = str1.substring(str1.length() - 1, str1.length());
				if ((!localFile1.exists()) && (str2.equals("/")))
					localFile1.mkdirs();
				else if (!localFile2.exists())
					localFile2.mkdirs();
				if (!localFile1.isDirectory()) {
					FileOutputStream localFileOutputStream = new FileOutputStream(
							localFile1);
					int i = 0;
					while ((i = localDataInputStream.read(arrayOfByte)) > -1)
						localFileOutputStream.write(arrayOfByte, 0, i);
					localDataInputStream.close();
					localFileOutputStream.close();
				}
			}
			localZipFile.close();
			log.debug("Finish unzip file: " + paramString1);
		} catch (ZipException localZipException) {
			localZipFile.close();
			localZipException.getMessage();
		}
	}

	private static void zip(String paramString, File paramFile)
			throws IOException {
		log.info("[INFO: Begin to compress file:" + paramFile.getAbsolutePath()
				+ "]");
		ZipOutputStream localZipOutputStream = new ZipOutputStream(
				new FileOutputStream(paramString));
		zip(localZipOutputStream, paramFile, "");
		localZipOutputStream.close();
	}

	private static void zip(ZipOutputStream paramZipOutputStream,
			File paramFile, String paramString) throws IOException {
		File[] files;
		if (paramFile.isDirectory()) {
			files = paramFile.listFiles();
			paramZipOutputStream.putNextEntry(new ZipEntry(paramString + "/"));
			paramString = paramString + "/";
			for (int i = 0; i < files.length; i++)
				zip(paramZipOutputStream, files[i],
						paramString + files[i].getName());
		} else {
			paramZipOutputStream.putNextEntry(new ZipEntry(paramString));
			BufferedInputStream bufIn = new BufferedInputStream(
					new FileInputStream(paramFile));
			byte[] arrayOfByte = new byte[1048576];
			int j = 0;
			while ((j = ((InputStream) bufIn).read(arrayOfByte)) != -1)
				paramZipOutputStream.write(arrayOfByte, 0, j);
			((InputStream) bufIn).close();
		}
	}

	private static void zip(ZipOutputStream paramZipOutputStream,
			File[] paramArrayOfFile, String paramString) throws IOException {
		if ((paramArrayOfFile == null) || (paramArrayOfFile.length < 1))
			return;
		for (int i = 0; i < paramArrayOfFile.length; i++)
			zip(paramZipOutputStream, paramArrayOfFile[i],
					paramArrayOfFile[i].getName());
	}

	public static String zipDir(String desDir) throws IOException {
		String str = desDir+ ".zip";
		zip(str, new File(desDir));
		return str;
	}

	public static void zipDirs(String paramString1, String paramString2)
			throws IOException {
		zip(paramString2, new File(paramString1));
	}

	public static String zipFile(String paramString) throws IOException {
		File localFile = new File(paramString);
		String str = paramString.substring(paramString.lastIndexOf("/") + 1);
		ZipOutputStream localZipOutputStream = new ZipOutputStream(
				new FileOutputStream(paramString + ".zip"));
		zip(localZipOutputStream, localFile, str);
		localZipOutputStream.close();
		return str + ".zip";
	}

	public static String zipFile(String paramString1, String paramString2)
			throws IOException {
		if (!paramString2.endsWith(".zip"))
			paramString2 = paramString2 + ".zip";
		File localFile = new File(paramString1);
		String str = paramString1.substring(paramString1.lastIndexOf("/") + 1);
		ZipOutputStream localZipOutputStream = new ZipOutputStream(
				new FileOutputStream(paramString2));
		zip(localZipOutputStream, localFile, str);
		localZipOutputStream.close();
		return paramString2;
	}

	public static void zipFiles(File[] paramArrayOfFile, String paramString)
			throws Exception {
		if (!paramString.endsWith(".zip"))
			paramString = paramString + ".zip";
		ZipOutputStream localZipOutputStream = new ZipOutputStream(
				new FileOutputStream(paramString));
		zip(localZipOutputStream, paramArrayOfFile, "");
		localZipOutputStream.close();
	}

	public static void main(String[] args) throws Exception{
		/*String filePath = "C:\\Users\\Administrator\\Desktop\\commons_core.zip";
		unzip(filePath, "d:\\codegen\\commons_core");*/
		String desDir="D:\\chx\\workspace_kingMicro\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\portal-web\\WEB-INF\\classes\\gen\\天猫Java并发编程常识";
		zipDir(desDir);
	}
}