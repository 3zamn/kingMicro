package com.king.rest.oss;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.king.api.oss.OssFileService;
import com.king.api.smp.SysConfigService;
import com.king.common.annotation.Log;
import com.king.common.utils.JsonResponse;
import com.king.common.utils.Page;
import com.king.common.utils.constant.Constant;
import com.king.common.utils.exception.RRException;
import com.king.common.utils.validator.ValidatorUtils;
import com.king.common.utils.validator.group.AliyunGroup;
import com.king.common.utils.validator.group.QcloudGroup;
import com.king.common.utils.validator.group.QiniuGroup;
import com.king.dal.gen.model.oss.CloudStorageConfig;
import com.king.dal.gen.model.oss.OssFile;
import com.king.utils.AbstractController;
import com.king.utils.Query;
import com.king.utils.cloud.CloudStorageService;
import com.king.utils.cloud.OSSFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


/**
 * 文件云盘
 * 使用懒加载，使用时才初始化bean
 * @author king chen
 * @email 396885563@qq.com
 * @date 2018-05-23 14:25:16
 */
@Lazy
@RestController
@Api(value = "文件云盘", description = "文件云盘")
@RequestMapping("/oss/file")
public class OssFileController extends AbstractController{
	@Autowired
	private OssFileService ossFileService;
	@Autowired
	private SysConfigService sysConfigService;
	private final static String KEY = Constant.CLOUD_STORAGE_CONFIG;
	/**
	 * 列表
	 */
	@Log("文件上传列表")
	@ApiOperation(value = "列表",notes = "权限编码（oss:file:list）")
	@GetMapping("/list")
	@RequiresPermissions("oss:file:list")
	public JsonResponse list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params,OssFile.class.getSimpleName());
		Page page = ossFileService.getPage(query);
		return JsonResponse.success(page);
	}
	
	
	/**
	 * 信息
	 */
	@Log("文件上传查询信息")
    @ApiOperation(value = "查询信息",notes = "权限编码（oss:file:info）")
	@GetMapping("/info/{id}")
	@RequiresPermissions("oss:file:info")
	public JsonResponse info(@PathVariable("id") Object id){
		OssFile sysOss = ossFileService.queryObject(id);
		
		return JsonResponse.success(sysOss);
	}
	
	
	/**
	 * 修改
	 */
	@Log("文件上传修改")
	@ApiOperation(value = "修改",notes = "权限编码（oss:file:update）")
	@PostMapping("/update")
	@RequiresPermissions("oss:file:update")
	public JsonResponse update(@RequestBody(required = false) OssFile sysOss){
		ossFileService.update(sysOss);
		
		return JsonResponse.success();
	}
	
	/**
	 * 删除本地文件并循环删除云文件
	 */
	@Log(value="文件上传删除",delete=true,serviceClass=OssFileService.class)
	@ApiOperation(value = "删除",notes = "权限编码（oss:file:delete）")
	@PostMapping("/delete")
	@RequiresPermissions("oss:file:delete")
	public JsonResponse delete(@RequestBody(required = false) Long[] ids){
		CloudStorageConfig config = sysConfigService.getConfigObject(Constant.CLOUD_STORAGE_CONFIG, CloudStorageConfig.class);
		String yunPath=null;
		String deleteObject =null;
		List<OssFile> list = ossFileService.queryBatch(ids);
		for(OssFile oss:list){
			switch (config.getType()) {
			case 1:
				yunPath=config.getQiniuDomain()+"/";
				if(StringUtils.isNotBlank(config.getQiniuPrefix())){
					yunPath=yunPath+config.getQiniuPrefix();
				}
				deleteObject =oss.getUrl().replace(yunPath, "");
				OSSFactory.build().delete(deleteObject);
				break;
			case 2:
				yunPath=config.getAliyunDomain()+"/";
				if(StringUtils.isNotBlank(config.getAliyunPrefix())){
					yunPath=yunPath+config.getAliyunPrefix();
				}
				deleteObject =oss.getUrl().replace(yunPath, "");
				OSSFactory.build().delete(deleteObject);
				break;
			case 3:
				yunPath=config.getQcloudDomain()+"/";
				if(StringUtils.isNotBlank(config.getQcloudPrefix())){
					yunPath=yunPath+config.getQcloudPrefix();
				}
				deleteObject =oss.getUrl().replace(yunPath, "");
				OSSFactory.build().delete(deleteObject);
				break;
			default:
				break;
			}
		}
		ossFileService.deleteBatch(ids);	
		return JsonResponse.success();
	}
	

    /**
     * 云存储配置信息
     */
	@Log("云存储配置信息")
	@ApiOperation(value = "云存储配置信息",notes = "权限编码（oss:file:config）")
	@GetMapping("/config")
    @RequiresPermissions("oss:file:config")
    public JsonResponse config(){
        CloudStorageConfig config = sysConfigService.getConfigObject(KEY, CloudStorageConfig.class);
        return JsonResponse.success(config);
    }


	/**
	 * 保存云存储配置信息
	 */
    @Log("保存云存储配置信息")
	@ApiOperation(value = "保存云存储配置信息",notes = "权限编码（oss:file:config）")
    @PostMapping("/saveConfig")
	@RequiresPermissions("oss:file:saveConfig")
	public JsonResponse saveConfig(@RequestBody(required = false) CloudStorageConfig config){
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
	@ApiOperation(value = "文件上传",notes = "权限编码（oss:file:upload）")
	@PostMapping("/upload")
	@RequiresPermissions("oss:file:upload")
	public JsonResponse upload(@RequestParam(value="file",required=false) MultipartFile file) throws Exception {
		if (file.isEmpty()) {
			throw new RRException("上传文件不能为空");
		}
		//上传文件
		String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		CloudStorageService cloudStorage = OSSFactory.build();//初始化获取配置
		CloudStorageConfig config=cloudStorage.config;
		String url = cloudStorage.uploadSuffix(file.getBytes(), suffix);
		String size=new BigDecimal(file.getSize()).divide(new BigDecimal(1024),RoundingMode.HALF_UP)+" KB";
		//保存文件信息
		OssFile oss = new OssFile();
		oss.setType(config.getType()+"");
		oss.setSize(size);
		oss.setUrl(url);
		oss.setName(file.getOriginalFilename());
		oss.setCreator(getUser().getUsername());
		oss.setCreateDate(new Date());
		ossFileService.save(oss);
		return JsonResponse.success(url);
	}


	
}
