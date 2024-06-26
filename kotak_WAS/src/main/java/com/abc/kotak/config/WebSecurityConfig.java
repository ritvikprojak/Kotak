package com.abc.kotak.config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import com.abc.kotak.security.AuthoritiesConstants;
import com.abc.kotak.security.jwt.JWTConfigurer;
import com.abc.kotak.security.jwt.TokenProvider;
//import com.abc.kotak.web.rest.util.AES;
import com.sun.org.apache.xerces.internal.parsers.SecurityConfiguration;

@Configuration
@Import(SecurityProblemSupport.class)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private final AuthenticationManagerBuilder authenticationManagerBuilder;

	private final UserDetailsService userDetailsService;

	private final TokenProvider tokenProvider;

	private final CorsFilter corsFilter;

	private final SecurityProblemSupport problemSupport;

	private final Logger log = LoggerFactory.getLogger(SecurityConfiguration.class);

	@Autowired
	private Environment env;

	public WebSecurityConfig(AuthenticationManagerBuilder authenticationManagerBuilder,
			UserDetailsService userDetailsService, TokenProvider tokenProvider, CorsFilter corsFilter,
			SecurityProblemSupport problemSupport) {
		this.authenticationManagerBuilder = authenticationManagerBuilder;
		this.userDetailsService = userDetailsService;
		this.tokenProvider = tokenProvider;
		this.corsFilter = corsFilter;
		this.problemSupport = problemSupport;
	}

	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@PostConstruct
	public void init() {
		try {
			log.info("SecurityConfiguration init() begin");
			authenticationManagerBuilder.ldapAuthentication().userDnPatterns(env.getProperty("ldap.userDnPatterns"))
					.userSearchFilter(env.getProperty("ldap.userSearchFilter"))
					.groupSearchBase(env.getProperty("ldap.groupSearchBase"))
					.groupSearchFilter(env.getProperty("ldap.groupSearchFilter")).contextSource()
					.url(env.getProperty("ldap.url")).managerDn(env.getProperty("ldap.managerDn"))
					.managerPassword(env.getProperty("ldap.managerPassword"));
			log.info("SecurityConfiguration init() end");

		} catch (Exception e) {
			throw new BeanInitializationException("Security configuration failed", e);
		}
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class).exceptionHandling()
				.authenticationEntryPoint(problemSupport).accessDeniedHandler(problemSupport).and().csrf().disable()
				.headers().frameOptions().disable().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
				.antMatchers("/api/register").permitAll().antMatchers("/api/activate").permitAll()
				.antMatchers("/api/authenticate").permitAll().antMatchers("/api/account/reset-password/init")
				.permitAll().antMatchers("/api/account/reset-password/finish").permitAll()
				.antMatchers("/api/profile-info").permitAll().antMatchers("/api/**").authenticated()
				.antMatchers("/management/health").permitAll().antMatchers("/management/**")
				.hasAuthority(AuthoritiesConstants.ADMIN).and().apply(securityConfigurerAdapter());

	}

	private JWTConfigurer securityConfigurerAdapter() {
		return new JWTConfigurer(tokenProvider);
	}

}
