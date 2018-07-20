package com.king.rest.oss;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.util.GraphicsRenderingHints;
import org.jodconverter.JodConverter;
import org.jodconverter.office.LocalOfficeManager;
import org.jodconverter.office.OfficeException;
import org.jodconverter.office.OfficeManager;
import org.jodconverter.office.OfficeUtils;
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
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.king.api.oss.SysOssService;
import com.king.api.smp.SysConfigService;
import com.king.common.annotation.Log;
import com.king.common.utils.JsonResponse;
import com.king.common.utils.Page;
import com.king.common.utils.constant.ConfigConstant;
import com.king.common.utils.exception.RRException;
import com.king.common.utils.file.FileToolkit;
import com.king.common.utils.file.IoUtil;
import com.king.dal.gen.model.oss.CloudStorageConfig;
import com.king.dal.gen.model.oss.SysOss;
import com.king.utils.AbstractController;
import com.king.utils.Query;
import com.king.utils.cloud.CloudStorageService;
import com.king.utils.cloud.OSSFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * office、png转pdf、pdf生成图片
 * 
 * @author King chen
 * @emai 396885563@qq.com
 * @data 2018年7月19日
 */
@Lazy
@RestController
@Api(value = "pdf转换服务", description = "pdf转换服务")
@RequestMapping("/oss/pdf")
public class OssPdfController extends AbstractController {
	@Autowired
	private SysOssService sysOssService;
	@Autowired
	private SysConfigService sysConfigService;
	private final static String KEY = ConfigConstant.CLOUD_STORAGE_CONFIG_KEY;

	/**
	 * 列表
	 */
	@ApiOperation(value = "列表", notes = "权限编码（oss:pdf:list）")
	@GetMapping("/list")
	@RequiresPermissions("oss:pdf:list")
	public JsonResponse list(@RequestParam Map<String, Object> params) {
		// 查询列表数据
		Query query = new Query(params, SysOss.class.getSimpleName());
		Page page = sysOssService.getPage(query);
		return JsonResponse.success(page);
	}

	/**
	 * 信息
	 */
	@Log("文件上传查询信息")
	@ApiOperation(value = "查询信息", notes = "权限编码（oss:pdf:info）")
	@GetMapping("/info/{id}")
	@RequiresPermissions("oss:pdf:info")
	public JsonResponse info(@PathVariable("id") Object id) {
		SysOss sysOss = sysOssService.queryObject(id);

		return JsonResponse.success(sysOss);
	}

	/**
	 * 修改
	 */
	@Log("文件上传修改")
	@ApiOperation(value = "修改", notes = "权限编码（oss:pdf:update）")
	@PostMapping("/update")
	@RequiresPermissions("oss:pdf:update")
	public JsonResponse update(@RequestBody SysOss sysOss) {
		sysOssService.update(sysOss);

		return JsonResponse.success();
	}

	/**
	 * 删除本地文件并循环删除云文件
	 */
	@Log("文件上传删除")
	@ApiOperation(value = "删除", notes = "权限编码（oss:pdf:delete）")
	@PostMapping("/delete")
	@RequiresPermissions("oss:pdf:delete")
	public JsonResponse delete(@RequestBody Object[] ids) {
		CloudStorageConfig config = sysConfigService.getConfigObject(ConfigConstant.CLOUD_STORAGE_CONFIG_KEY,
				CloudStorageConfig.class);
		String yunPath = null;
		String deleteObject = null;
		List<SysOss> list = sysOssService.queryBatch(ids);
		for (SysOss oss : list) {
			switch (config.getType()) {
			case 1:
				yunPath = config.getQiniuDomain() + "/";
				if (StringUtils.isNotBlank(config.getQiniuPrefix())) {
					yunPath = yunPath + config.getQiniuPrefix();
				}
				deleteObject = oss.getUrl().replace(yunPath, "");
				OSSFactory.build().delete(deleteObject);
				break;
			case 2:
				yunPath = config.getAliyunDomain() + "/";
				if (StringUtils.isNotBlank(config.getAliyunPrefix())) {
					yunPath = yunPath + config.getAliyunPrefix();
				}
				deleteObject = oss.getUrl().replace(yunPath, "");
				OSSFactory.build().delete(deleteObject);
				break;
			case 3:
				yunPath = config.getQcloudDomain() + "/";
				if (StringUtils.isNotBlank(config.getQcloudPrefix())) {
					yunPath = yunPath + config.getQcloudPrefix();
				}
				deleteObject = oss.getUrl().replace(yunPath, "");
				OSSFactory.build().delete(deleteObject);
				break;
			default:
				break;
			}
		}
		sysOssService.deleteBatch(ids);
		return JsonResponse.success();
	}

	/**
	 * 上传文件
	 * Text documents (odt, doc, docx, rtf, etc.) 
	 * Spreadsheet documents (ods, xls, xlsx, csv, etc.)
	 * Spreadsheet documents (odp, ppt, pptx, etc.) 
	 * Drawing documents (odg, png, svg, etc.)
	 */
	@ApiOperation(value = "文件上传", notes = "权限编码（oss:pdf:upload）")
	@RequestMapping("/upload")
	@RequiresPermissions("oss:pdf:upload")
	public JsonResponse upload(@RequestParam("file") MultipartFile file) throws Exception {
		if (file.isEmpty()) {
			throw new RRException("上传文件不能为空");
		}else if(file.getName().endsWith("doc")||file.getName().endsWith("docx")||file.getName().endsWith("xls")
				||file.getName().endsWith("xlsx")||file.getName().endsWith("ppt")||file.getName().endsWith("ppt")
				||file.getName().endsWith("png")||file.getName().endsWith("svg")||file.getName().endsWith("rtf")){
			throw new RRException("只能转换office文档或png格式图片");
		}
		String dest= IoUtil.getFile("gen").getPath()+File.separator+file.getOriginalFilename();
		IoUtil.writeByteToFile(file.getBytes(), dest);
		docConvertPdf(new File(dest),file.getOriginalFilename());//转换成pdf
		// 上传文件
		String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		CloudStorageService cloudStorage = OSSFactory.build();// 初始化获取配置
		CloudStorageConfig config = cloudStorage.config;
		String url = cloudStorage.uploadSuffix(file.getBytes(), suffix);
		String size = new BigDecimal(file.getSize()).divide(new BigDecimal(1024), RoundingMode.HALF_UP) + " KB";
		// 保存文件信息
		SysOss oss = new SysOss();
		oss.setType(config.getType() + "");
		oss.setSize(size);
		oss.setUrl(url);
		oss.setName(file.getOriginalFilename());
		oss.setCreator(getUser().getUsername());
		oss.setCreateDate(new Date());
		sysOssService.save(oss);
		return JsonResponse.success(url);
	}


	@ApiOperation(value = "pdf预览", notes = "权限编码（oss:pdf:view）")
	@GetMapping("/view")
//	@RequiresPermissions("oss:pdf:view")
	public void pdfViewer(HttpServletRequest request, HttpServletResponse response, String urlpath) {
		logger.info("urlpath=" + urlpath);
		try {
			InputStream fileInputStream = getFile(urlpath);
			response.setHeader("Content-Disposition", "attachment;fileName=test.pdf");
			response.setContentType("multipart/form-data");
			OutputStream outputStream = response.getOutputStream();
			IOUtils.write(IOUtils.toByteArray(fileInputStream), outputStream);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public InputStream getFile(String urlPath) {
		InputStream inputStream = null;
		try {
			try {
				String strUrl = urlPath.trim();
				URL url = new URL(strUrl);
				// 打开请求连接
				URLConnection connection = url.openConnection();
				HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
				httpURLConnection.setRequestProperty("User-Agent",
						"Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
				// 取得输入流，并使用Reader读取
				inputStream = httpURLConnection.getInputStream();
				return inputStream;
			} catch (IOException e) {
				System.out.println(e.getMessage());
				inputStream = null;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			inputStream = null;
		}
		return inputStream;
	}

	/**
	 * doc、png转pdf
	 * @throws IOException 
	 */
	public void docConvertPdf(File inputFile,String name) throws IOException {

		if (!inputFile.exists()) {
			System.out.println("源文件不存在！");
			return;
		}
		// 输出文件到临时目录
		String dest= IoUtil.getFile("gen").getPath()+File.separator;
		File outputFile = new File(dest+File.separator+name.substring(0, name.lastIndexOf("."))+".pdf");
		if (outputFile.getParentFile().exists()&& outputFile.isDirectory()) {
			System.out.println(inputFile.getName());
			System.out.println(outputFile.getName());
		}
		// 连接OpenOffice/LibreOffice服务
		OfficeManager officeManager = LocalOfficeManager.builder().officeHome("C:\\Program Files\\LibreOffice").install().build();
		try {
			officeManager.start();
			// 转换文档到pdf
			long time = System.currentTimeMillis();
			JodConverter.convert(inputFile).to(outputFile).execute();
			String pdfPath=dest+File.separator+name.substring(0, name.lastIndexOf("."))+".pdf";
			CloudStorageService cloudStorage = OSSFactory.build();// 初始化获取配置		
			cloudStorage.uploadSuffix(IoUtil.readByteFromFile(pdfPath), ".pdf");
			pdfConvertPng(pdfPath, dest+File.separator+name.substring(0, name.lastIndexOf(".")));
			logger.info("文件转换PDF完成，用时{}毫秒！", System.currentTimeMillis() - time);
		} catch (OfficeException e) {
			e.printStackTrace();
			logger.warn("文件转换PDF失败！");
		} finally {
			// 关闭连接
			OfficeUtils.stopQuietly(officeManager);
		}
	}

	/**
	 * pdf转png
	 */
	public void pdfConvertPng(String pdfPath, String path) {
		if(!new File(path).exists()){  
			new File(path).mkdirs();  
		} 
		Document document = new Document();
		document.setFile(pdfPath);
		// 缩放比例、像素
		float scale = 5.5f;
		// 旋转角度
		float rotation = 0f;

		for (int i = 0; i < document.getNumberOfPages(); i++) {
			BufferedImage image = (BufferedImage) document.getPageImage(i, GraphicsRenderingHints.SCREEN,
					org.icepdf.core.pobjects.Page.BOUNDARY_CROPBOX, rotation, scale);
			RenderedImage rendImage = image;
			try {
				String imgName = i + ".png";
				System.out.println(imgName);
				File file = new File(path+File.separator + imgName);
				ImageIO.write(rendImage, "png", file);
				CloudStorageService cloudStorage = OSSFactory.build();// 初始化获取配置		
				cloudStorage.uploadSuffix(IoUtil.readByteFromFile(path+File.separator+imgName), ".png");
			} catch (IOException e) {
				e.printStackTrace();
			}
			image.flush();
		}
		document.dispose();
	}

}
