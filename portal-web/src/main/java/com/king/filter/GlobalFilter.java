package com.king.filter;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.king.common.utils.JsonResponse;
import com.king.common.utils.constant.Constant;
import com.king.common.utils.security.ShiroUtils;

import java.io.IOException;

/**
 * 全局过滤器
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年3月7日
 */
@WebFilter(filterName = "globalFilter", urlPatterns = "/*")
public class GlobalFilter implements Filter {
	private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    	//设置跨域
        HttpServletRequest req=(HttpServletRequest)request;
        HttpServletResponse res=(HttpServletResponse)response;
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        res.setHeader("Access-Control-Max-Age", "0");
        res.setHeader("Access-Control-Allow-Headers",
                "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId,token");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        res.setHeader("XDomainRequestAllowed", "1");
        String url = req.getRequestURI();
        //为第三方插件权限拦截、只允许超级管理员
        if(url.contains("monitoring") || url.contains("druid")){
        	try {
            	if(ShiroUtils.getUserId() != Constant.SUPER_ADMIN){
            		accessDenied(req, res);
            	}   
			} catch (Exception e) {
				// TODO: handle exception
				accessDenied(req, res);
			} 		
        }
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }
    
    
    /**
	 * 拒绝访问处理
	 */
	private void accessDenied(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
		logger.warn("权限不够,拒绝访问!");
         String json = new Gson().toJson(JsonResponse.error(HttpStatus.SC_UNAUTHORIZED, "invalid token"));
         response.getOutputStream().print(json);
	}
}
