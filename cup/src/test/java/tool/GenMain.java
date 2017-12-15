package tool;

import tools.mybatis.gen.GenMybatisFiles;



/**
 * 生产数据操作层代码
 *
 */
public class GenMain {
	public static void main(String[] args) {
		String configFile = "/generatorConfig.xml";
		try {
			String[] tableNames = new String[] {"sys_news"};
			GenMybatisFiles.gen(configFile, tableNames);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
