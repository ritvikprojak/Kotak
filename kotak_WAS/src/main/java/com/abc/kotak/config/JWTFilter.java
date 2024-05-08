/*package com.abc.kotak.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import com.abc.kotak.security.jwt.TokenProvider;
import com.abc.kotak.service.JwtUserDetailsService;

@Component
public class JWTFilter extends GenericFilterBean {

	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	private TokenProvider tokenProvider;
	
	
	
	
	 public JWTFilter(TokenProvider tokenProvider) {
		super();
		this.tokenProvider = tokenProvider;
	}

	@Override
	    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
	        throws IOException, ServletException {
	        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
	        HttpServletResponse response = (HttpServletResponse) servletResponse;
	        
	        response.setHeader("Access-Control-Allow-Origin", "*");

	        if ("OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
	            response.setStatus(HttpServletResponse.SC_OK);
	            response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
	            response.setHeader("Access-Control-Max-Age", "3600");
	            response.setHeader("Access-Control-Allow-Headers", "*");
	            response.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Origin, Access-Control-Allow-Credentials, Accept, AccessControlAllowMethods, authorization, Content-Type, x-requested-with ");
	        }
	        String jwt = resolveToken(httpServletRequest);
	        if (StringUtils.hasText(jwt) && this.tokenProvider.validateToken(jwt)) {
	            Authentication authentication = this.tokenProvider.getAuthentication(jwt);
	            SecurityContextHolder.getContext().setAuthentication(authentication);
	        }
	        filterChain.doFilter(servletRequest, servletResponse);
	    }

	    private String resolveToken(HttpServletRequest request){
	        String bearerToken = request.getHeader(JWTConfigurer.AUTHORIZATION_HEADER);
	        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
	            return bearerToken.substring(7, bearerToken.length());
	        }
	        return null;
	    }

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		final String requestTokenHeader = request.getHeader("Authorization");
		HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        response.setHeader("Access-Control-Allow-Origin", "*");


		String username = null;
		String jwtToken = null;
		// JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
		
		 if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
	            response.setStatus(HttpServletResponse.SC_OK);
	            response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
	            response.setHeader("Access-Control-Max-Age", "3600");
	            response.setHeader("Access-Control-Allow-Headers", "*");
	            response.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Origin, Access-Control-Allow-Credentials, Accept, AccessControlAllowMethods, authorization, Content-Type, x-requested-with ");
	        } 
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			jwtToken = requestTokenHeader.substring(7);
			try {
				username = jwtTokenUtil.getUsernameFromToken(jwtToken);
			} catch (IllegalArgumentException e) {
				System.out.println("Unable to get JWT Token");
			} catch (ExpiredJwtException e) {
				System.out.println("JWT Token has expired");
			}
		} else {
			logger.warn("JWT Token does not begin with Bearer String");
		}

		//Once we get the token validate it.
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

			// if token is valid configure Spring Security to manually set authentication
			if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				usernamePasswordAuthenticationToken
						.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				// After setting the Authentication in the context, we specify
				// that the current user is authenticated. So it passes the Spring Security Configurations successfully.
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
		chain.doFilter(request, response);
	}

}
*/