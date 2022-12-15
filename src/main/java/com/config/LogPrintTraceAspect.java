package com.config;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


/**
 * 系统日志，切面处理类
 */
@Slf4j
@Aspect
@Component
public class LogPrintTraceAspect {
    private final static String TRACE_ID = "TRACE_ID";

    @Autowired
    HttpServletRequest request;

    public void setTraceId() {
        if (request != null && request.getHeader("Trace-Id") != null) {
            MDC.put(TRACE_ID, request.getHeader("Trace-Id"));
        } else {
            MDC.put(TRACE_ID, UUID.randomUUID().toString().replaceAll("-", ""));
        }
    }

    @Pointcut("execution(public * com.print.controller..*.*(..)) ")
    public void sysLogTracePointCut() {

    }

    // @Pointcut("execution(public * com.app.controller..*.*(..)) ")
    // public void appLogTracePointCut() {
    //
    // }

    // @Around("sysLogTracePointCut() || appLogTracePointCut()")
    @Around("sysLogTracePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // 设置日志追踪 setTraceId();
        setTraceId();
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
