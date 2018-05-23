package com.king.oss;

/**
 * 用dubbo的main方法方式启动容器
 * @author King chen
 * @date 2017年12月15日
 */
public class App {
	public static void main(String[] args) throws Exception {
		com.alibaba.dubbo.container.Main.main(args);
	}

}
