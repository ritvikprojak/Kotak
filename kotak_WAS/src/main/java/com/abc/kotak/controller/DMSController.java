package com.abc.kotak.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.abc.kotak.model.UserLogin;
import com.abc.kotak.service.DMSService;
import com.abc.kotak.service.dto.MappingDTO;
import com.abc.kotak.service.dto.SearchDTO;
import com.abc.kotak.web.rest.util.AES;
import com.abc.kotak.web.rest.util.Script;

@RestController
@CrossOrigin
@RequestMapping("/dms")
public class DMSController {

	private static Logger log = LoggerFactory.getLogger(DMSController.class);

	@Autowired
	private DMSService dmsService;

	@Autowired
	private Script script;

	@Value("${AESKey}")
	private String key;

	@Value("${nBits}")
	private int nBits;

	@RequestMapping(value = "/empValidation", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> employeeValidation(@RequestParam(value = "empId") String userId) {
		log.info("employeeValidation >>> " + userId);
		System.out.println("employeeValidation >>> " + userId);
		JSONObject jobj = new JSONObject();
		Boolean employeePresent = dmsService.validateEmployee(userId);
		log.info("employeePresent >>> " + employeePresent);
		System.out.println("employeePresent >>> " + employeePresent);
		if (employeePresent) {
			jobj.put("isEmployeePresent", employeePresent);
			System.out.println(jobj.toString());
			log.info(jobj.toString());
			System.out.println(ResponseEntity.ok().body(jobj.toString()));
			return ResponseEntity.ok().body(jobj.toString());

		}

		else {
			jobj.put("isEmployeePresent", employeePresent);
			System.out.println(ResponseEntity.ok().body(jobj.toString()));
			return ResponseEntity.ok().body(jobj.toString());
		}
	}

	@SuppressWarnings("unused")
	@RequestMapping(value = "/getUserDetailsWithAD", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getUserDetailsWithAD(@RequestParam(value = "userId") String userId,
			HttpServletRequest request) throws FileNotFoundException, NoSuchMethodException, ScriptException {
		try {
			String xForwardedForHeader = request.getHeader("X-Forwarded-For");
			System.out.println(xForwardedForHeader);
			String temp12 = request.getRemoteAddr();
			System.out.println(temp12);
		} catch (Exception ex) {
			System.out.println(ex.getStackTrace());
		}

//		String encrypttemp = script.Encryt(userId);
//		String encodetemp = encoding(encrypttemp);
//	    String temp = decoding(userId);
//		String temp = decoding(userId);
//		System.out.println(key);
//		String decodeduserId = script.DecryptResponse(temp,key, nBits);

		log.info("getUserDetailsWithAD >>> " + userId);
		System.out.println("getUserDetailsWithAD >>> " + userId);
		Date date = new Date();
		String system = "DMS";
		dmsService.addLoginTime(userId, date, system);
		System.out.println(userId + "logged at >>>>" + date);
		JSONObject userDetails = dmsService.getUserDetailsWithAD(userId);
		log.info("User Details >>> " + userDetails);
		System.out.println("User Details >>> " + userDetails);
		if (null != userDetails) {
			System.out.println(ResponseEntity.ok().body(userDetails.toString()));
			return ResponseEntity.ok().body(userDetails.toString());
		} else {
			System.out.println(ResponseEntity.ok().body(userDetails.toString()));
			return ResponseEntity.badRequest().body(userDetails.toString());
		}

	}

	@SuppressWarnings("unused")
	@RequestMapping(value = "/getUserDetailsForSearch", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getUserDetailsForSearch(@RequestParam(value = "userId") String userId)
			throws FileNotFoundException, NoSuchMethodException, ScriptException {
		String tp = script.EncrytResponse(userId, key, nBits);
		String temp = script.EncrytResponse(userId, key, nBits);
		String decodeduserId = decoding(temp);
		log.info("getUserDetailsForSearch >>> " + decodeduserId);
		System.out.println("getUserDetailsForSearch >>> " + decodeduserId);
		JSONObject userDetails = dmsService.getUserDetailForSearch(decodeduserId);
		System.out.println("userDetails for search>>> " + decodeduserId.toString());
		log.info("userDetails for search>>> " + decodeduserId.toString());
		if (null != decodeduserId) {
			System.out.println(ResponseEntity.ok().body(decodeduserId.toString()));
			return ResponseEntity.ok().body(decodeduserId.toString());
		} else {
			System.out.println(ResponseEntity.ok().body(decodeduserId.toString()));
			return ResponseEntity.badRequest().body(decodeduserId.toString());
		}
	}

	@RequestMapping(value = "/userToRole", consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json")
	public ResponseEntity<String> userToRoleMapping(@RequestParam(value = "userId") String userId,
			@RequestParam(value = "role") String role) {
		log.info("userToRoleMapping User >>> " + userId + " Role  >>> " + role);
		System.out.println("userToRoleMapping User >>> " + userId + " Role  >>> " + role);
		JSONObject jobj = dmsService.mapUserWithRole(userId, role);
		System.out.println(ResponseEntity.ok().body(jobj.toString()));
		return ResponseEntity.ok().body(jobj.toString());
	}

	@RequestMapping(value = "/userToCustRole", consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json")
	public ResponseEntity<String> userToCustRoleMapping(@RequestParam(value = "userId") String userId,
			@RequestParam(value = "role") String role, @RequestParam(value = "employeeNumber") String employeeNumber,
			@RequestParam(value = "isCustomRole") boolean isCustom) {
		log.info("userToRoleMapping User >>> " + userId + " Role  >>> " + role);
		System.out.println("userToRoleMapping User >>> " + userId + " Role  >>> " + role);
		JSONObject jobj = dmsService.mapCustomUserWithRole(userId, employeeNumber, role, isCustom);
		System.out.println(ResponseEntity.ok().body(jobj.toString()));
		return ResponseEntity.ok().body(jobj.toString());
	}

	@RequestMapping(value = "/deactivateCustRole", consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json")
	public ResponseEntity<String> DeactivateCustRole(@RequestParam(value = "userId") String userId) {
		log.info("Deactivating User >>> " + userId);
		System.out.println("Deactivating User >>> " + userId);
		JSONObject jobj = dmsService.deactivateCustomUser(userId);
		System.out.println(ResponseEntity.ok().body(jobj.toString()));
		return ResponseEntity.ok().body(jobj.toString());
	}

	@RequestMapping(value = "/searchWithEmployeeID", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> searchForEmployeeID(@RequestParam(value = "loggedInUserID") String loggedInUserID,
			@RequestParam(value = "searchUserId") String searchUserId)
			throws IOException, NoSuchMethodException, ScriptException {
		AES aes = new AES();
		String tempUserId = decoding(loggedInUserID);
		String tempSearchId = decoding(searchUserId);
		String decodedloggedInUserId = script.DecryptResponse(tempUserId, key, nBits);
		String decodedSearchUserId = script.DecryptResponse(tempSearchId, key, nBits);
		
		log.info("searchWithEmployeeID logged in user >>> " + decodedloggedInUserId + " user to search>>> "
				+ decodedSearchUserId); // "searchWithEmployeeID logged in user KMLB8919 user to search>>> KML100023"
		
		System.out.println("searchWithEmployeeID logged in user >>> " + decodedloggedInUserId + " user to search>>> "
				+ decodedSearchUserId);
		
		JSONObject isHRJson = dmsService.searchUserId(loggedInUserID, searchUserId);
		log.info("logged in user isHR >>> " + isHRJson);
		System.out.println("logged in user isHR >>> " + isHRJson);
		if (null != isHRJson) {
			System.out.println(ResponseEntity.ok().body(isHRJson.toString()));
			return ResponseEntity.ok().body(isHRJson.toString());
		} else
			return ResponseEntity.ok().body(isHRJson.toString());
	}

	@SuppressWarnings("unused")
	@RequestMapping(value = "/searchWithRelation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	public ResponseEntity<String> searchWithRelation(@RequestBody SearchDTO searchDTO) {
		log.info("SearchWithRelation >>> " + searchDTO.toString());
		System.out.println("SearchWithRelation >>> " + searchDTO.toString());
		JSONObject relationJson = dmsService.searchForRelation(searchDTO);

		log.info("SearchWithRelation logged in user searchRelation >>> " + relationJson.toString());
		System.out.println("SearchWithRelation logged in user searchRelation >>> " + relationJson.toString());
		if (null != relationJson) {
			System.out.println(ResponseEntity.ok().body(relationJson.toString()));
			return ResponseEntity.ok().body(relationJson.toString());
		} else {
			return ResponseEntity.badRequest().body(null);
		}
	}

	@RequestMapping(value = "/addMasterRole", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public void addMasterRoleToRole() {
		log.info("addMasterRoleToRole >>> ");
		System.out.println("addMasterRoleToRole >>> ");
		dmsService.addToRole();

		log.info("addMasterRoleToRole completed >>> ");
		System.out.println("addMasterRoleToRole completed >>> ");

	}

	@RequestMapping(value = "/searchByDOJ", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> searchByDOJ(@RequestBody Map<String, String> doj) {
		log.info("searchByDOJ >>> " + doj);
		System.out.println("searchByDOJ >>> " + doj);
		JSONObject dojJson = dmsService.searchByDOJ(doj);
		System.out.println(ResponseEntity.ok().body(dojJson.toString()));
		return ResponseEntity.ok().body(dojJson.toString());

	}

	@PostMapping(value = "/logoutTime")
	public ResponseEntity<String> loginTime(@RequestParam(value = "userId") String userId) {
		log.info("LogoutUser >>>" + userId);
		System.out.println("LogoutUser >>>" + userId);
		Date date = new Date();
		String system = "DMS";
		System.out.println("User Logged Out at >>>" + date);
		log.info("User Logged Out at >>>" + date.toString());
		JSONObject json = dmsService.addLogoutTime(userId, date, system);
		log.info("Time Noted");
		System.out.println("Time Noted");
		System.out.println(ResponseEntity.ok().body(json.toString()));
		return ResponseEntity.ok().body(json.toString());
	}

	public String encoding(String text) {
		try {
			String encoded = new String(Base64.getEncoder().encodeToString(text.getBytes()));
			System.out.println("Encoding" + text + "to >>>>>" + encoded);
			return encoded;
		} catch (Exception ex) {
			System.out.println("Error at Encoding Text" + text);
			System.out.println(ex.getStackTrace());
			return text;
		}
	}

	public String decoding(String text) {
		try {
			byte[] decoded = Base64.getDecoder().decode(text);
			System.out.println("Decoding " + text + "to >>>>>" + decoded);
			String message = new String(decoded);
			return message;
		} catch (Exception ex) {
			System.out.println("Error at Decoding Text" + text);
			System.out.println(ex.getStackTrace());
			return text;
		}
	}

//	@PostMapping(value="/loginTime")
//	public void loginTime(@RequestParam(value="userId") String userId) {
//		log.info("LoginUser >>>"+userId);
//		System.out.println("LoginUser >>>" +userId);
//		Date date = new Date();
//		System.out.println("User Logged at >>>" +date);
//		log.info("User Logged at >>>" +date.toString());
//		dmsService.addLoginTime(userId, date);
//		log.info("Time Noted");
//	}
}
