package tool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import junit.framework.Test;
import tools.mybatis.gen.GenMybatisFiles;



/**
 * 生产数据操作层代码
 *
 */
public class GenMain {
	
	public static void main(String[] args) {
		String configFile = "/generatorConfig.xml";
		try {
			String[] tableNames = new String[] {"sys_user_role"};
			GenMybatisFiles.gen(configFile, tableNames);
		} catch (Exception e) {
			e.printStackTrace();
		}
		  ExecutorService fixedThreadPool = Executors.newFixedThreadPool(50);
		  for(int i=0;i<50;i++){
			  fixedThreadPool.execute(new Runnable() {
				    public void run() {
				     System.out.println(Thread.currentThread());
				    }
				   });	
		  }
		  fixedThreadPool.shutdown();
	}
}
