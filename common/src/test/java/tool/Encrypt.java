package tool;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import org.junit.Test;

import com.king.common.utils.security.SecurityUtil;
import com.king.common.utils.security.crypto.Sha256Hash;

/**
 * Sha256加盐加密
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年6月28日
 */
public class Encrypt {
	private static final String salt="YzcmCZNvbXocrsz9dm8e";//盐值
	private static String Algorithm = "DES"; //定义 加密算法,可用 DES,DESede,Blowfish
	String originalValue="";
	public static void main(String[] args) {
		String originalValue="javen";
		System.out.println(originalValue+",加密后的密文："+new Sha256Hash(originalValue, salt).toHex());
	}
	
	@Test
	public void encode(){//DES加密
		 originalValue="javen";
		String key="YzcmCZNvbXocrsz9dm8e";
		try {

			System.out.println(originalValue+" 加密后密文 "+SecurityUtil.encryptDes(originalValue, key.getBytes()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void decode(){//DES解密
		originalValue="LUCFTMdcrgI=";
		String key="YzcmCZNvbXocrsz9dm8e";
		try {
			System.out.println(originalValue+" 解密后明文 "+SecurityUtil.decryptDes(originalValue, key.getBytes()));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
