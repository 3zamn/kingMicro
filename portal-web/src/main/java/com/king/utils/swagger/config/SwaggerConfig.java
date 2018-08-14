package com.king.utils.swagger.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import com.king.api.smp.SysConfigService;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swaggeer配置
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年1月11日
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {	
/*	@Value("#{new Boolean('${king.swagger.status}')}")
	public boolean enableSwagger;
	@Value("#{new Boolean('${king.redis.open}')}")
	//	@Value("${king.redis.open}") 
		private Boolean redisOpen;
		@Value("#{new Boolean('${king.shiro.redis}')}")
	 //   @Value("${king.shiro.redis}")
	    private	Boolean shiroRedis;
		@Value("#{new Boolean('${king.swagger.status}')}")
	 //   @Value("${king.swagger.status}")
	    private	Boolean swagger;*/
	@Autowired
	private SysConfigService sysConfigService;
	
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 系统管理
     * @return
     */
    @Bean
    public Docket smpApi(){  
    	String enableSwagger =sysConfigService.getValue("SWAGGER_ENABLE");
        ParameterBuilder tokenPar = new ParameterBuilder();  
        List<Parameter> pars = new ArrayList<Parameter>();  
        tokenPar.name("token").description("令牌").modelRef(new ModelRef("string")).parameterType("header").required(true).build();  
        pars.add(tokenPar.build());  
        return new Docket(DocumentationType.SWAGGER_2)  
        	.groupName("1_smpApi")  
        	.enable(enableSwagger!=null?enableSwagger.equals("true"):false)
            .select()  
        //    .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)) 
            .apis(RequestHandlerSelectors.basePackage("com.king.rest.smp"))  
        //    .apis(RequestHandlerSelectors.any())  
         //.paths(regex("/api.*"))   
            .paths(PathSelectors.any())
            .build()  
            .globalOperationParameters(pars)  
            .apiInfo(smpApiInfo());
          //  .enable(enableSwagger);  
    } 
  
    private ApiInfo smpApiInfo() {
        return new ApiInfoBuilder()
            .title("King Fast Dev Platform")
            .description("接口文档。提示：Try it out时请输入当前用户的token。分页查询接口传参【query】约定；"
            + "page:页码，limit:每页大小，sidx:排序字段(非必填)，order:排序，searchKey:模糊查询内容，keyParam:模糊查询的属性列，"
            + "property(属性列):精确查询内容，property(属性列): {begin:范围查询开始,end:范围查询结束}。例子:"
            + "limit=10&page=1&sidx=id&order=asc&searchKey=&keyParam=[\"username\",\"ip\",\"operation\",\"method\"]&status=&createDate={\"begin\":\"2018-05-21\",\"end\":\"2018-05-25\"}")
            .termsOfServiceUrl("https://github.com/3zamn/kingMicro")
            .version("1.0.0")
            .build();
    }
    
    /**
     * 云盘服务
     * @return
     */
    @Bean
    public Docket ossApi(){  
    	String enableSwagger =sysConfigService.getValue("SWAGGER_ENABLE");
        ParameterBuilder tokenPar = new ParameterBuilder();  
        List<Parameter> pars = new ArrayList<Parameter>();  
        tokenPar.name("token").description("令牌").modelRef(new ModelRef("string")).parameterType("header").required(true).build();  
        pars.add(tokenPar.build());  
        return new Docket(DocumentationType.SWAGGER_2)  
        	.groupName("2_ossApi")  
        	.enable(enableSwagger!=null?enableSwagger.equals("true"):false)
            .select()  
            .apis(RequestHandlerSelectors.basePackage("com.king.rest.oss"))  
            .paths(PathSelectors.any())
            .build()  
            .globalOperationParameters(pars)  
            .apiInfo(ossApiInfo());
          //  .enable(enableSwagger);  
    } 
  
    private ApiInfo ossApiInfo() {
        return new ApiInfoBuilder()
            .title("OSS云服务")
            .description("接口文档。提示：Try it out时请输入当前用户的token")
            .termsOfServiceUrl("https://github.com/3zamn/kingMicro")
            .version("1.0.0")
            .build();
    }

    //第三方应用api
    @Bean
    public Docket thirdappApi(){  
    	String enableSwagger =sysConfigService.getValue("SWAGGER_ENABLE");
        ParameterBuilder tokenPar = new ParameterBuilder();  
        List<Parameter> pars = new ArrayList<Parameter>();  
        tokenPar.name("token").description("令牌").modelRef(new ModelRef("string")).parameterType("header").required(true).build();  
        pars.add(tokenPar.build());  
        return new Docket(DocumentationType.SWAGGER_2)  
        	.groupName("3_thirdappApi")  
        	.enable(enableSwagger!=null?enableSwagger.equals("true"):false)
            .select()  
        //    .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)) 
            .apis(RequestHandlerSelectors.basePackage("com.king.app.controller"))  
        //    .apis(RequestHandlerSelectors.any())  
         //.paths(regex("/api.*"))   
            .paths(PathSelectors.any())
            .build()  
            .globalOperationParameters(pars)  
            .apiInfo(thirdappApiInfo());
          //  .enable(enableSwagger);  
    } 
  
    private ApiInfo thirdappApiInfo() {
        return new ApiInfoBuilder()
            .title("第三方应用接入api")
            .description("接口文档。提示：Try it out时请输入当前用户的token")
            .termsOfServiceUrl("https://github.com/3zamn/kingMicro")
            .version("1.0.0")
            .build();
    }
    

}