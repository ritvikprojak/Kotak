package com.sb.filenet.genericapi.filter;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.springframework.boot.autoconfigure.cache.CacheProperties.Caffeine;
import org.springframework.http.HttpStatus;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

public class RequestThrottleFilter implements Filter {
	private int MAX_REQUESTS_PER_SECOND = 5;

	private LoadingCache<String, Integer> requestCountsPerIpAddress;

	public RequestThrottleFilter() {
		super();
		requestCountsPerIpAddress = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.SECONDS)
				.build(new CacheLoader<String, Integer>() {

					public Integer load(String key) {

						return 0;

					}

				});
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
		HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
		String clientIpAddress = getClientIP((HttpServletRequest) servletRequest);
		try {
			if (isMaximumRequestsPerSecondExceeded(clientIpAddress)) {
				httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
				httpServletResponse.getWriter().write("Too many requests");
				return;
			}
		} catch (ExecutionException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); // TODO: remove this
		}

		filterChain.doFilter(servletRequest, servletResponse);
	}

	private boolean isMaximumRequestsPerSecondExceeded(String clientIpAddress) throws ExecutionException {
		Integer requests = 0;
		requests = requestCountsPerIpAddress.get(clientIpAddress);
		if (requests != null) {
			if (requests > MAX_REQUESTS_PER_SECOND) {
				requestCountsPerIpAddress.asMap().remove(clientIpAddress);
				requestCountsPerIpAddress.put(clientIpAddress, requests);
				return true;
			}

		} else {
			requests = 0;
		}
		requests++;
		requestCountsPerIpAddress.put(clientIpAddress, requests);
		return false;
	}

	public String getClientIP(HttpServletRequest request) {
		String xfHeader = request.getHeader("X-Forwarded-For");
		if (xfHeader == null) {
			return request.getRemoteAddr();
		}
		return xfHeader.split(",")[0];
	}

	@Override
	public void destroy() {

	}
}
