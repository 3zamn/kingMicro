package com.king.common.utils.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
 
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.File;
 
/**
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年4月20日
 */
public class FtpDownload {
    public static void main(String[] agrs) {
        try {
            int blockCount = 5;//分5个线程下载
            String ip = "127.0.0.1";
            int port = 21;
            String username = "cytest";
            String password = "cytest";
            String remotePath = "/home/";
            String remoteFileName = "test.tar";
            String localPath = "D:/test.tar";
            FtpDownload ftpDownload = new FtpDownload(ip, port, username, password, remotePath, remoteFileName, localPath);
            long size = ftpDownload.size();//获得远程文件的大小
            if (size == 0) {
                return;
            }
            System.out.println("文件大小为 " + size + " B");
            List blockList = ftpDownload.buildBlock(size, blockCount); //创建分块信息，5个线程分成5个块
            if (blockList != null && blockList.size() > 0) {
                for (int i = 0; i < blockList.size(); i++) {
                    FtpDownloadBlockInfo blockInfo = (FtpDownloadBlockInfo) blockList.get(i);
                    FtpDownloadThread ftpDownloadThread = new FtpDownloadThread(blockInfo);
                    ftpDownloadThread.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String ip;//ip
    private int port;//端口
    private String username;
    private String password;
    private String remotePath;
    private String remoteFileName;
    private String localPath;
    public FtpDownload(String ip, int port, String username, String password, String remotePath, String remoteFileName, String localPath) {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
        this.remotePath = remotePath;
        this.remoteFileName = remoteFileName;
        this.localPath = localPath;
    }
    private List buildBlock(long size, int blockCount) {
        long blockSize = size / blockCount;
        long lastSize = size % blockCount;
        List blockList = new ArrayList();
        for (int i = 0; i < blockCount; i++) {
            long beginPoint = i * blockSize;
            long endPoint = i * blockSize + blockSize;
            if (blockCount == (i + 1)) {
                endPoint = endPoint + lastSize;
                blockSize = blockSize + lastSize;
            }
            FtpDownloadBlockInfo blockInfo = new FtpDownloadBlockInfo(blockSize, beginPoint, localPath, ip,
                    remotePath + remoteFileName, username, password, port);
            blockList.add(blockInfo);
        }
        return blockList;
    }
    private long size() {
        long size = 0;
        FTPClient ftpClient = null;
        try {
            ftpClient = new FTPClient();
            ftpClient.connect(ip, port);
            ftpClient.login(username, password);
            FTPFile[] files = ftpClient.listFiles(remotePath);
            for (int i = 0; i < files.length; i++) {
                FTPFile file = files[i];
                String fileName = file.getName();
                if (fileName.equals(remoteFileName)) {
                    size = file.getSize();
                    return size;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return size;
    }
}
class FtpDownloadBlockInfo {
    private long blockSize;
    private long beginPoint;
    private String localPath;
    private String ip;
    private String path;
    private String username;
    private String password;
    private int port;
    public FtpDownloadBlockInfo(long blockSize, long beginPoint, String localPath, String ip, String path,
                                String username, String password, int port) {
        this.blockSize = blockSize;
        this.beginPoint = beginPoint;
        this.localPath = localPath;
        this.ip = ip;
        this.path = path;
        this.username = username;
        this.password = password;
        this.port = port;
    }
    public long getBlockSize() {
        return blockSize;
    }
    public long getBeginPoint() {
        return beginPoint;
    }
    public String getLocalPath() {
        return localPath;
    }
    public String getIp() {
        return ip;
    }
    public String getPath() {
        return path;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public int getPort() {
        return port;
    }
}
class FtpDownloadThread extends Thread {
    private FtpDownloadBlockInfo blockInfo;
    public FtpDownloadThread(FtpDownloadBlockInfo blockInfo) {
        this.blockInfo = blockInfo;
    }
    public void run() {
        System.out.println("begin download beginPoint:" + blockInfo.getBeginPoint() + " blockSize:" + blockInfo.getBlockSize());
        long start = System.currentTimeMillis();
        FTPClient ftpClient = null;
        InputStream inputStream = null;
        RandomAccessFile raf = null;
        try {
            ftpClient = new FTPClient();
            ftpClient.connect(blockInfo.getIp(), blockInfo.getPort());
            ftpClient.login(blockInfo.getUsername(), blockInfo.getPassword());
            ftpClient.changeWorkingDirectory(blockInfo.getPath());
            ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
            ftpClient.rest(String.valueOf(blockInfo.getBeginPoint()));//设置开始点偏移量
            inputStream = ftpClient.retrieveFileStream(blockInfo.getPath());//此处由于设置了偏移量了 ，inputStream变成了null造成异常
            raf = new RandomAccessFile(new File(blockInfo.getLocalPath()), "rw");
            raf.seek(blockInfo.getBeginPoint());
            this.downloadStream(inputStream,raf);
            long end = System.currentTimeMillis();
            System.out.println("end download beginPoint:"+blockInfo.getBeginPoint()+" blockSize:"+blockInfo.getBlockSize()+"->"+((end-start)/1000)+"s");
        } catch (IOException e) {
            System.out.println("end download failed beginPoint:"+blockInfo.getBeginPoint()+" blockSize:"+blockInfo.getBlockSize());
            e.printStackTrace();
        } finally {
            try {
                raf.close();
                ftpClient.logout();
                ftpClient.disconnect();
                ftpClient = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void downloadStream(InputStream inputStream, RandomAccessFile raf) throws IOException {
        long bloackSize = blockInfo.getBlockSize();
        byte[] buffer = new byte[1024];
        long bytesRead;
        long downloaded = 0;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            downloaded = downloaded + bytesRead;
            if (downloaded > bloackSize) {
                bytesRead = bytesRead - (downloaded - bloackSize);
            }
            raf.write(buffer, 0, (int) bytesRead);
            if (downloaded > bloackSize) {
                break;
            }
        }
        raf.close();
        inputStream.close();
    }
}