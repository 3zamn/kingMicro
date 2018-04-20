package com.king.common.utils.ftp;

import java.net.InetAddress;

/**
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年4月20日
 */
public class Sequence {
    private static Sequence me;

    private static final int IP;
    static {
        int ipadd;
        try {
            String hostadd = InetAddress.getLocalHost().getHostAddress();
            ipadd = Integer.parseInt(hostadd.substring(hostadd.length() - 1));
        }
        catch (Exception e) {
            ipadd = 0;
        }
        IP = ipadd;
    }

    private static final int base = 10;
    private static long millis, old;

    private Sequence()
        throws Exception {
    }


    public static synchronized Sequence getInstance()
        throws Exception {
        if (me == null) {
            me = new Sequence();
        }
        return me;
    }

    public static synchronized long getSequence()
        throws Exception {
        long result = System.currentTimeMillis();
        if (result == millis) {
            old++;
            if (old >= (millis + 1) * base) {
                throw new Exception("�Ѵﵽ�������ڵ�������к�");
            }
            result = old;
        }
        else {
            millis = result;
            result *= base;
            old = result;
        }
        return result * 10 + IP;
    }
 
}