package com.jktickets.config;

import com.jktickets.interceptor.LogInterceptor;

import com.jktickets.interceptor.MemberInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


//开启拦截器
//一般开启拦截器 都是拦截token 然后存取 token中的payload
@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

//日志拦截器
    @Resource
    LogInterceptor logInterceptor;

   @Resource
   MemberInterceptor memberInterceptor;

   @Override
   public void addInterceptors(InterceptorRegistry registry) {
       registry.addInterceptor(logInterceptor);

       registry.addInterceptor(memberInterceptor)
               .addPathPatterns("/**")
               .excludePathPatterns(
                       "/member/hello",
                       "/member/member/send-code",
                       "/member/member/login"
               );
   }
}
