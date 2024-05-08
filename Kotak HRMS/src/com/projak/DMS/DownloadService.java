package com.projak.DMS;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.filenet.api.core.Factory;
import com.filenet.api.core.Factory.Event;
import com.filenet.api.query.SearchSQL;
import com.filenet.apiimpl.query.Search;
import com.filenet.apiimpl.query.SearchScope;
import com.ibm.ecm.extension.Plugin;
import com.ibm.ecm.extension.PluginRequestFilter;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.ecm.json.JSONViewoneBootstrapResponse;
import com.ibm.json.java.JSONArtifact;
import com.ibm.json.java.JSONObject;

public class DownloadService extends PluginRequestFilter {

	int i;
	static int k;
	Integer r;
	
	@Override
	public JSONObject filter(PluginServiceCallbacks arg0,
			HttpServletRequest arg1, JSONArtifact arg2) throws Exception {
		// TODO Auto-generated method stub
		SearchSQL sql = new SearchSQL();
		return null;
	}

	@Override
	public String[] getFilteredServices() {
		// TODO Auto-generated method stub
		return new String[] { "/p8/getViewoneBootstrap" };

		
	}
	/* public int integer (){
		 int k;
		 return k;
	 }*/
	
	/*public static void main(String []args){
		int j;
		Integer t;
		DownloadService d = new DownloadService();
		
		System.out.println("j is " + j);
		System.out.println("i is " + d.i);
		System.out.println("k is " + k);
		System.out.println("R is " + d.r );
		System.out.println("T is " + t);
	}*/

}
