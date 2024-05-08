package com.sb.filenet.genericapi.controller;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.sb.filenet.genericapi.exception.FNException;
import com.sb.filenet.genericapi.request.GetDocumentRequest;
import com.sb.filenet.genericapi.request.Request;
import com.sb.filenet.genericapi.response.Response;
import com.sb.filenet.genericapi.response.RetrieveDocResponse;
import com.sb.filenet.genericapi.response.RetrieveDocsResponse;
import com.sb.filenet.genericapi.response.Status;
import com.sb.filenet.genericapi.service.IAttribute;
import com.sb.filenet.genericapi.service.ICEAdapter;
import com.sb.filenet.genericapi.service.ICESession;
import com.sb.filenet.genericapi.service.impl.CEAdapter;
import com.sb.filenet.genericapi.service.impl.DocAttribute;
import com.sb.filenet.genericapi.util.CripUtils;
import com.sb.filenet.genericapi.util.DataType;

@CrossOrigin("https://10.240.20.22:9443")
@RestController
@RequestMapping("/rest/api")
public class FilenetGenericAPIController {

	private final Logger log = Logger.getLogger(FilenetGenericAPIController.class);
	

	private final Gson gson = new Gson();
	private static final ICEAdapter ceAdapter = CEAdapter.getInstance();

	@Autowired
	Properties systemProperties;

	@PostMapping(value = "/addDocument")
	public Response addDocument(@RequestParam("file") MultipartFile file, @RequestParam("objectStore") String objectStore, @RequestParam("documentClass") String documentClass,
			@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("properties") String properties)  {

		if (log.isInfoEnabled()) {
			log.info("<< ce Property URI ::" + systemProperties.getProperty("URI"));
			log.info("<< ce Property Stanza ::" + systemProperties.getProperty("Stanza"));
			log.info("FileName ::" + file.getOriginalFilename());
			log.info("objectStore ::" + objectStore);
			log.info("documentClass ::" + documentClass);

		}
		ICESession ceSession = null;
		Response response =  new Response();
		Status status =  new Status("0", "Success", null);
		try {
			if(systemProperties.getProperty("ENC").equalsIgnoreCase("yes"))
			{
				String secretKey = systemProperties.getProperty("SECRETKEY");
				username = CripUtils.decryptStr(username,secretKey);
				password = CripUtils.decryptStr(password,secretKey);

				log.info("username aftre decryption ::" + username);
				log.info("password aftre decryption ::" + password);
			}else
			{
				log.info("username ::" + username);
				log.info("password ::" + password);
			}
			List<IAttribute> attributeList = getAttributeList(properties);
			attributeList.add(new DocAttribute("MimeType", "image/jpeg", false, DataType.STRING));
			systemProperties.put("ObjectStoreName", objectStore);
			ceSession = ceAdapter.connect(username, password, systemProperties);
			if (log.isInfoEnabled())
				log.info("<< ceSession created Successfully ::");
			String originalFilename =  file.getOriginalFilename();
			String docId = ceAdapter.importDocument(ceSession, file.getInputStream(), attributeList, documentClass, originalFilename);
			response.setDocId(docId);
			response.setStatus(status);
			ceAdapter.disconnect(ceSession);
		}catch(Exception e){
			e.printStackTrace();
			status.setCode("1");
			status.setMessage("Failed to Upload Document");
			status.setTrace(e.getCause().getMessage());
			response.setStatus(status);

		}
		finally
		{
			ceAdapter.disconnect(ceSession);
		}
		if (log.isInfoEnabled())
			log.info("<< file imported Successfully ::");

		return  response;
	}

	@PostMapping(value = "/retrieveDocument", consumes = "application/json")
	public ResponseEntity<StreamingResponseBody> retrieveDocument(@RequestBody Request request)  {

		if (log.isInfoEnabled())
			log.info("<< retrieveDocument requestJson ::" + gson.toJson(request));
		ICESession ceSession = null;
		Response errorResponse =  new Response();
		Status status =  new Status("0", "Success", null);
		try {
			systemProperties.put("ObjectStoreName", request.getObjectStore());
			ceSession = ceAdapter.connect(request.getUsername(), request.getPassword(), systemProperties);

			if (log.isInfoEnabled())
				log.info("<< ceSession created Successfully ::");
			RetrieveDocResponse response = ceAdapter.getContentAsStream(ceSession, request.getDocId());
			StreamingResponseBody responseBody = outputStream -> {
				int numberOfBytesToWrite;
				byte[] data = new byte[1024];
				while ((numberOfBytesToWrite = response.getFileContent().read(data, 0, data.length)) != -1) {
					System.out.println("Writing some bytes..");
					outputStream.write(data, 0, numberOfBytesToWrite);
				}
				response.getFileContent().close();
			};
			if (log.isInfoEnabled())
				log.info("<< file downloaded Successfully ::");
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + response.getFileName() + "")
					.contentType(MediaType.APPLICATION_OCTET_STREAM).body(responseBody);
		}catch(Exception e) {
			e.printStackTrace();
			status.setCode("1");
			status.setMessage("Failed to Retrieve Document");
			status.setTrace(e.getCause().getMessage());
			errorResponse.setStatus(status);
			ceAdapter.disconnect(ceSession);
			StreamingResponseBody responseBody = outputStream -> {
				try (Writer w = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
					w.write(gson.toJson(errorResponse));
				}
			};
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "")
					.contentType(MediaType.APPLICATION_JSON).body(responseBody);
		}
	}



	@PostMapping(value = "/getDocument")
	public RetrieveDocsResponse getDocument( @RequestBody GetDocumentRequest request)
	{
		String objectStore = request.getObjectStore();
		String documentClass = request.getDocumentClass();
		String username = request.getUsername();
		String password = request.getPassword();
		String properties = request.getProperties();
		RetrieveDocsResponse response = new RetrieveDocsResponse();
		Status status =  new Status("0", "Success", "");
		ICESession ceSession = null;
		try {
			isValidJSON(properties);

			if (log.isInfoEnabled()) {
				log.info("<< ce Property URI ::" + systemProperties.getProperty("URI"));
				log.info("<< ce Property Stanza ::" + systemProperties.getProperty("Stanza"));
				log.info("objectStore ::" + objectStore);
				log.info("documentClass ::" + documentClass);
				log.info("Encryption Enabled ?"+systemProperties.getProperty("ENC"));

			}
 


			if(systemProperties.getProperty("ENC").equalsIgnoreCase("yes"))
			{
				String secretKey = systemProperties.getProperty("SECRETKEY");
				//final String pubKey = this.systemProperties.getProperty("publickey");
				//final String privateKey = this.systemProperties.getProperty("privatekey");
				username = CripUtils.decryptStr(username,secretKey);
				password = CripUtils.decryptStr(password,secretKey);
				//EncryptionUtility enc = new EncryptionUtility(pubKey, privateKey);
				//username=enc.decrypt(username);
				//password=enc.decrypt(password);

				log.info("username aftre decryption ::" + username);
				log.info("password aftre decryption ::" + password);
			}else
			{
				log.info("username ::" + username);
				log.info("password ::" + password);
			}
			List<IAttribute> searchParameters = getAttributeList(properties);
			systemProperties.put("ObjectStoreName", objectStore);
			ceSession = ceAdapter.connect(username, password, systemProperties);

			if (log.isInfoEnabled())
				log.info("<< ceSession created Successfully ::");
			List<RetrieveDocResponse> docList = ceAdapter.searchAttachDocByAttributes(ceSession, searchParameters,documentClass);
			if(docList.size()>=0)
			{
				log.info("Inside doclist more than 0. Document list size is::"+docList.size());
				response.setRetrieveDocResponse(docList);
				response.setStatus(status);
				
			}else
			{
				status = new Status("1","No Documents Found","No Documents Found for the requested criteria");
				response.setStatus(status);
			}
			

			if (log.isInfoEnabled())
				log.info("<< file downloaded Successfully ::");



		}
		catch (FNException fns) {
			log.info("In FN Exception: "+fns.getMessage());
			status = new Status("1","Failed to Retrieve Document","Invalid Request  :"+"Kindly check your JSON request");
			response.setStatus(status);

		}
//		catch (HttpMessageNotReadableException  ex) {
//	        log.info("In HttpMessageNotReadableException");
//	        status = new Status("1","Failed to Retrieve Document","Invalid JSON request");
//			response.setStatus(status);
//		}
		catch (JSONException fns) {
			log.info("In JSONException");
			status = new Status("1","Failed to Retrieve Document","Invalid JSON");
			response.setStatus(status);

		}

		catch(Exception e) {
			log.info("Not in FN Exception");
			log.info("Exception while getting the documents",e);
			e.printStackTrace();
			status = new Status("1","Failed to Retrieve Document",e.getMessage());
			response.setStatus(status);
		}
		finally
		{
			if(null!=ceSession)
				ceAdapter.disconnect(ceSession);
		}


		return response;
	}
	private List<IAttribute> getAttributeList(String jsonStr) throws FNException {
		List<IAttribute> attributeList = new ArrayList<>();
		JsonArray convertedJsonArray = gson.fromJson(jsonStr, JsonArray.class);
		for(int i=0; i<convertedJsonArray.size(); i++){
			IAttribute attribute = gson.fromJson(convertedJsonArray.get(i), DocAttribute.class);
			log.info("attribute Name ::"+attribute.getName());
			log.info("attribute value ::"+attribute.getValue());
			Pattern pattern = Pattern.compile("[^a-zA-Z0-9-]");
	        Matcher matcher = pattern.matcher(attribute.getValue());
	        boolean isStringContainsSpecialCharacter = matcher.find();
	        if(isStringContainsSpecialCharacter)
	        {
	        	log.info("Value  of the "+attribute.getName()+ "contains special charcters" );
				throw new FNException("Value  of the "+attribute.getName()+ "contains special charcters");
	        }
			if(attribute.getValue().toString().length() <50)
			{
				attributeList.add(attribute);	
				
			}else
			{
				log.info("Value  of the "+attribute.getName()+ "is more then 50 character" );
				throw new FNException("Value  of the "+attribute.getName()+ "is more than 50 character");
			}

		}
		return attributeList;
	}
	private void isValidJSON(String properties) throws Exception
	{
		try {
			final JSONArray array = new JSONArray(properties);
			for (int i = 0; i < array.length(); ++i) {
				final JSONObject object = array.getJSONObject(i);
				final String name = object.getString("name");
				final String value = object.getString("value");
				
				
//				// Check for double-quote in name or value
//	            if (name.contains("\"") || value.contains("\"")) {
//	                throw new CustomBadRequestException("Invalid JSON: Name or value cannot contain double-quote (\").");
//	            }
			}
		}

		catch (FNException e) {
			log.error(e.getMessage() + " Invalid JSON ");

			throw new FNException("Invalid JSON");
		}
	}
}
