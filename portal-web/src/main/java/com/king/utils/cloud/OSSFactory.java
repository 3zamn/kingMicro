package com.king.utils.cloud;


import com.king.api.smp.SysConfigService;
import com.king.common.utils.constant.Constant;
import com.king.common.utils.spring.SpringContextUtils;
import com.king.dal.gen.model.oss.CloudStorageConfig;


/**
 * 文件上传Factory
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年5月23日
 */
public final class OSSFactory {
    private static SysConfigService sysConfigService;

    static {
        OSSFactory.sysConfigService = (SysConfigService) SpringContextUtils.getBean("sysConfigService");
    }

    public static CloudStorageService build(){
        //获取云存储配置信息
        CloudStorageConfig config = sysConfigService.getConfigObject(Constant.CLOUD_STORAGE_CONFIG, CloudStorageConfig.class);

        if(config.getType() == Constant.CloudService.QINIU.getValue()){
            return new QiniuCloudStorageService(config);
        }else if(config.getType() == Constant.CloudService.ALIYUN.getValue()){
            return new AliyunCloudStorageService(config);
        }else if(config.getType() == Constant.CloudService.QCLOUD.getValue()){
            return new QcloudCloudStorageService(config);
        }

        return null;
    }

}
