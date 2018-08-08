package com.king.common.utils.constant;

/**
 * 常量
 * @author King chen
 * @date 2017年12月25日
 */
public class Constant {
	/** 超级管理员ID */
	public static final long SUPER_ADMIN = 1;

	/*token失效时长*/
	public static final long TOKEN_EXPIRE =30 * 60 *1000;//半小时失效
	public static final long HALF_HOUR =1800;//半小时
	
	public static final long SHIRO_SESSION_EXPIRE =30 * 60 * 1000;//半小时
	
	public static final long PERMS_EXPIRE =48 *60 * 60;//权限48小时后权限失效，需重新登录
	
	public static final long SERIALNO_EXPIRE =30;//秒
	public static final long LOGIN_IP_COUNT =30;//同一个IP同一个帐号
	public static final long LOGIN_COUNT =100;//同一个帐号
	public final static String CLOUD_STORAGE_CONFIG = "CLOUD_STORAGE_CONFIG";//云盘配置
	
    public enum MenuType {
        /**
         * 目录
         */
    	CATALOG(0),
        /**
         * 菜单
         */
        MENU(1),
        /**
         * 按钮
         */
        BUTTON(2);

        private int value;

        private MenuType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    
    /**
     * 字典类型
     * @author King chen
     * @emai 396885563@qq.com
     * @data2018年5月10日
     */
    public enum DicType {
        /**
         * 字典目录
         */
    	CATALOG(0),
        /**
         * 字典项
         */
        TERM(1);


        private int value;

        private DicType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    
    /**
     * 定时任务状态
     * @author King chen
   	 * @date 2017年12月25日
     */
    public enum ScheduleStatus {
        /**
         * 正常
         */
    	NORMAL(0),
        /**
         * 暂停
         */
    	PAUSE(1);

        private int value;

        private ScheduleStatus(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }

    /**
     * 云服务商
     */
    public enum CloudService {
        /**
         * 七牛云
         */
        QINIU(1),
        /**
         * 阿里云
         */
        ALIYUN(2),
        /**
         * 腾讯云
         */
        QCLOUD(3);

        private int value;

        private CloudService(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
