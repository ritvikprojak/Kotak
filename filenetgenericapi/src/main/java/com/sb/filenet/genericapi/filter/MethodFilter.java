package com.sb.filenet.genericapi.filter;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class MethodFilter extends OncePerRequestFilter {
	
	 private final String[] allowedMethods = new String[]{ "POST" };

	    @Override
	    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	            throws ServletException, IOException {
	        if (Arrays.stream(allowedMethods).noneMatch(x -> x.equals(request.getMethod()))) {
	            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	        }
	        filterChain.doFilter(request, response);
	    }
	}
	