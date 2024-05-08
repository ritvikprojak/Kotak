package com.sb.filenet.genericapi.filter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
//import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class CrossScriptingFilter implements Filter
{
    @Override
    public void init(FilterConfig filterConfig) {

    }

    private final Logger logger = LoggerFactory.getLogger(CrossScriptingFilter.class);
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.info("filter executed: {}", ((HttpServletRequest) request).getRequestURL().toString());
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, "no");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setHeader("Cache-Control", "no-cache, no-store, no-control, must-revalidate");
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "https://10.240.20.22:9443");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With,Content-Disposition, Content-Type, Accept");
        httpServletResponse.setHeader("x-content-type-options","nosniff");
        httpServletResponse.setHeader("X-XSS-Protection","1; mode=block");
        httpServletResponse.setHeader("Strict-Transport-Security","max-age=3153600; includeSubDomains");
        httpServletResponse.setHeader("X-Frame-Options","sameorigin");
        httpServletResponse.setHeader("Content-Security-Policy","default src 'self', Script-src, Object-src");
       
        
        HttpServletRequest Irequest = (HttpServletRequest) request;
        if (Irequest.getMethod().equals("OPTIONS") || HttpMethod.TRACE.name().equals(Irequest.getMethod())) {
        	httpServletResponse.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
          }else
          {
        	  chain.doFilter(request, response);  
          }
        
    }

    @Override
    public void destroy() {

    }
}