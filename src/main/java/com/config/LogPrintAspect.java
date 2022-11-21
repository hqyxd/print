package com.config;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * 系统日志，切面处理类
 */
@Slf4j
@Aspect
@Component
public class LogPrintAspect {


    @Pointcut("@annotation(com.config.LogPrint)")
    public void logPointCut() {

    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object result = point.proceed();
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
        Map<String, String[]> parameterMap = request.getParameterMap();
        log.info("请求类名:{},请求方法名:{}",
                point.getSignature().getDeclaringTypeName(),
                point.getSignature().getName());
        log.info("请求URL:{},请求 IP:{},请求头参数:{},请求参数:{}",
                request.getRequestURL(),
                request.getRemoteAddr(),
                JSONObject.toJSONString(getHeaders(request)),
                JSONObject.toJSONString(parameterMap));
        long beginTime = System.currentTimeMillis();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;

        log.info("执行时长(毫秒):{}", time);
        log.info("接口返回值:{}", JSONObject.toJSONString(result));
        return result;
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headerMap = new HashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            headerMap.put(name, value);
        }
        return headerMap;
    }
}
