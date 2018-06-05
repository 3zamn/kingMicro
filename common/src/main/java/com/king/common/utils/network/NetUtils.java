package com.king.common.utils.network;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.king.common.utils.pattern.StringToolkit;


/**
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年4月20日
 */
public class NetUtils {

	/**
	 * 获取本机所有IP
	 */
	public static String[] getAllLocalHostIP() {
		List<String> res = new ArrayList<String>();
		Enumeration netInterfaces;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) netInterfaces
						.nextElement();
				// System.out.println("---Name---:" + ni.getName());
				Enumeration nii = ni.getInetAddresses();
				while (nii.hasMoreElements()) {
					ip = (InetAddress) nii.nextElement();
					if (ip != null && ip instanceof Inet4Address
							&& ip.getHostAddress().indexOf(":") == -1) {
						res.add(ip.getHostAddress());
//						System.out.println("本机的ip=" + ip.getHostAddress());
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return (String[]) res.toArray(new String[0]);
	}

	public static String getLinuxLocalIP() {
		String ip = "";
		try {
			Enumeration<?> e1 = (Enumeration<?>) NetworkInterface
					.getNetworkInterfaces();
			while (e1.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) e1.nextElement();
				System.out.println("getLocalIP--nic.getDisplayName ():"
						+ ni.getDisplayName());
				System.out
						.println("getLocalIP--nic.getName ():" + ni.getName());
				if (!ni.getName().equals("eth0")) {
					continue;
				} else {
					Enumeration<?> e2 = ni.getInetAddresses();
					while (e2.hasMoreElements()) {
						InetAddress ia = (InetAddress) e2.nextElement();
						if (ia instanceof Inet6Address)
							continue;
						ip = ia.getHostAddress();
						System.out.println("IP：" + ip);
					}
					break;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return ip;
	}

	public static String getWinLocalIP() {
		String ip = "";
		try {
			Enumeration<?> e1 = (Enumeration<?>) NetworkInterface
					.getNetworkInterfaces();
			while (e1.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) e1.nextElement();
				System.out.println("getWinLocalIP--nic.getDisplayName ():"
						+ ni.getDisplayName());
				System.out.println("getWinLocalIP--nic.getName ():"
						+ ni.getName());
				Enumeration<?> e2 = ni.getInetAddresses();
				while (e2.hasMoreElements()) {
					InetAddress ia = (InetAddress) e2.nextElement();
					ip = ia.getHostAddress();
					System.out.println("IP：" + ip);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return ip;
	}

	// 获取所有网卡的MAC地址
	public static List<String> getAllMac() {
		List<String> list = new ArrayList<String>();
		try {
			Enumeration<NetworkInterface> e = NetworkInterface
					.getNetworkInterfaces();// 返回所有网络接口的一个枚举实例
			while (e.hasMoreElements()) {
				NetworkInterface network = e.nextElement();// 获得当前网络接口
				if (network != null) {
					if (network.getHardwareAddress() != null) {
						// 获得MAC地址
						// 结果是一个byte数组，每项是一个byte，需要通过parseByte方法转换成常见的十六进制表示
						byte[] addres = network.getHardwareAddress();
						StringBuffer sb = new StringBuffer();
						if (addres != null && addres.length > 1) {
							sb.append(parseByte(addres[0])).append(":")
									.append(parseByte(addres[1])).append(":")
									.append(parseByte(addres[2])).append(":")
									.append(parseByte(addres[3])).append(":")
									.append(parseByte(addres[4])).append(":")
									.append(parseByte(addres[5]));
							list.add(sb.toString());
						}
					}
				} else {
					System.out.println("获取MAC地址发生异常");
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return list;
	}

	// 格式化二进制
	private static String parseByte(byte b) {
		int intValue = 0;
		if (b >= 0) {
			intValue = b;
		} else {
			intValue = 256 + b;
		}
		return Integer.toHexString(intValue);
	}

	// 获取MAC地址的方法
	private static String getMACAddress(InetAddress ia) throws Exception {
		// 获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
		byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();

		// 下面代码是把mac地址拼装成String
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < mac.length; i++) {
			if (i != 0) {
				sb.append("-");
			}
			// mac[i] & 0xFF 是为了把byte转化为正整数
			String s = Integer.toHexString(mac[i] & 0xFF);
			sb.append(s.length() == 1 ? 0 + s : s);
		}

		// 把字符串所有小写字母改为大写成为正规的mac地址并返回
		return sb.toString().toUpperCase();
	}
	
	public static String getLocalhostMacAddress() {
		String mac = null;
		try {
			Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) netInterfaces
						.nextElement();
				// System.out.println("---Name---:" + ni.getName());
				Enumeration nii = ni.getInetAddresses();
				while (nii.hasMoreElements()) {
					InetAddress ip = (InetAddress) nii.nextElement();
					if (ip != null && ip instanceof Inet4Address
							&& ip.getHostAddress().indexOf(":") == -1) {
						if (!ip.getHostAddress().equals("127.0.0.1")){
							mac = getMACAddress(ip); 
							if (StringToolkit.isNotEmpty(mac))
								break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mac;
	}
	/** 获取网卡序列号 */
	public static final String getDUID() {
		String address = "";
		String command = "cmd.exe /c ipconfig /all";
		try {
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.indexOf("DUID") > 0) {
					int index = line.indexOf(":");
					index += 2;
					address = line.substring(index);
					break;
				}
			}
			br.close();
		} catch (IOException e) {
		}
		return address;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		getAllLocalHostIP();
		// getLocalIP();
		// getWinLocalIP();
		// getMacAddress();
//		System.out.println(getAllMac());
		System.out.println(getLocalhostMacAddress());
	}
}
