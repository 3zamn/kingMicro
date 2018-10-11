package com.king.common.utils.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

/**
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年4月20日
 */
public class FtpAccess {
	private static final Logger log = Logger.getLogger(FtpAccess.class);
	private FtpInfo ftpinfo;
	public FTPClient ftp = null;
	public final static String PATH_SPLIT_STR = File.separator;
	public final static char PATH_SPLIT_CH = File.separatorChar;

	public FtpAccess(FtpInfo ftpInfo) throws Exception {

		this.ftpinfo = ftpInfo;
		try {
			ftp = new FTPClient();
			ftp.setControlEncoding("GBK");
			if (log.isInfoEnabled()) {
				log.info("ftpServer=" + ftpInfo.ftpServer + " ftpPort="
						+ ftpInfo.ftpPort + " ftpUser=" + ftpInfo.ftpUser
						+ " ftpPwd=" + ftpInfo.ftpPwd);
			}
			try {// 为防止配置的ftp连接出错，尝试重新修正ftp地址再次连接
				ftp.connect(ftpInfo.ftpServer, ftpInfo.ftpPort);
				if (!ftp.login(ftpInfo.ftpUser, ftpInfo.ftpPwd)) {
					throw new Exception("User or password error!");
				}
			} catch (Exception e) {
				// 修正ftp连接地址,增强容错性
				if (!"127.0.0.1".equals(ftpInfo.ftpServer)) {
					ftpInfo.ftpServer = "127.0.0.1";
					ftpInfo.ftpPort = 21;
					ftpInfo.ftpUser = "test";
					ftpInfo.ftpPwd = "test";
					ftpInfo.curSerPath = "orison";
					// 再次连接
					ftp.connect(ftpInfo.ftpServer, ftpInfo.ftpPort);
					if (!ftp.login(ftpInfo.ftpUser, ftpInfo.ftpPwd)) {
						throw new Exception("User or password error!");
					}
				} else {
					throw e;
				}
			}
			String curpath = ftpInfo.curSerPath;
			if (curpath != null && !"".equals(curpath)) {
				ftp.changeWorkingDirectory(curpath);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("[FTP-MSG]\t" + ftp.getReplyCode()
					+ " connect ftp error!:" + ex.getMessage());
		}
	}

	/**
	 * 上传文件到ftp服务器
	 * 
	 * @param localFileName
	 *            本地文件名称
	 * @param remotePath
	 *            FTP上的路径，注意不能带名称
	 * @param genFileName
	 *            是否产生唯一的文件名称。规则：原文件名称+序列号
	 * @return 成功返回变更后的名称，不成功返回null
	 */
	public String uploadFile(String localFileName, String remotePath,
			boolean genFileName) {
		File file = new File(localFileName);
		String result = null;
		if (file.exists()) {
			try {
				String remoteFilePath = remotePath;
				if (remotePath == null)
					remotePath = "";
				String filename = file.getName();
				ftp.makeDirectory(remotePath);
				if (genFileName) {
					filename = changeFilename(filename, String.valueOf(Sequence
							.getSequence()));
				}
				if (!remotePath.equals("")) {
					remoteFilePath = remotePath + "/" + filename;
				} else {
					remoteFilePath = filename;
				}
				FileInputStream fis = new FileInputStream(file);
				boolean rlt = ftp.storeFile(remoteFilePath, fis);
				fis.close();
				if (rlt) {
					result = remoteFilePath;
				} else {
					log.error("upload file failed：" + ftp.getReplyString()
							+ ":" + ftp.getReplyCode());
				}
			} catch (Exception e) {
				log.error("upload file failed：" + ftp.getReplyString() + ":"
						+ ftp.getReplyCode());
				log.error(e.getMessage());
			}
		}
		return result;
	}

	/**
	 * 将文件眳 filename.xxx 改名为 filename_extname.xxx
	 * 
	 * @param filename
	 *            原文件名称
	 * @param extname
	 *            文件眳加上的后缀名
	 * @return 新文件名称
	 */
	public String changeFilename(String filename, String extname) {
		if (filename == null || filename.equals(""))
			return filename;
		int pos = filename.indexOf(".");
		if (pos < 0)
			return filename + "_" + extname;
		else {
			String name = filename.substring(0, pos);
			return name + "_" + extname + filename.substring(pos);
		}
	}

	/**
	 * 删FTP上的文件
	 * 
	 * @param filename
	 *            文件名称
	 * @return 是否成功
	 * @throws IOException
	 *             异常
	 */
	public boolean deleteFilename(String filename) throws IOException {
		if (filename == null || filename.equals(""))
			return true;
		return ftp.deleteFile(filename);
	}

	public List downloadFile(String localDir, String remoteDir, String regx)
			throws Exception {
		List list = new ArrayList();
		if (StringUtils.isEmpty(remoteDir) || StringUtils.isEmpty(localDir)) {
			return null;
		}
		if (!remoteDir.endsWith("/")) {
			remoteDir += "/";
		}
		if (!localDir.endsWith("/")) {
			localDir += "/";
		}
		PatternCompiler compiler = new Perl5Compiler();
		Pattern pattern = compiler.compile(regx);
		PatternMatcher matcher = new Perl5Matcher();
		String[] fileList = this.getFileList(remoteDir);
		if (fileList != null && fileList.length > 0) {
			for (int i = 0; i < fileList.length; i++) {
				String remoteFileName = fileList[i];
				if (matcher.contains(remoteFileName, pattern)) {
					String localFilePath = downloadFile(localDir, remoteDir
							+ remoteFileName);
//					if (deleteFilename(remoteDir + remoteFileName)){
//						log.info("delete file[" + remoteDir + remoteFileName + "] success!");
//					}
					if (localFilePath != null){
						list.add(localFilePath);
					}
				}
			}
		}
		return list;
	}

	/**
	 * 下载文件到本地目录
	 * 
	 * @param localPath
	 *            下载到本地的路径
	 * @param remoteFilePath
	 *            FTP上的远程文件名称
	 * @return 下载后的文件路径
	 */
	public String downloadFile(String localPath, String remoteFilePath) {
		if (localPath == null || localPath.equals("") || remoteFilePath == null
				|| remoteFilePath.equals(""))
			return null;
		String result = null;

		String filename = getFilenameFromPath(remoteFilePath);
		File localDir = new File(localPath);
		if (!localDir.exists()){
			localDir.mkdir();
		}
		if (filename != null) {
			if (!localPath.endsWith("/")){
				localPath = localPath + "/" + filename;
			}else {
				localPath = localPath + filename;
			}
		}
			
		File file = new File(localPath);
		if (file.exists())
			file.delete();
		try {
			FileOutputStream fio = new FileOutputStream(file);
			log.info("begin downloads file [" + remoteFilePath + "] to [" + localPath + "]");
			boolean rlt = ftp.retrieveFile(remoteFilePath, fio);
			log.info("end downlaos file [" + remoteFilePath + "]" + "] to [" + localPath + "]");
			if (rlt) {
				result = localPath;
			} else {
				log.error("download file failed" + ftp.getReplyString());
			}
			fio.close();
		} catch (IOException e) {
			log.error("download file failed：" + ftp.getReplyString() + ":"
					+ ftp.getReplyCode());
			e.printStackTrace();
		}
		return result;
	}

	private String getFilenameFromPath(String remoteFilePath) {
		int pos = remoteFilePath.lastIndexOf("/");
		if (pos >= 0) {
			return remoteFilePath.substring(pos + 1);
		} else {
			return remoteFilePath;
		}
	}

	public String getFileName(String local) {
		int last = local.lastIndexOf("\\");
		String filename = local.substring(last + 1, local.length());
		return filename;
	}

	/**
	 * 下载文件到本地目录(支持多文件下载)
	 * 
	 * @return
	 */
	public String[] getFiles(List filelist, String destpath, String regx[])
			throws Exception {
		if ((filelist == null) || (filelist.size() == 0)) {
			return new String[] { "[FTP-MSG]\t下载文件列表为空" };
		}
		if (destpath == null || "".equals(destpath)) {
			throw new Exception("[FTP-MSG]\t请输入文件保存路径");
		}

		int allCount = filelist.size();
		int errCount = 0;
		int susCount = 0;

		String dealmsg[] = new String[allCount];
		FileOutputStream outStream = null;
		File file = new File(destpath);
		if (!file.exists()) {
			file.mkdir();
		}
		if (!file.isDirectory()) {
			throw new Exception("[FTP-MSG]\t保存路径必须是目录,而不是文件或者非法路径名");
		}

		String filename = null;
		if (destpath.charAt(destpath.length() - 1) != PATH_SPLIT_CH) {
			destpath += PATH_SPLIT_STR;
		}
		for (int i = 0; i < filelist.size(); i++) {
			try {
				filename = (String) filelist.get(i);
				outStream = new FileOutputStream(destpath + filename);
				ftp.retrieveFile(filename, outStream);

				susCount++;
				dealmsg[i] = "[FTP-MSG]\t成功下载名为[" + filename + "]的文件";
				log.info("[FTP-MSG]\t成功下载名为[" + filename + "]的文件");
			} catch (Exception ex) {
				log.error("ftp error code：" + ftp.getReplyString() + ":"
						+ ftp.getReplyCode());
				ex.printStackTrace();
				errCount++;
				dealmsg[i] = "[FTP-MSG]\t文件[" + filename + "]传送失败:"
						+ ex.getMessage();
				log.info("[FTP-MSG]\t文件[" + filename + "]传送失败:"
						+ ex.getMessage());
			} finally {
				outStream.close();
			}
		}

		if (ftp != null) {
			ftp.logout();
			ftp.disconnect();
		}

		return dealmsg;
	}

	/**
	 * 获取指定目录的文件列表
	 */
	public String[] getFileList(String destpath) throws Exception {
		if (destpath == null || "".equals(destpath)) {
			throw new Exception("[FTP-MSG]\t要显示列表的目录为空");
		}

		if (ftp != null && ftp.isConnected()) {
			ftp.changeWorkingDirectory(destpath);
			return ftp.listNames();
		} else {
			throw new Exception("[FTP-MSG]\tftp连接超时或者被中断");
		}
	}

	public static void main(String[] args) {
		FtpInfo fi = new FtpInfo();
		fi.ftpServer = "127.0.0.1";
		fi.ftpPort = 21;
		fi.curSerPath = "/home/cytest/orison/analysis/ftp/logfiles";
		fi.ftpUser = "cytest";
		fi.ftpPwd = "cytest";
		try {
			FtpAccess fa = new FtpAccess(fi);
			int reply = fa.ftp.getReplyCode();
			/*String result = fa.uploadFile("e:\\sqlnet.log", ".", true);

			if (result != null)
				System.out.println("success :" + result);
			else
				System.out.println("failed :" + result);

			System.out.println("ftp reply = " + reply);

			String rlt = fa.downloadFile("c:/temp/", result);
			if (rlt != null)
				System.out.println("download success");
			else
				System.out.println("download failed");*/
			
			fa.downloadFile("c:/temp2", "/home/cytest/orison/analysis/ftp/logfiles", "^AirCZApp_gz.*");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}