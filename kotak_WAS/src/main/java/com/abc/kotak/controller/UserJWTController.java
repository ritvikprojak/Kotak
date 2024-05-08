package com.abc.kotak.controller;

import java.util.Date;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abc.kotak.config.ApplicationProperties;
import com.abc.kotak.security.AuthoritiesConstants;
import com.abc.kotak.security.SecurityUtils;
import com.abc.kotak.security.jwt.JWTConfigurer;
import com.abc.kotak.security.jwt.TokenProvider;
import com.abc.kotak.web.rest.vm.LoginVM;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class UserJWTController {

	private final TokenProvider tokenProvider;
	private final AuthenticationManager authenticationManager;
	private final Logger log = LoggerFactory.getLogger(UserJWTController.class);
	// private final SecurityUtils securityUtils;
	// private final ApplicationProperties applicationProperties;

	private String roleManagement = AuthoritiesConstants.SMS; //ROLE_FN_SMS

	private String docManagement = AuthoritiesConstants.HRM;  //ROLE_HR_ADMINS

	public UserJWTController(TokenProvider tokenProvider,
			AuthenticationManager authenticationManager) {
		this.tokenProvider = tokenProvider;
		this.authenticationManager = authenticationManager;
		// this.securityUtils=securityUtils;
		// this.applicationProperties= applicationProperties;
	}

	@PostMapping("/authenticate")
	// @Timed
	public ResponseEntity<JWTToken> authorize(
			@RequestBody LoginVM loginVM) {
		String jwt = null;
		HttpHeaders httpHeaders = new HttpHeaders();
		try{
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
					loginVM.getUsername(), loginVM.getPassword());

			log.info("authorize begin");

			log.info("SMS group " + roleManagement + " HR group " + docManagement);

			log.info("authorize LDAP System for - " + loginVM.getUsername()
					+ " - Time - " + new Date());
			Authentication authentication = this.authenticationManager
					.authenticate(authenticationToken);

			SecurityContextHolder.getContext().setAuthentication(authentication);
			

			//UserDetails userdetail = SecurityUtils.getCurrentUserDetails();
			if (SecurityUtils.isCurrentUserInRole(roleManagement) && SecurityUtils.isCurrentUserInRole(docManagement)) {
				log.info("authorize LDAP System authentication successful  for - "
						+ loginVM.getUsername());
				String groups = roleManagement + "," + docManagement;
				httpHeaders.add("GroupName", groups);
			} else if (SecurityUtils.isCurrentUserInRole(docManagement)) {
				log.info("authorize LDAP System authentication successful  for - "
						+ loginVM.getUsername());
				httpHeaders.add("GroupName", docManagement);
			}else if (SecurityUtils.isCurrentUserInRole(roleManagement)) {
				log.info("authorize LDAP System authentication successful  for - "
						+ loginVM.getUsername());
				httpHeaders.add("GroupName", roleManagement);
			} else{
				log.info("Unauthorized - " + loginVM.getUsername());
				httpHeaders.add("error", "Unauthorized : Bad Credentials");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);	
			}

			log.info("authorize LDAP System authentication successful  for - "
					+ loginVM.getUsername());
			
			boolean rememberMe = (loginVM.isRememberMe() == null) ? false : loginVM
					.isRememberMe();
			jwt = tokenProvider.createToken(authentication, rememberMe);
			
			httpHeaders.add(JWTConfigurer.AUTHORIZATION_HEADER, "Bearer " + jwt);

			log.info("authorize end");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return new ResponseEntity<>(
				new JWTToken(
						jwt,httpHeaders.get("GroupName").toString()), 
				httpHeaders,
				HttpStatus.OK);
	}

	/**
	 * Object to return as body in JWT Authentication.
	 */
	static class JWTToken {

		private String idToken;
		private String groupName;

		JWTToken(String idToken , String groupName) {
			this.idToken = idToken;
			this.groupName = groupName;
		}
		
		
		@JsonProperty("Group_Name")
		public String getGroupName() {
			return groupName;
		}



		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}



		@JsonProperty("id_token")
		String getIdToken() {
			return idToken;
		}

		void setIdToken(String idToken) {
			this.idToken = idToken;
		}
	}
}
