package com.jktickets.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import com.jktickets.context.LoginMemberContext;
import com.jktickets.res.MemberLoginRes;
import com.jktickets.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 拦截器：Spring框架特有的，常用于登录校验，权限校验，请求日志打印
 */
@Component
public class MemberInterceptor implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(MemberInterceptor.class);


//    拦截header 中的token
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取header的token参数
        String token = request.getHeader("token");
        if (StrUtil.isNotBlank(token)) {
            LOG.info("获取会员登录token：{}", token);
//            将token转成JSONObject
            JSONObject loginMember = JwtUtil.getJSONObject(token);
            LOG.info("当前登录会员：{}", loginMember);
//            JSONObject转成MemberLoginRes
            MemberLoginRes member = JSONUtil.toBean(loginMember, MemberLoginRes.class);
//            存储在线程的ThreadLocal 中去
            LoginMemberContext.setMember(member);
        }
        return true;
    }

}
