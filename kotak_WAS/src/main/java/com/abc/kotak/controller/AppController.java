/*package com.abc.kotak.controller;

import javax.servlet.annotation.HttpConstraint;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abc.kotak.form.logonForm;
import com.abc.kotak.ldap.UpmLdapEvents;
import com.abc.kotak.model.JwtRequest;



@RestController
@CrossOrigin
public class AppController {

	private static Logger log =LoggerFactory.getLogger(DocumentController.class);
	private static final String ENTITY_NAME = "LogonService";
	
	@PostMapping(value = "/logon")
	public ResponseEntity<String> submitLogon(Model model,@RequestBody JwtRequest logonForm,HttpServletRequest request)
	{
		log.debug(" userName "+logonForm.getUsername());
		//log.debug(" password "+logonForm.getPassword());
		String responseMessage= null;
		
		String responsePage = null;
		UpmLdapEvents ldapEvents = new UpmLdapEvents();
		String userLoggedIn = null;
		try {
			//boolean flag = ldapEvents.userAuthentication(logonForm.getUserName(), logonForm.getPassword());
			userLoggedIn =  ldapEvents.userAuthentication(logonForm.getUsername(), logonForm.getPassword());
			if(null!=userLoggedIn)
			{
				model.addAttribute("responseMessage",responseMessage);
				HttpSession session = request.getSession();
				session.setAttribute("userName", logonForm.getUsername());
				session.setAttribute("userLoggedIn", userLoggedIn);
				String clintHost = request.getRemoteHost();
				log.debug("clintHost "+clintHost);
				String clientIP = request.getRemoteAddr();
				log.debug("clientIP "+clientIP);
				log.debug(logonForm.getUsername()+"	Logged In from IpAdress "+clientIP);
				new ResponseEntity<>("Success ", HttpStatus.OK);
			}
			else
			{
				//responseMessage= "User doesn't exists, please check username and password";
				responseMessage= "Administrator or BranchManager are allowed to logon";
				model.addAttribute("responseMessage",responseMessage);
				new ResponseEntity<>("Administrator or BranchManager are allowed to logon ", HttpStatus.UNAUTHORIZED);
			}
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage = "Unable to login";
			model.addAttribute("responseMessage",responseMessage);
			new ResponseEntity<>("Unable to login", HttpStatus.BAD_REQUEST);
			
		}
		
		return new ResponseEntity<>("Success ", HttpStatus.OK);
	}
}
*/