package com.king.utils.cloud;

import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.king.common.utils.date.DateUtils;
import com.king.dal.gen.model.oss.CloudStorageConfig;

/**
 * 云存储(支持七牛、阿里云、腾讯云、又拍云)
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年5月23日
 */
public abstract class CloudStorageService {
    /** 云存储配置信息 */
   public CloudStorageConfig config;

    /**
     * 文件路径
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 返回上传路径
     */
    public String getPath(String prefix, String suffix) {
        //生成uuid
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        //文件路径
        String path = DateUtils.format(new Date(), "yyyyMMdd") + "/" + uuid;

        if(StringUtils.isNotBlank(prefix)){
            path = prefix + "/" + path;
        }

        return path + suffix;
    }

    /**
     * 文件上传
     * @param data    文件字节数组
     * @param path    文件路径，包含文件名
     * @return        返回http地址
     */
    public abstract String upload(byte[] data, String path);

    /**
     * 文件上传
     * @param data     文件字节数组
     * @param suffix   后缀
     * @return         返回http地址
     */
    public abstract String uploadSuffix(byte[] data, String suffix);

    /**
     * 文件上传
     * @param inputStream   字节流
     * @param path          文件路径，包含文件名
     * @return              返回http地址
     */
    public abstract String upload(InputStream inputStream, String path);

    /**
     * 文件上传
     * @param inputStream  字节流
     * @param suffix       后缀
     * @return             返回http地址
     */
    public abstract String uploadSuffix(InputStream inputStream, String suffix);
    
    /**
     * 删除指定的文件
     * @param deleteObject
     */
    public abstract void delete(String deleteObject);

}
