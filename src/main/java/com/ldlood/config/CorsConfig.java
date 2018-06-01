package com.ldlood.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 
* @Description: 解决跨域问题
* @Author: DJ 
* @Date: 2018年5月30日 下午6:16:12  
* @Version: V1.0
 */
@Configuration
public class CorsConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*")
                .allowedMethods("GET", "POST")
                .allowCredentials(true).maxAge(3600);
    }


}