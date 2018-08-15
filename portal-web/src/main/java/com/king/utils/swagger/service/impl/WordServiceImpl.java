package com.king.utils.swagger.service.impl;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.king.api.smp.SysConfigService;
import com.king.common.utils.exception.RRException;
import com.king.common.utils.pattern.StringToolkit;
import com.king.utils.ShiroUtils;
import com.king.utils.swagger.entity.Body;
import com.king.utils.swagger.entity.Head;
import com.king.utils.swagger.entity.Info;
import com.king.utils.swagger.entity.Request;
import com.king.utils.swagger.entity.Response;
import com.king.utils.swagger.service.WordService;

/**
 * @author King chen
 * @emai 396885563@qq.com
 * @data 2018年8月10日
 */
@Service
public class WordServiceImpl implements WordService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
	private SysConfigService sysConfigService;
    private Logger logger = LoggerFactory.getLogger(getClass());
 
    
    @Override
	public Info getinfo() {
    	if(StringUtils.isBlank(sysConfigService.getValue("SWAGGER_ENABLE")) || !sysConfigService.getValue("SWAGGER_ENABLE").equals("true")){
      		return null;//未启用
      	}
    	String swaggerUrl = sysConfigService.getValue("SWAGGER_URL");
		@SuppressWarnings("unchecked")
		Map<String, Object> map = restTemplate.getForObject(swaggerUrl, Map.class);
		@SuppressWarnings("unchecked")
		Map<String, Object> infoMap=(Map<String, Object>)map.get("info");
		Info info = new Info();
		info.setVersion(StringToolkit.getObjectString(infoMap.get("version")));
		info.setBasePath(StringToolkit.getObjectString(map.get("basePath")));
		info.setDate(new Date());
		info.setDescription(StringToolkit.getObjectString(infoMap.get("description")));
		info.setTitle(StringToolkit.getObjectString(infoMap.get("title")));
		info.setHost(StringToolkit.getObjectString(map.get("host")));
		Configuration config = getConfig();
		info.setAuthor(config.getString("author", "King Chen"));
		info.setEmail(config.getString("email", "396885563@qq.com"));
		info.setTermsOfService(StringToolkit.getObjectString(infoMap.get("termsOfService")));
		return info;
	}
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
  	@Override
      public List<Head> headList() {
      	if(StringUtils.isBlank(sysConfigService.getValue("SWAGGER_ENABLE")) || !sysConfigService.getValue("SWAGGER_ENABLE").equals("true")){
      		return null;//未启用
      	}
		String swaggerUrl = sysConfigService.getValue("SWAGGER_URL");
		Map<String, Object> map = restTemplate.getForObject(swaggerUrl, Map.class);
		List<Head> list = new LinkedList();
		// 解析tags
		List<HashMap<String, String>> headList = (List<HashMap<String, String>>) map.get("tags");
		for (HashMap<String, String> m : headList) {
			Head head = new Head();
			head.setName(m.get("name"));
			head.setDescription(m.get("description"));
			list.add(head);
		}    
          return list;
      }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public List<Body> bodyList() {
    	if(StringUtils.isBlank(sysConfigService.getValue("SWAGGER_ENABLE")) || !sysConfigService.getValue("SWAGGER_ENABLE").equals("true")){
    		return null;//未启用
    	}
    	String swaggerUrl =sysConfigService.getValue("SWAGGER_URL");
        Map<String, Object> map = restTemplate.getForObject(swaggerUrl, Map.class);
        List<Body> list = new LinkedList();
        String host=StringUtils.substringBefore(swaggerUrl, ":")+"://"+String.valueOf(map.get("host"))+String.valueOf(map.get("basePath"));
        //解析paths
        LinkedHashMap<String, LinkedHashMap> paths = (LinkedHashMap) map.get("paths");
        if (paths != null) {
            Iterator<Map.Entry<String, LinkedHashMap>> it = paths.entrySet().iterator();
            while (it.hasNext()) {
                Body body = new Body();
                List<Request> requestList = new LinkedList<>();
                List<Response> responseList = new LinkedList<>();
                String requestForm = ""; //请求参数格式，类似于 multipart/form-data
                String responseForm = ""; //响应参数格式
                String requestType = ""; //请求方式，类似为 get,post,delete,put 这样
                String url; //请求路径
                String title; //大标题（类说明）
                String tag; //小标题 （方法说明）
                String description; //接口描述
				Map.Entry<String, LinkedHashMap> path = it.next();
                url = path.getKey();

                LinkedHashMap<String, LinkedHashMap> value = path.getValue();
                Set<String> requestTypes = value.keySet();
                for (String str : requestTypes) {
                    requestType += str + ",";
                }

                Iterator<Map.Entry<String, LinkedHashMap>> it2 = value.entrySet().iterator();
                //只解析第一种请求方式(所以接口建议用@GetMapping、@PostMapping.代替@RequestMapping)
                Map.Entry<String, LinkedHashMap> firstRequestType = it2.next();
                LinkedHashMap content = firstRequestType.getValue();
                title = String.valueOf(((List) content.get("tags")).get(0));
                description = String.valueOf(content.get("description"));
                List<String> consumes = (List) content.get("consumes");
                if (consumes != null && consumes.size() > 0) {
                    for (String consume : consumes) {
                        requestForm += consume + ",";
                    }
                }
                List<String> produces = (List) content.get("produces");
                if (produces != null && produces.size() > 0) {
                    for (String produce : produces) {
                        responseForm += produce + ",";
                    }
                }

                tag = String.valueOf(content.get("summary"));
                //请求体
                List parameters = (ArrayList) content.get("parameters");
                if (parameters != null && parameters.size() > 0) {
                    for (int i = 0; i < parameters.size(); i++) {
                        Request request = new Request();
                        LinkedHashMap<String, Object> param = (LinkedHashMap) parameters.get(i);
                        request.setName(String.valueOf(param.get("name")));
                        request.setType(param.get("type") == null ? "Object" : param.get("type").toString());
                        request.setParamType(String.valueOf(param.get("in")));
                        request.setRequire((Boolean) param.get("required"));
                        request.setRemark(String.valueOf(param.get("description")));
                        requestList.add(request);
                    }
                }
                //返回体
                LinkedHashMap<String, Object> responses = (LinkedHashMap) content.get("responses");
                Iterator<Map.Entry<String, Object>> it3 = responses.entrySet().iterator();
                while (it3.hasNext()) {
                    Response response = new Response();
                    Map.Entry<String, Object> entry = it3.next();
                    String statusCode = entry.getKey(); //状态码 200 201 401 403 404 这样
                    LinkedHashMap<String, Object> statusCodeInfo = (LinkedHashMap) entry.getValue();
                    String statusDescription = (String) statusCodeInfo.get("description");
                    response.setName(statusCode);
                    response.setDescription(statusDescription);
                    response.setRemark(null);
                    responseList.add(response);
                }

                //模拟一次HTTP请求,封装请求体和返回体
                String restType = firstRequestType.getKey();//得到请求方式
                Map<String, Object> paramMap = ParamMap(requestList);
                String buildUrl = buildUrl(host + url, requestList);

                //封装Table
                body.setTitle(title);
                body.setUrl(url);
                body.setTag(tag);
                body.setDescription(description);
                body.setRequestForm(StringUtils.removeEnd(requestForm, ","));
                body.setResponseForm(StringUtils.removeEnd(responseForm, ","));
                body.setRequestType(StringUtils.removeEnd(requestType, ","));
                body.setRequestList(requestList);
                body.setResponseList(responseList);
                body.setRequestParam(String.valueOf(paramMap));
                body.setResponseParam(doRestRequest(restType, buildUrl, paramMap));
                list.add(body);
            }
        }
        return list;
    }

    /**
     * 重新构建url
     * @param url
     * @param requestList
     * @return
     */
    private String buildUrl(String url, List<Request> requestList) {
        String param = "";
        if(!url.contains("logout")){//排除退出登录
        	 if (requestList != null && requestList.size() > 0) {
                 for (Request request : requestList) {
                     String name = request.getName();
                     param += name + "={" + name + "}&";
                 }
             }
             if (StringUtils.isNotEmpty(param)) {
                 url += "?" + StringUtils.removeEnd(param, "&");
             }
        }
    
        return url;

    }


    /**
     * 请求
     * @param restType
     * @param url
     * @param paramMap
     * @return
     */
    private String doRestRequest(String restType, String url, Map<String, Object> paramMap) {
        Object object = null;
        try {
            switch (restType) {
                case "get":
                    object = restTemplate.getForObject(url, Object.class, paramMap);
                    break;
                case "post":
                    object = restTemplate.postForObject(url, null, Object.class, paramMap);
                    break;
                case "put":
                    restTemplate.put(url, null, paramMap);
                    break;
                case "head":
                    HttpHeaders httpHeaders = restTemplate.headForHeaders(url, paramMap);
                    return String.valueOf(httpHeaders);
                case "delete":
                    restTemplate.delete(url, paramMap);
                    break;
                case "options":
                    Set<HttpMethod> httpMethods = restTemplate.optionsForAllow(url, paramMap);
                    return String.valueOf(httpMethods);
                case "patch":
                    object = restTemplate.execute(url, HttpMethod.PATCH, null, null, paramMap);
                    break;
                case "trace":
                    object = restTemplate.execute(url, HttpMethod.TRACE, null, null, paramMap);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
        	logger.error("生成接口文档出现未知错误"+e.getMessage());
            return "";
        }
        return String.valueOf(object);
    }


    /**
     * 封装请求体/使用默认参数
     * @param list
     * @return
     */
    private Map<String, Object> ParamMap(List<Request> list) {
        Map<String, Object> map = new HashMap<>(8);
        if (list != null && list.size() > 0) {
            for (Request request : list) {
                String name = request.getName();
                String type = request.getType();
                switch (type) {
                    case "string":
                    	if(name.equals("token")){//设置token
                    		 map.put(name, ShiroUtils.getUserEntity().getToken());                 		
                    	}else{
                    		 map.put(name, "string");
                    	}                    
                        break;
                    case "integer":
                    	map.put(name, 0);
                        break;
                    case "object":
                    	if(name.equals("params")){                		
                      		 map.put(name, "limit=10&page=1");
                       	}else{
                       		  map.put(name, 0);
                       	}                                
                        break;
                    case "file":
                        map.put(name, null);
                        break;       
                    case "Object":
                        map.put(name, null);
                        break;    
                    case "number":
                        map.put(name, 0.0);
                        break;
                    case "boolean":
                        map.put(name, true);
                        break;
                    case "Array[long]":
                        map.put(name, "[0]");    
                        break;
                    case "Array[string]":
                        map.put(name, "[0]");  
                        break;
                    case "Model":
                        map.put(name, "{\"msg\":\"success\"}");
                        break;
                    default:
                        map.put(name, null);
                        break;
                }
            }
        }
        return map;
    }
    
    
	/**
	 * 获取配置信息
	 */
	public static Configuration getConfig(){
		try {
			return new PropertiesConfiguration("gen"+File.separator+"generator.properties");
		} catch (ConfigurationException e) {
			throw new RRException("获取配置文件失败，", e);
		}
	}
}
