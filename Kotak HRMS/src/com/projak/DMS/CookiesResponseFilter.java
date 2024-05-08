package com.projak.DMS;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ecm.extension.PluginResponseFilter;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.ecm.extension.PluginServletResponseWrapper;
import com.ibm.json.java.JSONObject;

public class CookiesResponseFilter extends PluginServletResponseWrapper{

	public CookiesResponseFilter(HttpServletResponse response) {
		super(response);
		// TODO Auto-generated constructor stub
	}
	
	public HttpServletResponse filterService() {
		
		return null;
	}



}
