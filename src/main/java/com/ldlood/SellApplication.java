package com.ldlood;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 修改启动类，继承 SpringBootServletInitializer 并重写 configure 方法
 * 具体见：https://blog.csdn.net/huangyaa729/article/details/78031337
 */
@SpringBootApplication
@MapperScan(basePackages = "com.ldlood.dataobject.mapper")
@EnableCaching
public class SellApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SellApplication.class, args);
    }


    /**
     * 新增方法
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // 指向main方法执行的Application启动类
        return builder.sources(SellApplication.class);
    }
}
