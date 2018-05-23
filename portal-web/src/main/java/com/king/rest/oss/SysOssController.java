package com.king.rest.oss;

import java.util.Date;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.king.api.oss.SysOssService;
import com.king.api.smp.SysConfigService;
import com.king.common.annotation.Log;
import com.king.common.utils.JsonResponse;
import com.king.common.utils.Page;
import com.king.common.utils.Query;
import com.king.common.utils.constant.ConfigConstant;
import com.king.common.utils.constant.Constant;
import com.king.common.utils.exception.RRException;
import com.king.common.utils.validator.ValidatorUtils;
import com.king.common.utils.validator.group.AliyunGroup;
import com.king.common.utils.validator.group.QcloudGroup;
import com.king.common.utils.validator.group.QiniuGroup;
import com.king.dal.gen.controller.AbstractController;
import com.king.dal.gen.model.oss.CloudStorageConfig;
import com.king.dal.gen.model.oss.SysOss;
import com.king.utils.cloud.OSSFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


/**
 * 文件上传
 * 
 * @author king chen
 * @email 396885563@qq.com
 * @date 2018-05-23 14:25:16
 */
@RestController
@Api(value = "文件上传", description = "文件上传")
@RequestMapping("/sys/oss")
public class SysOssController extends AbstractController{
	@Autowired
	private SysOssService sysOssService;
	@Autowired
	private SysConfigService sysConfigService;
	private final static String KEY = ConfigConstant.CLOUD_STORAGE_CONFIG_KEY;
	/**
	 * 列表
	 */
	@Log("文件上传列表")
	@ApiOperation(value = "列表",notes = "权限编码（sys:oss:list）")
	@GetMapping("/list")
	@RequiresPermissions("sys:oss:list")
	public JsonResponse list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params,SysOss.class.getSimpleName());
		Page page = sysOssService.getPage(query);
		return JsonResponse.success(page);
	}
	
	
	/**
	 * 信息
	 */
	@Log("文件上传查询信息")
    @ApiOperation(value = "查询信息",notes = "权限编码（sys:oss:info）")
	@GetMapping("/info/{id}")
	@RequiresPermissions("sys:oss:info")
	public JsonResponse info(@PathVariable("id") Object id){
		SysOss sysOss = sysOssService.queryObject(id);
		
		return JsonResponse.success(sysOss);
	}
	
	
	/**
	 * 修改
	 */
	@Log("文件上传修改")
	@ApiOperation(value = "修改",notes = "权限编码（sys:oss:update）")
	@PostMapping("/update")
	@RequiresPermissions("sys:oss:update")
	public JsonResponse update(@RequestBody SysOss sysOss){
		sysOssService.update(sysOss);
		
		return JsonResponse.success();
	}
	
	/**
	 * 删除
	 */
	@Log("文件上传删除")
	@ApiOperation(value = "删除",notes = "权限编码（sys:oss:delete）")
	@PostMapping("/delete")
	@RequiresPermissions("sys:oss:delete")
	public JsonResponse delete(@RequestBody Object[] ids){
		sysOssService.deleteBatch(ids);
		
		return JsonResponse.success();
	}
	

    /**
     * 云存储配置信息
     */
	@Log("云存储配置信息")
	@ApiOperation(value = "云存储配置信息",notes = "权限编码（sys:oss:all）")
    @RequestMapping("/config")
    @RequiresPermissions("sys:oss:all")
    public JsonResponse config(){
        CloudStorageConfig config = sysConfigService.getConfigObject(KEY, CloudStorageConfig.class);

        return JsonResponse.success(config);
    }


	/**
	 * 保存云存储配置信息
	 */
    @Log("保存云存储配置信息")
	@ApiOperation(value = "保存云存储配置信息",notes = "权限编码（sys:oss:all）")
	@RequestMapping("/saveConfig")
	@RequiresPermissions("sys:oss:all")
	public JsonResponse saveConfig(@RequestBody CloudStorageConfig config){
		//校验类型
		ValidatorUtils.validateEntity(config);

		if(config.getType() == Constant.CloudService.QINIU.getValue()){
			//校验七牛数据
			ValidatorUtils.validateEntity(config, QiniuGroup.class);
		}else if(config.getType() == Constant.CloudService.ALIYUN.getValue()){
			//校验阿里云数据
			ValidatorUtils.validateEntity(config, AliyunGroup.class);
		}else if(config.getType() == Constant.CloudService.QCLOUD.getValue()){
			//校验腾讯云数据
			ValidatorUtils.validateEntity(config, QcloudGroup.class);
		}
		

        sysConfigService.updateValueByKey(KEY, JSON.toJSONString(config));

		return JsonResponse.success();
	}
	

	/**
	 * 上传文件
	 */
	@Log("文件上传修改")
	@ApiOperation(value = "文件上传修改",notes = "权限编码（sys:oss:all）")
	@RequestMapping("/upload")
	@RequiresPermissions("sys:oss:all")
	public JsonResponse upload(@RequestParam("file") MultipartFile file) throws Exception {
		if (file.isEmpty()) {
			throw new RRException("上传文件不能为空");
		}

		//上传文件
		String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		String url = OSSFactory.build().uploadSuffix(file.getBytes(), suffix);

		//保存文件信息
		SysOss oss = new SysOss();
		oss.setUrl(url);
		oss.setName(file.getOriginalFilename());
		oss.setCreator(getUser().getUsername());
		oss.setCreateDate(new Date());
		sysOssService.save(oss);

		return JsonResponse.success(url);
	}


	
}
