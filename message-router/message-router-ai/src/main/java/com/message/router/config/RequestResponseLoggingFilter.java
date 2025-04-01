package com.message.router.config;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 请求响应日志过滤器
 * 
 * @author rcloud
 * @since 2025-03-31
 */
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        
        // 记录请求信息
        StringBuilder requestLog = new StringBuilder();
        requestLog.append("Request: ").append(request.getMethod()).append(" ").append(request.getRequestURI());
        
        // 添加查询参数
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry: parameterMap.entrySet()) {
            String key =  entry.getKey();
            String[] values = entry.getValue();
            if (values != null && values.length > 0) {
                requestLog.append("\n").append(key).append(": ").append(values[0]);
            }
        }
        
        
        // 添加请求头
        requestLog.append("\nHeaders: ");
        Collections.list(request.getHeaderNames()).forEach(headerName -> 
            requestLog.append(headerName).append(": ").append(request.getHeader(headerName)).append(", ")
        );
        
        // 添加请求体
        String requestBody = new String(requestWrapper.getContentAsByteArray());
        if (!requestBody.isEmpty()) {
            requestLog.append("\nBody: ").append(truncateBody(requestBody));
        }
        
        logger.info(requestLog.toString());
        
        // 继续处理请求
        filterChain.doFilter(requestWrapper, responseWrapper);
        
        // 记录响应信息
        StringBuilder responseLog = new StringBuilder();
        responseLog.append("Response: ").append(response.getStatus());
        
        // 添加响应头
        responseLog.append("\nHeaders: ");
        response.getHeaderNames().forEach(headerName -> 
            responseLog.append(headerName).append(": ").append(response.getHeader(headerName)).append(", ")
        );
        
        // 添加响应体
        String responseBody = new String(responseWrapper.getContentAsByteArray());
        if (!responseBody.isEmpty()) {
            responseLog.append("\nBody: ").append(truncateBody(responseBody));
        }
        
        logger.info(responseLog.toString());
        
        // 将响应体写回
        responseWrapper.copyBodyToResponse();
    }
    
    private String truncateBody(String body) {
        if (body.length() > 2048) {
            return body.substring(0, 2048) + "...";
        }
        return body;
    }
} 