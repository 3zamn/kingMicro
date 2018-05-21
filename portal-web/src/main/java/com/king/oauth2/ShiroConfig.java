package com.king.oauth2;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.king.common.utils.constant.Constant;
import com.king.utils.RedisShiroSession;

/**
 * Shiro配置
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年1月11日
 */
@Configuration
public class ShiroConfig {
	@Value("#{new Boolean('${king.redis.open}')}")
//	@Value("${king.redis.open}") 
	private Boolean redisOpen;
	@Value("#{new Boolean('${king.shiro.redis}')}")
 //   @Value("${king.shiro.redis}")
    private	Boolean shiroRedis;
	@Value("#{new Boolean('${king.swagger.status}')}")
 //   @Value("${king.swagger.status}")
    private	Boolean swagger;
    
	@Bean("sessionManager")
    public SessionManager sessionManager(RedisShiroSession redisShiroSessionDAO ){	
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        //设置session过期时间为30分钟(单位：毫秒)
        sessionManager.setGlobalSessionTimeout(Constant.SHIRO_SESSION_EXPIRE);
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        sessionManager.setSessionDAO(redisShiroSessionDAO);
        return sessionManager;
    }

	@Bean("securityManager")
	public SecurityManager securityManager(OAuth2Realm oAuth2Realm, SessionManager sessionManager) {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(oAuth2Realm);
		securityManager.setSessionManager(sessionManager);
		return securityManager;
	}

    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);

        //oauth过滤
        Map<String, Filter> filters = new HashMap<>();
        filters.put("oauth2", new OAuth2Filter());
        shiroFilter.setFilters(filters);
        //第三方应用或插件在其他拦截器或过滤器出来权限、这里设为anon
        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/webjars/**", "anon");
        filterMap.put("/druid/**", "anon");
        filterMap.put("/app/**", "anon");
        filterMap.put("/sys/login", "anon");
    //    filterMap.put("/sys/user/test", "anon");
        filterMap.put("/**/*.css", "anon");
        filterMap.put("/**/*.js", "anon");
        filterMap.put("/**/*.html", "anon");
        filterMap.put("/fonts/**", "anon");
        filterMap.put("/plugins/**", "anon");
        filterMap.put("/swagger/**", "anon");
        filterMap.put("/v2/api-docs", "anon");
        filterMap.put("/swagger-ui.html", "anon");
        filterMap.put("/swagger-resources/**", "anon");
        filterMap.put("/favicon.ico", "anon");
        filterMap.put("/captcha.jpg", "anon");
        filterMap.put("/", "anon");
        filterMap.put("/monitoring/**", "anon");
        filterMap.put("/**", "oauth2");
        shiroFilter.setFilterChainDefinitionMap(filterMap);

        return shiroFilter;
    }

    @Bean("lifecycleBeanPostProcessor")
    public static  LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }
    
    //统一用spring中一个aop自动代理、使用cglib代理<aop:aspectj-autoproxy proxy-target-class="true" />。防止两次代理或冲突、shrio中不创建代理
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
        proxyCreator.setProxyTargetClass(true);
        return proxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

}
