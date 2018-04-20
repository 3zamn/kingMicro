package com.king.common.utils.file;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ClassUtils;

/**
 * 文件工具类
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年4月20日
 */
public final class FileToolkit {
	private static final Log log = LogFactory.getLog(FileToolkit.class);

	/**
	 * 复制文件或者目录,复制前后文件完全一样。
	 * 
	 * @param resFilePath
	 *            源文件路径
	 * @param distFolder
	 *            目标文件夹
	 * @IOException 当操作发生异常时抛出
	 */
	public static void copyFile2Dir(String resFilePath, String distFolder)
			throws Exception {
		File resFile = new File(resFilePath);
		File distFile = new File(distFolder);

		if (resFile.isDirectory()) {
			FileUtils.copyDirectoryToDirectory(resFile, distFile);
		} else if (resFile.isFile()) {
			FileUtils.copyFileToDirectory(resFile, distFile, true);
		}
	}
	
	public static void copyFile(String resFilePath, String destFile)
			throws Exception {
		File resFile = new File(resFilePath);
		File distFile = new File(destFile);

		FileUtils.copyFile(resFile, distFile);
	}
	
	public static void copyFileByList(String listFile,String distFolder) throws Exception{
		File file = ResourceUtils.getFile(listFile);
		if (file.exists()){
			List list = FileUtils.readLines(file, "GBK");
			if (list != null && list.size() > 0){
				for (Iterator it = list.iterator(); it.hasNext();){
					String filePath = (String)it.next();
					copyFile2Dir(filePath,distFolder);
				}
			}
		}
	}

	/**
	 * 删除一个文件或者目录
	 * 
	 * @param targetPath
	 *            文件或者目录路径
	 * @IOException 当操作发生异常时抛出
	 */
	public static void deleteFile(String targetPath) throws IOException {
		File targetFile = new File(targetPath);
		if (targetFile.isDirectory()) {
			FileUtils.deleteDirectory(targetFile);
		} else if (targetFile.isFile()) {
			targetFile.delete();
		}
	}

	/**
	 * 移动文件或者目录,移动前后文件完全一样,如果目标文件夹不存在则创建。
	 * 
	 * @param resFilePath
	 *            源文件路径
	 * @param distFolder
	 *            目标文件夹
	 * @IOException 当操作发生异常时抛出
	 */
	public static void moveFile(String resFilePath, String distFolder) throws IOException{
		File resFile = new File(resFilePath);
		moveFile(resFile,distFolder);
	}
	
	public static void moveFile(File resFile, String distFolder) throws IOException {
		if (resFile != null) {
			File distFile = new File(distFolder);
			if (resFile.isDirectory()) {
				FileUtils.moveDirectoryToDirectory(resFile, distFile, true);
			} else if (resFile.isFile()) {
				FileUtils.moveFileToDirectory(resFile, distFile, true);
			}
		} else {
			throw new IOException("resFile is null and distFolder: " + distFolder);
		}
	}

	public static void moveFileForce(String resFilePath, String distFolder) throws IOException{
		File resFile= new File(resFilePath);
		moveFileForce(resFile,distFolder);
	}
	
	public static synchronized void moveFileForce(File resFile,String distFolder) throws IOException{
		File distFile = new File(distFolder);
		if (resFile.isDirectory()) {
			FileUtils.moveDirectoryToDirectory(resFile, distFile, true);
		} else if (resFile.isFile()) {
			File destFile = new File(distFolder, resFile.getName());
			if (destFile.exists()){
				destFile.delete();
			}
			FileUtils.moveFileToDirectory(resFile, distFile, true);
		}
	}
	/**
	 * 重命名文件或文件夹
	 * 
	 * @param resFilePath
	 *            源文件路径
	 * @param newFileName
	 *            重命名
	 * @return 操作成功标识
	 */
	public static File renameFile(String resFilePath, String newFileName) {
		File resFile = new File(resFilePath);
		if (!resFile.exists()){
			if (log.isWarnEnabled()){
				log.warn("renameFile error:File [" + resFilePath + "] does not exists!");
			}
			return null;
		}
		String newFilePath = resFile.getParent() +File.separator + newFileName;
		File newFile = new File(newFilePath);
		if (resFile.renameTo(newFile)){
			return newFile;
		}
		return null;
	}

	/**
	 * 读取文件或者目录的大小
	 * 
	 * @param distFilePath
	 *            目标文件或者文件夹
	 * @return 文件或者目录的大小，如果获取失败，则返回-1
	 */
	public static long genFileSize(String distFilePath) {
		File distFile = new File(distFilePath);
		if (distFile.isFile()) {
			return distFile.length();

		} else if (distFile.isDirectory()) {
			return FileUtils.sizeOfDirectory(distFile);
		}
		return -1L;
	}

	/**
	 * 判断一个文件是否存在
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 存在返回true，否则返回false 网管网bitsCN_com
	 */
	public static boolean isExist(String filePath) {
		return new File(filePath).exists();
	}

	/**
	 * 本地某个目录下的文件列表（不递归）
	 * 
	 * @param folder
	 *            ftp上的某个目录
	 * @param suffix
	 *            文件的后缀名（比如.mov.xml)
	 * @return 文件名称列表
	 */

	public static String[] listFilebySuffix(String folder, String suffix) {
		IOFileFilter fileFilter1 = new SuffixFileFilter(suffix);
		IOFileFilter fileFilter2 = new NotFileFilter(
				DirectoryFileFilter.INSTANCE);
		FilenameFilter filenameFilter = new AndFileFilter(fileFilter1,
				fileFilter2);
		return new File(folder).list(filenameFilter);
	}

	/**
	 * 将字符串写入指定文件(当指定的父路径中文件夹不存在时，会最大限度去创建，以保证保存成功！)
	 * 
	 * @param res
	 *            原字符串
	 * @param filePath
	 *            文件路径
	 * @return 成功标记
	 */
	public static boolean string2File(String res, String filePath) {
		boolean flag = true;
		BufferedReader bufferedReader = null;

		BufferedWriter bufferedWriter = null;
		try {
			File distFile = new File(filePath);
			if (!distFile.getParentFile().exists())
				distFile.getParentFile().mkdirs();
			bufferedReader = new BufferedReader(new StringReader(res));
			bufferedWriter = new BufferedWriter(new FileWriter(distFile));
			char buf[] = new char[1024]; // 字符缓冲区
			int len;
			while ((len = bufferedReader.read(buf)) != -1) {
				bufferedWriter.write(buf, 0, len);
			}

			bufferedWriter.flush();
			bufferedReader.close();
			bufferedWriter.close();
		} catch (IOException e) {
			flag = false;
			e.printStackTrace();

		}
		return flag;
	}
	
	public static String getLine(File file,int line,String encoding) throws Exception{
		if (file.exists()){
			LineIterator it = FileUtils.lineIterator(file, encoding);
			int i = 0;
			while (it.hasNext()){
				i++;
				if (i == line){
					return it.nextLine();
				}
			}
		}
		return null;
	}
	
	public static synchronized void mkdirs(String path) throws Exception{
		FileUtils.forceMkdir(new File(path));
	}
	
	
	
	private static final int BUFFER_MAX_SIZE = 1024*150 ;

	/**
	 * 把source的内容写入到合法的dest文件中去(非路劲).
	 * @param dest
	 * 				目标文件地址
	 * @param source
	 * 				要写入文件的数据
	 * @return File 
	 * 				返回新生成的文件. 
	 * @throws IOException
	 * 				把一切可能抛出的IO异常抛出
	 */
	public static File write(String dest
			, StringBuffer source ) throws IOException {
		File file = new File(dest);
		//String path = file.getAbsolutePath();
		if (file.exists()) {
			throw new IllegalArgumentException("文件已存在") ;
		}

		FileOutputStream out = null;
		ByteArrayInputStream input = null;
		try {
			out = new FileOutputStream(file);
			byte[] bytes = source.toString().getBytes();
			input = new ByteArrayInputStream(bytes);
			writeNotFile(input, out) ;
			log.debug("写入完成") ;
		} finally {
			if (null != input){
				input.close();
			}
			if (null != out){
				out.close();
			}
			if (null != source){
				source.delete(0, source.length());
			}
			log.debug("关闭通道资源") ;
		}
		return file ;
	}

	/**
	 * 将非文件Input的内容根据文件输出流写到文件中去. 
	 * 如果要写入的文件已存在, 是否覆盖其中的内容, 取决于提供的流定义.
	 * 如果文件不允许读/写操作, 责抛出IO异常 
	 * @param input
	 * 				非文件的输入流
	 * @param output
	 * 				制定文件的输出流
	 * @throws IOException
	 * 				抛出一切可能发生的IO异常
	 */
	public static void writeNotFile(InputStream input
			, FileOutputStream output) throws IOException {
		FileChannel channel = null;
		ReadableByteChannel read = null;
		try {
			channel = output.getChannel() ;
			read = Channels.newChannel(input);
//			ByteBuffer buffer = ByteBuffer.allocate(resetBufferSize(input.available()));
//			while (read.read(buffer) != -1) {
//				buffer.flip();
//				channel.write(buffer);
//				buffer.clear();
//			}
			channel.transferFrom(read, 0, input.available()) ;
		} finally {
			if (null != read){
				read.close();
			}
			if (null != channel){
				channel.close();
			}
		}
	}

	/**
	 * 将一个文件的内容拷贝到另一个文件中.  
	 * 如果要写入的文件已存在, 是否覆盖其中的内容, 取决于提供的流定义.
	 * 如果文件不允许读/写操作, 责抛出IO异常
	 * @param input
	 * 				要读取的文件流
	 * @param output
	 * 				要写入的文件流
	 * @throws IOException
	 * 				抛出一切可能发生的IO异常
	 */
	public static void write(FileInputStream input
			, FileOutputStream output) throws IOException {
		FileChannel channel = null;
		FileChannel read = null;
		try {
			channel = output.getChannel() ;
			read = input.getChannel() ;
//			ByteBuffer buffer = ByteBuffer.allocate(resetBufferSize(input.available()));
//			while (read.read(buffer) != -1) {
//				buffer.flip();
//				channel.write(buffer);
//				buffer.clear() ;
//			}
			int max_size = input.available() ;
			int read_size = 0 ;
			while (read_size<max_size){
				read_size += read.transferTo(read_size, resetBufferSize(input.available()), channel) ;
			}
		} finally {
			if (null != read){
				read.close();
			}
			if (null != channel){
				channel.close();
			}
		}
	}

	/**
	 * 读取文件内容组装成StringBuffer, 由于是将文件内容直接写入StringBuffer
	 * 中, 不建议读取大文件内容(如上100M的文件)
	 * @param file
	 * 				待读取的文件.
	 * @return StringBuffer
	 * 				返回一个包含全文件内容的StringBuffer
	 * @throws IOException
	 * 				抛出一切可能发生的IO操作异常
	 */
	public static StringBuffer read(String file) throws IOException {
		FileInputStream input = null ;
		try {
			input = new FileInputStream(file) ;
			return read(input) ;
		} finally {
			if (null != input){
				input.close() ;
			}
		}
	}

	/**
	 * 读取输入流中的字节码组装成StringBuffer, 由于是将文件内容直接写入StringBuffer
	 * 中, 不建议读取太大的输入流
	 * @param input
	 * 				提供的输入流
	 * @return StringBuffer
	 * 				返回一个包含输入流内容的StringBuffer
	 * @throws IOException
	 * 				抛出一切可能发生的IO操作异常
	 */
	public static StringBuffer read(InputStream input) 
			throws IOException {
		ReadableByteChannel channel = null;
		StringBuffer sb = null ;
		try {
			channel = Channels.newChannel(input);
			sb = new StringBuffer(input.available()) ;
			ByteBuffer bb = ByteBuffer.allocate(resetBufferSize(input.available())) ;
			while (channel.read(bb)!=-1){
				bb.flip() ;
				bb.limit(bb.remaining()) ;
				sb.append(readLimit(bb.remaining(),bb.array())) ;
			}
			bb.clear() ;
		} finally {
			if (null != channel){
				channel.close() ;
			}
		}
		return sb ;
	}

	/**
	 * 按照实际byte[]大小组装字符串
	 * @param limit
	 * 				实际有效长度.
	 * @param bytes
	 * 				待组装成字符串的byte[]
	 * @return String
	 * 				返回一个装入bytes内容后的String
	 */
	private static String readLimit(int limit, byte[] bytes) {
		return new String(bytes,0 , limit) ;
	}

	/**
	 * 确定缓冲区大小, 目前只是简单的根据输入流的长度
	 * 与类的默认块区大小进行对比, 大于默认块区大小的, 
	 * 以默认的块区大小进行读取/写入操作, 否则以流长度
	 * 作为缓冲区长度进行操作
	 * @param input
	 * 				输入流的长度
	 * @return int 
	 * 				返回确定的缓冲区大小
	 */
	private static int resetBufferSize(int input) {
		return input > BUFFER_MAX_SIZE? BUFFER_MAX_SIZE: input ;
	}

	/**
	 * 测试的操作方法, 生成一个符合规定大小的文件, 等待操作
	 * 本方法不负责该文件的回收, 消毁工作
	 * @param input
	 * @param output
	 * @param bufferLength
	 * @param count
	 * @throws IOException
	 */
	public static void write(InputStream input, OutputStream output
			, int bufferLength , int count) throws IOException {
		WritableByteChannel channel = null;
		ReadableByteChannel read = null;
		try {
			channel = Channels.newChannel(output) ;
			read = Channels.newChannel(input);
			ByteBuffer buffer = ByteBuffer.allocate(bufferLength);
			while (read.read(buffer) != -1) {
				for (int i = 0; i < count; i++){
					buffer.flip();
					channel.write(buffer);
				}
				buffer.clear();
			}
		} finally {
			if (null != read){
				read.close();
			}
			if (null != channel){
				channel.close();
			}
		}
	}
	
	public static void main(String[] args){
	/*	File file = new File("E:/代码\\source\\Jakarta\\commons\\exec\\commons-exec-1.0\\LICENSE.txt");
		System.out.println(file.getParent());
		System.out.println(file.getPath());
		System.out.println(file.getName());*/
		/*String jarFilePath = "classpath:com/sunrise/analysis/utils/resource/classpathjars";
		try {
			FileToolkit.copyFileByList(jarFilePath, "E:\\工作\\资料\\日志分析系统\\项目基线\\后台\\jar包");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		String jarFilePath = "com/sunrise/analysis/utils/resource/classpathjars";
		URL url = ClassUtils.getDefaultClassLoader().getResource(jarFilePath);
		if (url == null){
			System.out.println(false);
		}else {
			System.out.println(true);
		}
	}
	
	public static final String SYS_FILE_SEPARATOR = "/";
	public static final String BAK_END = ".bak";

	public static String cloneFile(File paramFile, String paramString)
			throws IOException {
		if (paramFile.exists()) {
			File localFile = paramFile.getParentFile();
			cloneFiles(localFile.getAbsolutePath(), paramString,
					paramFile.getName());
			return paramString + "/" + paramFile.getName();
		}
		throw new IOException("No assigned file exist");
	}

	public static boolean isExecutableSuffix(String paramString) {
		if (paramString == null)
			return false;
		String[] arrayOfString = { ".exe", ".bat", ".cmd", ".sh", ".com" };
		String str = paramString.toLowerCase();
		for (int i = 0; i < arrayOfString.length; i++)
			if (str.endsWith(arrayOfString[i]))
				return true;
		return false;
	}

	public static List<String> cloneFiles(String paramString1,
			String paramString2, String paramString3) throws IOException {
		return cloneFilterFiles(paramString1, paramString2, paramString3, null);
	}

	public static List<String> cloneFilterFiles(String paramString1,
			String paramString2, String paramString3, FileFilter paramFileFilter)
			throws IOException {
		ArrayList localArrayList = new ArrayList();
		cloneFiles(localArrayList, paramString1, paramString2, paramString3,
				paramFileFilter);
		return localArrayList;
	}

	public static void cloneFilesAndBak(List<String> paramList,
			String paramString1, String paramString2, String paramString3,
			FileFilter paramFileFilter) throws IOException {
		if (paramList == null)
			paramList = new ArrayList();
		cloneFiles(paramList, paramString1, paramString2, paramString3,
				paramFileFilter);
	}

	private static void cloneFiles(List<String> paramList, String paramString1,
			String paramString2, String paramString3, FileFilter paramFileFilter)
			throws IOException {
		if (check(paramString1, paramString2, paramString3))
			cloneFiles(paramList, new File(paramString1 + File.separator
					+ paramString3), paramString2, true, paramFileFilter);
	}

	public static void cloneFileInSameDir(String paramString1,
			String paramString2, String paramString3) throws IOException {
		if (!new File(paramString1).exists()) {
			log.warn("此文件夹不存在: " + paramString1);
			return;
		}
		File localFile = new File(paramString1 + "/" + paramString2);
		if (!localFile.exists()) {
			log.warn("待拷贝文件不存在: " + paramString1 + "/" + paramString2);
			return;
		}
		if ((paramString3 == null) || (paramString3.length() == 0)) {
			log.warn("目的文件名不能为空.");
			return;
		}
		clone2assignName(new ArrayList(), localFile, localFile.getParentFile()
				.getAbsolutePath(), paramString3, false);
	}

	public static void makeBakFile(String paramString) throws IOException {
		File localFile = new File(paramString);
		if (localFile.exists()) {
			String str = localFile.getAbsolutePath() + ".bak";
			cloneIt(localFile, str);
			return;
		}
		log.debug("No source file,no need to bak: "
				+ localFile.getAbsolutePath());
	}

	public static void recoverBak(File paramFile) throws IOException {
		if (!paramFile.exists())
			return;

		if (paramFile.isDirectory()) {
			File[] files = paramFile.listFiles();
			if (files == null)
				return;
			for (int i = 0; i < files.length; i++)
				recoverBak(files[i]);
		} else {
			File file = new File(paramFile.getAbsolutePath() + ".bak");
			if (file.exists()) {
				cloneIt(file, paramFile.getAbsolutePath());
				log.debug("recover bakup file: " + file.getAbsolutePath());
			} else {
				log.debug("No bak file,ignore this file: "
						+ paramFile.getAbsolutePath());
			}
		}
	}

	public static void deleteAllFromDir(String paramString) throws IOException {
		File localFile = new File(paramString);
		if (!localFile.exists()) {
			log.warn("要删除得目录不存在! path = " + paramString);
			return;
		}
		if (!localFile.isDirectory()) {
			localFile.delete();
			return;
		}
		removeFiles(localFile.listFiles());
		localFile.delete();
		log.debug("删除文件完毕.");
	}

	private static void removeFiles(File[] paramArrayOfFile) throws IOException {
		int i = 0;
		int j = paramArrayOfFile.length;
		while (i < j) {
			if (paramArrayOfFile[i].isDirectory())
				removeFiles(paramArrayOfFile[i].listFiles());
			paramArrayOfFile[i].delete();
			i++;
		}
	}

	private static boolean check(String paramString1, String paramString2,
			String paramString3) {
		File localFile1 = new File(paramString1);
		if (!localFile1.exists()) {
			log.warn("The directory of copy from should be exist! path = "
					+ paramString1);
			return false;
		}
		File localFile2 = new File(localFile1.getAbsolutePath()
				+ File.separator + paramString3);
		if (!localFile2.exists()) {
			log.warn("The file you want to copy does not exist! path = "
					+ paramString3);
			return false;
		}
		File localFile3 = new File(paramString2);
		if (!localFile3.exists())
			localFile3.mkdirs();
		return true;
	}

	private static void cloneFiles(List<String> paramList, File paramFile,
			String paramString, boolean paramBoolean, FileFilter paramFileFilter)
			throws IOException {
		String str = null;
		if (paramFile.isDirectory()) {
			str = buildDir(paramString, paramFile.getName());
			File[] files = paramFile.listFiles(paramFileFilter);
			if (files == null)
				return;
			int i = 0;
			int j = files.length;
			while (i < j) {
				cloneFiles(paramList, files[i], str, paramBoolean,
						paramFileFilter);
				i++;
			}
		} else {
			String filePath = paramString + File.separator
					+ paramFile.getName();
			if (paramBoolean) {
				File localFile = new File(filePath);
				if (localFile.exists()) {
					cloneIt(localFile, filePath + ".bak");
					log.debug("Backup this file: "
							+ localFile.getAbsolutePath());
				}
			}
			paramList.add(filePath);
			cloneIt(paramFile, filePath);
			log.debug("Clone file complete: " + filePath);
		}
	}

	private static void clone2assignName(List<String> paramList,
			File paramFile, String paramString1, String paramString2,
			boolean paramBoolean) throws IOException {
		String str = null;
		if (paramFile.isDirectory()) {
			str = buildDir(paramString1, paramString2);
			File[] files = paramFile.listFiles();
			if (files == null)
				return;
			int i = 0;
			int j = files.length;
			while (i < j) {
				cloneFiles(paramList, files[i], str, paramBoolean, null);
				i++;
			}
		} else {
			String filePath = paramString1 + File.separator + paramString2;
			if (paramBoolean) {
				File localFile = new File(filePath);
				if (localFile.exists()) {
					cloneIt(localFile, localFile.getAbsolutePath() + ".bak");
					log.debug("Backup this file: "
							+ localFile.getAbsolutePath());
				}
			}
			paramList.add(filePath);
			cloneIt(paramFile, filePath);
			log.debug("Clone file complete: " + filePath);
		}
	}

	private static void cloneIt(File paramFile, String paramString)
			throws IOException {
		doCloneWork(new FileInputStream(paramFile), new FileOutputStream(
				paramString));
	}

	public static void doCloneWork(FileInputStream paramFileInputStream,
			FileOutputStream paramFileOutputStream) throws IOException {
		if ((paramFileInputStream == null) || (paramFileOutputStream == null))
			return;
		try {
			write(paramFileInputStream, paramFileOutputStream);
		} finally {
			paramFileInputStream.close();
			paramFileOutputStream.close();
		}
	}

	public static void copyFileFromWebUrl(URL paramURL, String paramString1,
			String paramString2) throws IOException {
		if (paramURL == null)
			return;
		String str1 = paramString1 + "/";
		File localFile = new File(str1);
		if (!localFile.exists())
			localFile.mkdirs();
		String str2;
		if ((paramString2 == null) || (paramString2.length() == 0))
			str2 = paramURL.getFile().substring(
					paramURL.getFile().lastIndexOf("/") + 1);
		else
			str2 = paramString2;
		writeNotFile(paramURL.openStream(), new FileOutputStream(str1
				+ str2));
	}

	public static String fileContent(File paramFile) throws Exception {
		DataInputStream localDataInputStream = null;
		try {
			localDataInputStream = new DataInputStream(new BufferedInputStream(
					new FileInputStream(paramFile)));
			String str = read(localDataInputStream).toString();
			return str;
		} catch (Exception e) {
			throw e;
		} finally {
			localDataInputStream.close();
		}
	}

	private static String buildDir(String paramString1, String paramString2) {
		File localFile = new File(paramString1 + File.separator + paramString2);
		localFile.mkdir();
		return localFile.getAbsolutePath();
	}

	public static void write2ServletOutput(File paramFile,
			OutputStream paramOutputStream) throws Exception {
		FileInputStream localFileInputStream = null;
		try {
			localFileInputStream = new FileInputStream(paramFile);
			byte[] arrayOfByte = new byte[5120];
			int i = 0;
			while ((i = localFileInputStream.read(arrayOfByte)) != -1)
				paramOutputStream.write(arrayOfByte, 0, i);
			paramOutputStream.flush();
			paramOutputStream.close();
		} catch (Exception localException2) {
			throw localException2;
		} finally {
			try {
				if (localFileInputStream != null)
					localFileInputStream.close();
			} catch (Exception localException3) {
				localException3.printStackTrace();
				throw new RuntimeException(localException3);
			}
		}
	}
	
	public static String getTmpDir(){
		return System.getProperty("java.io.tmpdir");
	}
	
	public static void copyFileToTmpDir(String resFilePath, String tmpDir) throws Exception {
		copyFile2Dir(resFilePath, tmpDir);
	}
	
	public static void copyFileToTmpDir(String resFilePath) throws Exception{
		copyFile2Dir(resFilePath, getTmpDir());
	}
	
	public static String getLineSeperator(){
		return (String) java.security.AccessController.doPrivileged(
	               new sun.security.action.GetPropertyAction("line.separator"));
	}
}
