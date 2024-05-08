package com.projak.DMS;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import com.filenet.api.core.Factory;
import com.ibm.ecm.extension.PluginRequestFilter;
import com.ibm.ecm.extension.PluginRequestUtil;
import com.ibm.ecm.extension.PluginResponseFilter;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.ecm.json.JSONMessage;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONArtifact;
import com.ibm.json.java.JSONObject;

/**
 * This sample filter modifies the
 * 
 * search request to demonstrate the capabilities:
 * <ol>
 * <li>To
 * 
 * add a request parameter. The set parameter will be accessed in the
 * 
 * SamplePluginResponseFilter to update some user response.</li>
 * 
 * </ol>
 * To prevent the results changes from always happening,
 * 
 * the logic will only take effect if the desktop's id is "sample".
 * 
 * 
 */

public class DocumentUploadRestrictionRequestFilter extends PluginRequestFilter {

	@Override
	public String[]	getFilteredServices() {
		return new String[] { "/p8/addItem", "/p8/checkIn" };
	}

	@Override
	public JSONObject filter(PluginServiceCallbacks callbacks,HttpServletRequest request, JSONArtifact jsonRequest) throws	Exception {
		String desktopId = request.getParameter("desktop");
		System.out.println("Header Info: "+request.getHeader("Content-Type"));
		String template_name = request.getParameter("template_name");
		System.out.println("Template name"+template_name);
		System.out.println("Desktop Id"+desktopId);
		ResourceBundle rs = getResourceBundle();
		System.out.println("asdf" + request.getParameter("Content-Type"));
		String mimetype = request.getParameter("mimetype");
		String filename = request.getParameter("parm_part_filename");
		System.out.println("MIME type "+mimetype);
		System.out.println("FileName: "+filename);
		//if (desktopId != null && desktopId.equals(rs.getString("desktop_name"))) {
		if (desktopId != null && desktopId.equals("HRDMS")) {
			JSONObject json = null;

			JSONArray jsonRequestArray = (JSONArray) jsonRequest;

			try {
//				String template_name = request.getParameter("template_name");
				//if(template_name.equalsIgnoreCase(rs.getString("template_name"))) {
					System.out.println("Template name"+template_name);
					//String mimetype = request.getParameter("mimetype");
					//String filename = request.getParameter("parm_part_filename");
					
					String fileExt = "";
					if(filename.contains(".")){
						fileExt = filename.substring(filename.lastIndexOf(".") + 1);
						System.out.println("fileExt:"+fileExt);
					}
					List<String> validFileExtensionsdArrayList = Arrays.asList(rs.getString("mime_types").split(",")); // setup your list of file
													// ext here

					// only allow file extension in predefined list
					System.out.println("Valid files "+validFileExtensionsdArrayList);
					if (!validFileExtensionsdArrayList.contains(mimetype.toLowerCase())) {
						JSONMessage	errorMessage = new JSONMessage(10001, "Invalid file type:" + fileExt,
								"Only valid file type are allowed for upload.", "Please choose a valid file type.", "",
								"");

						JSONArray jsonMessages = new JSONArray();

						jsonMessages.add(errorMessage);

						if (jsonRequest != null) {
							json = (JSONObject) ((JSONArray) jsonRequest).get(0);

							json.put("errors", jsonMessages);

							// System.out.println("json="+json.toString());

							return json;
						}
					}

				//}

			} catch(Exception e) {

				System.out.println("Exception in DocumentUploadRestrictionRequestFilter" + e.fillInStackTrace());

			}

		} // aks

		return null;
	}

	public  ResourceBundle getResourceBundle() 
	  {
		 ResourceBundle rsbundle = null;
		
		try {
			 rsbundle = ResourceBundle.getBundle("config");
						
		} catch (Exception e) {
			System.out.println("Property File Not Found "+e.fillInStackTrace());
					
	  }
		return rsbundle;
}

}
