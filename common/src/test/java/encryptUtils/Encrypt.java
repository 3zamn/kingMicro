package encryptUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * 反转公私密钥rsa加密
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年7月5日
 */
public class Encrypt {

	private static final String DEFAULT_PRIVATE_KEY_STRING = "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAsa2crSsRhUpi7I5CTiep8GS0CpI2Q2o7dgpIMoFdYIzGGAGcPVzWMUncvdkGtYVykZ32NBUvww7tlBEUlVz/QQIDAQABAkAFUmjt+utEbcz2vvAXfTKm461M38JGVKlBSN+Ou7c7TA9T6gLZ2CQI7p2CpwI8ggW53pxyDF3jkqJPqkO1UwLtAiEA6IzsQXZnSjVuRJc60ekwI2uTaqpLfHPMsRw10dVBY5cCIQDDmDX9B4O2W8G3BhPaWtcrbfwomXY+mSJuPy6tFyEu5wIhAN6LReibo9sraLvxMZQCIVQ0NZ4lHBocQ23WflaxS2M/AiA+fsfhTSFchvziSeLWBehyS/Yh0sbhQXSq3S1AyP8lSQIhAJ3JB7qpLIxj1yfWhEFw+46K7z2mDs9ifR9jaDbj2t42";
	public static final String DEFAULT_PUBLIC_KEY_STRING = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALGtnK0rEYVKYuyOQk4nqfBktAqSNkNqO3YKSDKBXWCMxhgBnD1c1jFJ3L3ZBrWFcpGd9jQVL8MO7ZQRFJVc/0ECAwEAAQ==";

	public static void main(String[] args) throws Exception {
		/*  String password = args[0];
        String[] arr = genKeyPair(512);
        System.out.println("privateKey:" + arr[0]);
        System.out.println("publicKey:" + arr[1]);
        System.out.println("password:" + encrypt(arr[0], password));*/
    	/*String key="MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIV0h9JHs9PfwQ92FKoGbSoIZfRkX3fDJtReDErYyFDsrLPaycG1e+cO/0yARBGVxEkCAxO0V2K+CpI76xoH/6cCAwEAAQ==";
		String key= args[0];
		PublicKey publicKey = getPublicKey(key);
		String cipherText=args[1];
		String cipherText="ByyBUSo2FkBZiPPhacmTA3S7iGqzUQjjOuugQ+KEb4+kptdG2yF80q6uDlI0tiAQwcYK1NTOloTuOYWDxcMsgw==";
		System.out.println("password:"+decrypt(publicKey, cipherText));*/
		String password = "123456";
		String[] arr = genKeyPair(512);
		// System.out.println("privateKey:" + arr[0]);
		// System.out.println("publicKey:" + arr[1]);
		System.out.println("password:" + encrypt((String) null, password));
		// String
		// key="MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIV0h9JHs9PfwQ92FKoGbSoIZfRkX3fDJtReDErYyFDsrLPaycG1e+cO/0yARBGVxEkCAxO0V2K+CpI76xoH/6cCAwEAAQ==";
		// String key= args[0];
		PublicKey publicKey = getPublicKey(DEFAULT_PUBLIC_KEY_STRING);
		// String cipherText=args[1];
		String cipherText = "fnLjx6AccckAlAHDMeA52R3Np18CByes1PWSQmTWPvoN7dEooRJwKiRVuJl2ifCOUG3iPdY2ADfLbj19kccuHg==";
		System.out.println("password:" + decrypt(publicKey, cipherText));
	}

	public static String decrypt(String cipherText) throws Exception {
		return decrypt((String) null, cipherText);
	}

	public static String decrypt(String publicKeyText, String cipherText)
			throws Exception {
		PublicKey publicKey = getPublicKey(publicKeyText);

		return decrypt(publicKey, cipherText);
	}

	public static PublicKey getPublicKeyByX509(String x509File) {
		if (x509File == null || x509File.length() == 0) {
			return Encrypt.getPublicKey(null);
		}

		FileInputStream in = null;
		try {
			in = new FileInputStream(x509File);

			CertificateFactory factory = CertificateFactory
					.getInstance("X.509");
			Certificate cer = factory.generateCertificate(in);
			return cer.getPublicKey();
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to get public key", e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static PublicKey getPublicKey(String publicKeyText) {
		if (publicKeyText == null || publicKeyText.length() == 0) {
			publicKeyText = Encrypt.DEFAULT_PUBLIC_KEY_STRING;
		}

		try {
			byte[] publicKeyBytes = Base64.base64ToByteArray(publicKeyText);
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(
					publicKeyBytes);

			KeyFactory keyFactory = KeyFactory.getInstance("RSA", "SunRsaSign");
			return keyFactory.generatePublic(x509KeySpec);
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to get public key", e);
		}
	}

	public static PublicKey getPublicKeyByPublicKeyFile(String publicKeyFile) {
		if (publicKeyFile == null || publicKeyFile.length() == 0) {
			return Encrypt.getPublicKey(null);
		}

		FileInputStream in = null;
		try {
			in = new FileInputStream(publicKeyFile);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int len = 0;
			byte[] b = new byte[512 / 8];
			while ((len = in.read(b)) != -1) {
				out.write(b, 0, len);
			}

			byte[] publicKeyBytes = out.toByteArray();
			X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
			KeyFactory factory = KeyFactory.getInstance("RSA", "SunRsaSign");
			return factory.generatePublic(spec);
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to get public key", e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static String decrypt(PublicKey publicKey, String cipherText)
			throws Exception {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		try {
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
		} catch (InvalidKeyException e) {
            // 因为 IBM JDK 不支持私钥加密, 公钥解密, 所以要反转公私钥
            // 也就是说对于解密, 可以通过公钥的参数伪造一个私钥对象欺骗 IBM JDK
            RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
            RSAPrivateKeySpec spec = new RSAPrivateKeySpec(rsaPublicKey.getModulus(), rsaPublicKey.getPublicExponent());
            Key fakePrivateKey = KeyFactory.getInstance("RSA").generatePrivate(spec);
            cipher = Cipher.getInstance("RSA"); //It is a stateful object. so we need to get new one.
            cipher.init(Cipher.DECRYPT_MODE, fakePrivateKey);
		}
		
		if (cipherText == null || cipherText.length() == 0) {
			return cipherText;
		}

		byte[] cipherBytes = Base64.base64ToByteArray(cipherText);
		byte[] plainBytes = cipher.doFinal(cipherBytes);

		return new String(plainBytes);
	}

	public static String encrypt(String plainText) throws Exception {
		return encrypt((String) null, plainText);
	}

	public static String encrypt(String key, String plainText) throws Exception {
		if (key == null) {
			key = DEFAULT_PRIVATE_KEY_STRING;
		}

		byte[] keyBytes = Base64.base64ToByteArray(key);
		return encrypt(keyBytes, plainText);
	}

	public static String encrypt(byte[] keyBytes, String plainText)
			throws Exception {
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory factory = KeyFactory.getInstance("RSA", "SunRsaSign");
		PrivateKey privateKey = factory.generatePrivate(spec);
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        try {
		    cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        } catch (InvalidKeyException e) {
            //For IBM JDK, 原因请看解密方法中的说明
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;
            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(rsaPrivateKey.getModulus(), rsaPrivateKey.getPrivateExponent());
            Key fakePublicKey = KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, fakePublicKey);
        }

		byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
		String encryptedString = Base64.byteArrayToBase64(encryptedBytes);

		return encryptedString;
	}

	public static byte[][] genKeyPairBytes(int keySize)
			throws NoSuchAlgorithmException, NoSuchProviderException {
		byte[][] keyPairBytes = new byte[2][];

		KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA", "SunRsaSign");
		gen.initialize(keySize, new SecureRandom());
		KeyPair pair = gen.generateKeyPair();

		keyPairBytes[0] = pair.getPrivate().getEncoded();
		keyPairBytes[1] = pair.getPublic().getEncoded();

		return keyPairBytes;
	}

	public static String[] genKeyPair(int keySize)
			throws NoSuchAlgorithmException, NoSuchProviderException {
		byte[][] keyPairBytes = genKeyPairBytes(keySize);
		String[] keyPairs = new String[2];

		keyPairs[0] = Base64.byteArrayToBase64(keyPairBytes[0]);
		keyPairs[1] = Base64.byteArrayToBase64(keyPairBytes[1]);

		return keyPairs;
	}

}
