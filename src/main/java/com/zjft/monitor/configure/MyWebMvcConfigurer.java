package com.zjft.monitor.configure;

import com.zjft.monitor.interceptor.MyHandlerInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by hqhan on 2018/10/24.
 */
@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {

    /**增加配置用户自定义的静态资源路径**/
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/pic/**").addResourceLocations("classpath:/my/");
    }

    /**
     * 页面跳转功能
     * 以前要访问一个页面需要先创建个Controller控制类，再写方法跳转到页面
     * 在这里配置后就不需要那么麻烦了，直接访问http://localhost:8080/toLogin就跳转到login.htm页面了
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/hello").setViewName("login");
    }

    /**
     * 添加拦截器
     * addPathPatterns 拦截指定路径
     * excludePathPatterns 排除某些路径
     * */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /**addPathPatterns("/**")对所有请求都拦截，但是排除了/toLogin和/login请求的拦截**/
        registry.addInterceptor(new MyHandlerInterceptor()).addPathPatterns("/**").excludePathPatterns("/login");
    }
}
