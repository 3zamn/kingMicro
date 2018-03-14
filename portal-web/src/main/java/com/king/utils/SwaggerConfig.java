package com.king.utils;

import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

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
/*@EnableWebMvc
@ComponentScan(basePackages = {"com.king.rest"}) */
@EnableSwagger2
public class SwaggerConfig {	
	/*@Value("#{new Boolean('${king.swagger.status}')}")
	public boolean enableSwagger;*/
	@Autowired
	private SysConfigService sysConfigService;
	
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean
    public Docket api(){  
    	String enableSwagger =sysConfigService.getValue("enableSwagger");
        ParameterBuilder tokenPar = new ParameterBuilder();  
        List<Parameter> pars = new ArrayList<Parameter>();  
        tokenPar.name("token").description("令牌").modelRef(new ModelRef("string")).parameterType("header").required(false).build();  
        pars.add(tokenPar.build());  
        return new Docket(DocumentationType.SWAGGER_2)  
        	.enable(enableSwagger!=null?enableSwagger.equals("true"):false)
            .select()  
        //    .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)) 
            .apis(RequestHandlerSelectors.any())  
         //.paths(regex("/api.*"))   
            .paths(PathSelectors.any())
            .build()  
            .globalOperationParameters(pars)  
            .apiInfo(apiInfo());
          //  .enable(enableSwagger);  
    } 
    private List<Parameter> setHeaderToken() {
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        tokenPar.name("X-Auth-Token").description("token").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        pars.add(tokenPar.build());
        return pars;
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("King Fast Dev Platform")
            .description("接口文档。提示：Try it out时请输入当前用户的token")
            .termsOfServiceUrl("https://github.com/3zamn/kingMicro")
            .version("1.0")
            .build();
    }

}