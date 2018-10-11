package com.king.common.utils.ftp;

import java.util.ResourceBundle;

/**
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年4月20日
 */
public class FtpInfo {

    public String ftpServer; // ftp服务器ip

    public int ftpPort; // ftp服务器端口

    public String ftpUser; // ftp登陆用户名

    public String ftpPwd; // ftp登陆密码

    public String curSerPath; // ftp服务器当前目录

    public int timeout; // 超时时长

    private static FtpInfo info = null;
    
    private static ResourceBundle rs = null;

    public static FtpInfo getInstance() throws Exception {
        if (info == null) {
            rs = ResourceBundle.getBundle("sysinfo");
            info = new FtpInfo();
            info.ftpServer=rs.getString("ftp.address");
            info.ftpPort=Integer.parseInt(rs.getString("ftp.port"));
            info.ftpUser=rs.getString("ftp.user");
            info.ftpPwd=rs.getString("ftp.password");
            info.curSerPath=rs.getString("ftp.workdir");
            info.timeout = 3000;
        }

        return info;
    }
}