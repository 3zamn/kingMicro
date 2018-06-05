
package com.king.common.utils.security.coder;

import java.security.Security;

/**
 * 
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年6月1日
 */
public abstract class SecurityCoder {
    private static Byte ADDFLAG = 0;
    static {
        if (ADDFLAG == 0) {
            // 加入BouncyCastleProvider支持
            Security.addProvider(new BouncyCastleProvider());
            ADDFLAG = 1;
        }
    }
}
