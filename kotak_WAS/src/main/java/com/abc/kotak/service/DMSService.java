package com.abc.kotak.service;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Map;

import javax.script.ScriptException;

import org.json.JSONObject;

import com.abc.kotak.service.dto.MappingDTO;
import com.abc.kotak.service.dto.SearchDTO;

public interface DMSService {
	
	JSONObject getUserDetailsWithAD(String userId);
	JSONObject mapUserWithRole(String userId, String role);
	JSONObject mapCustomUserWithRole(String userId,String employeeNumber, String role,boolean isCustom);
//	JSONObject mapCustomUserWithRoleDTO(MappingDTO mappingDTO);
	Boolean validateEmployee(String userId);
	JSONObject searchForUser(String loggedInUserID, String searchUserId);
	JSONObject searchForRelation(SearchDTO searchDTO);
	JSONObject getUserDetailForSearch(String userId);
	void addToRole();
	JSONObject searchByDOJ(Map<String, String> doj);
	JSONObject deactivateCustomUser(String userId);
	JSONObject addLogoutTime(String userId, Date loginTime, String system);
	void addLoginTime(String userId, Date loginTime, String system);
//	public String encrypt(String arg);
	JSONObject searchUserId(String loggedInUserID, String searchUserId) throws FileNotFoundException, NoSuchMethodException, ScriptException;
	JSONObject mapCustomUserWithRoleDTO(MappingDTO mappingDTO);

}
