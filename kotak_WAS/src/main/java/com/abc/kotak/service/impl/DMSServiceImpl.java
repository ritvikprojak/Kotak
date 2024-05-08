package com.abc.kotak.service.impl;

import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.script.ScriptException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abc.kotak.controller.DMSController;
import com.abc.kotak.ldap.UpmLdapEvents;
import com.abc.kotak.model.CustomRole;
import com.abc.kotak.model.Documents;
import com.abc.kotak.model.Role;
import com.abc.kotak.model.Staging;
import com.abc.kotak.model.UserLogin;
import com.abc.kotak.repository.CustomRoleRepo;
import com.abc.kotak.repository.DocumentRepository;
import com.abc.kotak.repository.RoleRepository;
import com.abc.kotak.repository.StagingRepository;
import com.abc.kotak.repository.UserLoginRepository;
import com.abc.kotak.service.DMSService;
import com.abc.kotak.service.dto.MappingDTO;
import com.abc.kotak.service.dto.SearchDTO;
import com.abc.kotak.web.rest.util.Script;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Service
@Transactional
public class DMSServiceImpl implements DMSService {
	private StagingRepository stagRepo;
	private DocumentRepository docRepo;
	private RoleRepository roleRepo;
	private CustomRoleRepo custRepo;
	private final UpmLdapEvents upmLdap;
	private UserLoginRepository loginRepo;

	private Logger log;

	@Value("${wildCard}")
	private String wildCard;

	@Value("${nBits}")
	private int nBits;

	public DMSServiceImpl(StagingRepository stagRepo, DocumentRepository docRepo, RoleRepository roleRepo,
			UpmLdapEvents upmLdap, CustomRoleRepo custRepo, UserLoginRepository loginRepo) {
		super();
		this.stagRepo = stagRepo;
		this.docRepo = docRepo;
		this.roleRepo = roleRepo;
		this.upmLdap = upmLdap;
		this.custRepo = custRepo;
		this.loginRepo = loginRepo;
		this.log = LoggerFactory.getLogger(DMSServiceImpl.class);

	}

	private EntityManagerFactory emf;

	@PersistenceUnit
	public void setEntityManagerFactory(EntityManagerFactory emf) {
		this.emf = emf;
	}

	@Override
	@Transactional
	public JSONObject getUserDetailsWithAD(String userId) {
		log.debug("User to check in AD >>> " + userId);
		// UserDetails userDetails = SecurityUtils.getCurrentUserDetails();
		Role role = new Role();
		JSONObject jobj = new JSONObject();

		try {

			Staging stag = stagRepo.findUserDomain(userId);

			if (null != stag)

			{

				role = roleRepo.findRoleByRoleName(stag.getROLE()); 

				if (null != role) 
				{

					log.debug(userId + " is assigned to Role >>>" + role.getRoleName());

					jobj = roleJsonMaping(role);

				}

				else if (null == role) 
				{

					List<String> custRole = custRepo.getRoleName(userId);

					if (null != custRole && custRole.size() == 1) 
					{

						log.debug(userId + " is assigned to Custom Role>>>>" + custRole.get(0));

						String customRoleName = custRole.get(0); // added by sumit on 31-01-2020

						Role customRole = roleRepo.findRoleByRoleName(customRoleName);

						jobj = roleJsonMaping(customRole);

					}

					else {

						log.debug("Error>>>" + userId + " is assigned to CustomRole(s) >>>>>" + custRole.toString());

					}

				}

				if (null == stag.getFIRST_NAME()) 
				{

					jobj.put("FIRST_NAME", "");
				} else {

					jobj.put("FIRST_NAME", stag.getFIRST_NAME());

				}
				if (null == stag.getLAST_NAME()) 
				{

					jobj.put("LAST_NAME", "");
				} else {

					jobj.put("LAST_NAME", stag.getLAST_NAME());

				}
				if (null == stag.getEMPLOYEE_NUMBER()) 
				{

					jobj.put("Employee_ID", "");
				} else {

					jobj.put("Employee_ID", stag.getEMPLOYEE_NUMBER());

				}
			}

			else {

				log.debug(userId + " is not Available in HRMS table");

				jobj.put("Error", userId + " is not Available in HRMS table");

			}
//		

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jobj;
	}

	public List<Documents> getDocuments(List<Documents> docs) {
		// added by sumit on 10-02-2020
		List<Documents> parentDoc = new ArrayList<Documents>();

		try {

			for (Documents documents : docs) {

				if (documents.getIsActive()) {

					Documents tempDoc = documents.getParentId();

					if (null != tempDoc.getId()) {

						boolean isPresent = false;

						Iterator<Documents> itr = parentDoc.iterator();

						while (itr.hasNext()) {

							Documents parentTemp = itr.next();

							if (parentTemp.getId() == tempDoc.getId() && null == tempDoc.getParentId()
									&& null != parentTemp.getId()) {

								isPresent = true;

								List<Documents> tempChild = parentTemp.getChildren();

								tempChild.add(documents);

								parentTemp.setChildren(tempChild);

							}

						}

						if (!isPresent) {

							List<Documents> newChild = new ArrayList<Documents>();

							newChild.add(documents);

							tempDoc.setChildren(newChild);

							parentDoc.add(tempDoc);

						}

					}

				}

			}

		}

		catch (NullPointerException nullex) {
//			System.out.println(parentDoc.toString());
//			System.out.println(count);
		}

		return parentDoc;

	}

	public JSONObject roleJsonMaping(Role role) 
	{

		JSONObject jobj = new JSONObject();

		List<Documents> docs = role.getDocuments();

		Set<String> docArry = new HashSet<String>();

		List<Documents> parentDoc = getDocuments(docs);

		for (Documents documents : parentDoc) {

			JSONArray subDocArray = new JSONArray();

			List<Documents> childDocs = documents.getChildren();

			for (Documents child : childDocs) {

				if (null != child.getDocumentName()) {

					subDocArray.put(child.getDocumentName());

				}

			}

			jobj.put(documents.getDocumentName(), subDocArray);

			docArry.add(documents.getDocumentName());

		}

		if (null != docArry) {

			jobj.put("DOCUMENT_TYPE", docArry);

		}
		if (null == role.getCreate() || role.getCreate() == false) {

			jobj.put("CreateAllowed", false);
		} else {

			jobj.put("CreateAllowed", role.getCreate());
		}
		/*
		 * // will be handled in Document Types if(null == role.getCTC())
		 * jobj.put("CTCAllowed", false); else jobj.put("CTCAllowed", role.getCTC());
		 */

		if (null == role.getRead() || role.getRead() == false) {

			jobj.put("View_Allowed", false);
		} else {

			jobj.put("View_Allowed", role.getRead());

		}
		if (null == role.getCopy() || role.getCopy() == false) {

			jobj.put("Download_Allowed", false);
		} else {

			jobj.put("Download_Allowed", role.getCopy());

		}
		if (null == role.getCTC() || role.getCTC() == false) {

			jobj.put("CTC_Allowed", false);
		} else {

			jobj.put("CTC_Allowed", role.getCTC());

		}
		if (null == role.getPrint() || role.getPrint() == false) {

			jobj.put("Print_Allowed", false);
		} else {

			jobj.put("Print_Allowed", role.getPrint());

		}
		if (null == role.getRoleName()) {

			jobj.put("ROLE_NAME", "");
		} else {
			jobj.put("ROLE_NAME", role.getRoleName());
		}
		return jobj;

	}

	@Override
	@Transactional
	public JSONObject mapUserWithRole(String userId, String role) {

		Role roleToBeMapped = roleRepo.findRoleByRoleName(role);
		Staging stag = stagRepo.findUserDomain(userId);
		JSONObject json = new JSONObject();

		/*
		 * EntityManager em = emf.createEntityManager(); String qry = "";
		 */
		try {

			if (null != stag && null != roleToBeMapped) {
				stag.setROLE(role);
				// em.merge(stag);
				stagRepo.save(stag);
				json.put("User Domain Id", userId);
				json.put("User Role", stag.getROLE());
			}

			if (null != roleToBeMapped.getCopy()) {
				if (roleToBeMapped.getCopy()) {
					upmLdap.AddUserToGroup("FN_HR_DOWNLOAD", userId);
				} else
					upmLdap.RemoveUserFromGroup("FN_HR_DOWNLOAD", userId);
			} else
				upmLdap.RemoveUserFromGroup("FN_HR_DOWNLOAD", userId);

			if (null != roleToBeMapped.getCreate()) {
				if (roleToBeMapped.getCreate()) {
					upmLdap.AddUserToGroup("FN_HR_CREATE", userId);
				} else
					upmLdap.RemoveUserFromGroup("FN_HR_CREATE", userId);
			} else
				upmLdap.RemoveUserFromGroup("FN_HR_CREATE", userId);

			if (null != roleToBeMapped.getModify()) {
				if (roleToBeMapped.getModify()) {
					upmLdap.AddUserToGroup("FN_HR_MODIFY", userId);
				} else
					upmLdap.RemoveUserFromGroup("FN_HR_MODIFY", userId);
			} else
				upmLdap.RemoveUserFromGroup("FN_HR_MODIFY", userId);

			if (null != roleToBeMapped.getDelete()) {
				if (roleToBeMapped.getDelete()) {
					upmLdap.AddUserToGroup("FN_HR_DELETE", userId);
				} else
					upmLdap.RemoveUserFromGroup("FN_HR_DELETE", userId);
			} else
				upmLdap.RemoveUserFromGroup("FN_HR_DELETE", userId);

			if (null != roleToBeMapped.getPrint()) {
				if (roleToBeMapped.getPrint()) {
					upmLdap.AddUserToGroup("FN_HR_PRINT", userId);
				} else
					upmLdap.RemoveUserFromGroup("FN_HR_PRINT", userId);
			} else
				upmLdap.RemoveUserFromGroup("FN_HR_PRINT", userId);

			if (null != roleToBeMapped.getRead()) {
				if (roleToBeMapped.getRead()) {
					upmLdap.AddUserToGroup("FN_HR_VIEW", userId);
				} else
					upmLdap.RemoveUserFromGroup("FN_HR_VIEW", userId);
			} else
				upmLdap.RemoveUserFromGroup("FN_HR_VIEW", userId);

			upmLdap.AddUserToGroup("HR_USERS", userId); // Added by Sumit on 23-01-2020

		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;

	}

	@Override
	public Boolean validateEmployee(String userId) {

		int isEmployeePresent = stagRepo.findUserID(userId.trim());

		if (isEmployeePresent > 0)
			return true;
		else
			return false;
	}

	@Override
	public JSONObject searchUserId(String loggedInUserID, String searchUserId)
			throws FileNotFoundException, NoSuchMethodException, ScriptException {
		System.out.println(loggedInUserID + "," + searchUserId);
		Staging searchStag = stagRepo.findUserByEmpNumber(searchUserId.toUpperCase()); //search user details
		Staging loggedUserStag = stagRepo.findUserByEmpNumber(loggedInUserID.toUpperCase()); //shanshank details
		List<String> hrAvailable = stagRepo.findHrAvailable();                               // all the hr and custom role userid like KMBL1234
		List<String> custHrAvailable = custRepo.findUserByEmpNumber();                        //no response
		boolean hr = false;
		if (searchStag != null) {
			String hrList = searchStag.getHR_PERSON_NUMBER();
			if (hrList != null) 
			{
				try 
				{
					hr = Arrays.asList(hrList.split(",")).contains(loggedInUserID);          // searches in the row of emp data if the hr who is logged in is present as a hr person flag is set to true otherwise false
					return checkRights(searchUserId, searchStag, loggedUserStag, hrAvailable, custHrAvailable, hr);
				}
				catch (FileNotFoundException | NoSuchMethodException | ScriptException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					log.error(e.getMessage());
					boolean result = false;
					return resultJSON(searchUserId, result);
				}
			} else {
				try {
					List<String> ResponseNameList = custRepo.findResponseNameByEmpNumber(loggedInUserID);
					char IE = custRepo.findIncnOrExln(ResponseNameList.get(0), loggedInUserID);
					hr = ResponseNameList != null ? ResponseNameList.contains(wildCard) : false && IE == 'I';
					if (!hr) {
						String relation = (searchStag.getLOB_CODE() != null ? searchStag.getLOB_CODE().trim() : "")
								+ "-" + (searchStag.getLOB_CODE() != null ? searchStag.getLOB_CODE().trim() : "") + "-"
								+ (searchStag.getLOB_CODE() != null ? searchStag.getLOB_CODE().trim() : "");
						hr = ResponseNameList != null ? ResponseNameList.contains(relation) : false && IE == 'I';
						if(!hr) {
							hr = ResponseNameList != null ? !ResponseNameList.contains(relation) : false && IE == 'E';
						}
					}
					return checkRights(searchUserId, searchStag, loggedUserStag, hrAvailable, custHrAvailable, hr);
				} catch (FileNotFoundException | NoSuchMethodException | ScriptException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					log.error(e.getMessage());
					boolean result = false;
					return resultJSON(searchUserId, result);
				}
			}
		} 
		else 
		{
			boolean result = false;
			return resultJSON(searchUserId, result);
		}

	}

	/**
	 * @param searchUserId
	 * @param result
	 * @return
	 * @throws FileNotFoundException
	 * @throws ScriptException
	 * @throws NoSuchMethodException
	 */
	private JSONObject resultJSON(String searchUserId, boolean result)
			throws FileNotFoundException, ScriptException, NoSuchMethodException {
		Script script = new Script();
		String scriptTemp = script.EncrytResponse(String.valueOf(result), searchUserId, nBits);
		System.out.println(result);
		String message = encoding(scriptTemp);
		JSONObject isHRJson = new JSONObject();
		isHRJson.put("message", message);
		return isHRJson;
	}

	/**
	 * @param searchUserId
	 * @param searchStag
	 * @param loggedUserStag
	 * @param hrAvailable
	 * @param custHrAvailable
	 * @param hr
	 * @throws ScriptException
	 * @throws NoSuchMethodException
	 * @throws FileNotFoundException
	 */
	private JSONObject checkRights(String searchUserId, Staging searchStag, Staging loggedUserStag,
			List<String> hrAvailable, List<String> custHrAvailable, boolean hr)
			throws FileNotFoundException, NoSuchMethodException, ScriptException {
		Role role;
		boolean grade = false;
		boolean viewHr = false;
		if (hr) 
		{
			role = roleRepo.findRoleByRoleName(loggedUserStag.getROLE() != null ? loggedUserStag.getROLE() : null); // get the role of the logged in hr from db i.e hr relationship manager
			if (role != null) 
			{
				String[] grades = role.getGrades(); // get the grades of the logged in hr from the db
				if (grades != null) 
				{
					grade = Arrays.asList(grades).contains(searchStag.getDESIGNATION_LABEL_ID() != null ? searchStag.getDESIGNATION_LABEL_ID() : "");
					if (grade) 
					{
						if (hrAvailable.contains(searchUserId) || custHrAvailable.contains(searchUserId)) // checks if the user to search is a HR or not
						{
							viewHr = role.getViewHR() != null ? role.getViewHR() : false;
						} 
						else 
						{
							viewHr = true;
						}
					}
				} 
				else 
				{
					grade = false;
				}
			}
		}

		if (hr && grade && viewHr) {
			boolean result = true;
			return resultJSON(searchUserId, result);
		} else {
			boolean result = false;
			return resultJSON(searchUserId, result);
		}
	}

	@Override
	@JsonIgnore
	public JSONObject searchForUser(String loggedInUserID, String searchUserId) {
		System.out.println(loggedInUserID + "," + searchUserId);
		JSONObject isHRJson = new JSONObject();
		Script script = new Script();
		/*
		 * String hrList = stagRepo.findEmployeeHR(searchUserId); String [] values =
		 * hrList.split(","); System.out.println(Arrays.toString(values));
		 */
		boolean isHR = false;
		boolean isHRAvailable = false;
		boolean isUserHR = false;
		boolean isGradeAllowed = false;
		boolean isSearchUserHr = false;
		log.debug(wildCard + " is the WildCard Entry");
		Role role = new Role();
		List<String> hrAvailable = stagRepo.findHrAvailable();
		List<String> custHrAvailable = custRepo.findUserByEmpNumber();
		List<String> ResponseNameList = custRepo.findResponseNameByEmpNumber(loggedInUserID);
//        for (String string : hrAvailable) {
//			if (string.equals(loggedInUserID)) {
//				isHRAvailable = true;
//			}
//		}

		if (hrAvailable.contains(searchUserId) || custHrAvailable.contains(searchUserId)) {
			isSearchUserHr = true;
		}

		try {
			if (hrAvailable.contains(loggedInUserID.toUpperCase())) {
				log.debug(loggedInUserID + " is a HR User");
				isHRAvailable = true;
			} else if (custHrAvailable.contains(loggedInUserID.toUpperCase())) {
				log.debug(loggedInUserID + " is a Custom HR User");
				isHRAvailable = true;
			} else {
				log.debug(loggedInUserID + "is not a HR User");
			}

			if (isHRAvailable) {
				Staging stag = stagRepo.findUserByEmpNumber(searchUserId.toUpperCase());
//    		Staging stagHR = stagRepo.findUserByEmpNumber(loggedInUserID.toUpperCase());

				if (null != stag) {
					String hrLst = stag.getHR_PERSON_NUMBER();
					String[] hrValues = hrLst.split(",");
					if (Arrays.asList(hrValues).contains(loggedInUserID.toUpperCase())) {
						isUserHR = true;
					} else {
						isUserHR = false;
					}
				}
				if (ResponseNameList.contains(wildCard)) {
					log.debug(loggedInUserID + " is mapped to WildCard");
					isUserHR = true;
				} else {
					Iterator<String> iterator = ResponseNameList.iterator();
					boolean flag = true;
					char InclnOrExcln = 'I';
					loop: while (iterator.hasNext()) {
						String ResponseString = iterator.next();
						InclnOrExcln = custRepo.findIncnOrExln(ResponseString, loggedInUserID.toUpperCase());
						String[] ResponseName = ResponseString.split("-");
						List<String> ResponseEmployeeList = custRepo.getEmployeeNumber(ResponseName[0], ResponseName[1],
								ResponseName[2]);
						if (ResponseEmployeeList.contains(searchUserId.toUpperCase()) && InclnOrExcln == 'I') {
							isUserHR = true;
							flag = false;
							break loop;
						} else if (ResponseEmployeeList.contains(searchUserId.toUpperCase()) && InclnOrExcln == 'E') {
							isUserHR = false;
							flag = false;
							break loop;
						} else if (ResponseEmployeeList.isEmpty()) {
						}
					}
					if (flag && InclnOrExcln == 'E') {
						isUserHR = true;
					}
				}
				if (null != stag) {
					Staging stagHR = stagRepo.findUserByEmpNumber(loggedInUserID.toUpperCase());
					if (null != stagHR) {
						role = roleRepo.findRoleByRoleName(stagHR.getROLE());
						if (role != null) {
							if (role.getGrades() != null && role.getGrades().length > 0) {
								String[] GradeList = role.getGrades();
								String Designation = stag.getDESIGNATION_LABEL_ID();
								if (Designation != null && Designation != "") {
									if (Arrays.asList(GradeList).contains(Designation)) {
										isGradeAllowed = true;
									} else {
										isGradeAllowed = false;
									}
								}
							}
						} else { // added by sumit on 03-02-2020
							List<String> roleList = custRepo.getRoleNameFromEmployeeNumber(loggedInUserID);
							if (!roleList.isEmpty() && roleList.size() == 1) {
								String custRoleName = roleList.get(0);
								role = roleRepo.findRoleByRoleName(custRoleName);
								if (role != null) {
									{
										String[] GradeList = role.getGrades();
										String Designation = stag.getDESIGNATION_LABEL_ID();
										if (Designation != null && Designation != "") {
											if (Arrays.asList(GradeList).contains(Designation)) {
												isGradeAllowed = true;
											} else {
												isGradeAllowed = false;
											}
										}
									}
								}
							}

						}
					}
				}

				if (isGradeAllowed && isUserHR) {
					if (role.getViewHR()) {
						isHR = true;
					} else {
						if (isSearchUserHr) {
							isHR = false;
						} else {
							isHR = true;
						}
					}

				}

//            if(null != role){
//            	if(null != role.getViewHR()){
//                	for (String string : values) {
//        			if(string.equals(loggedInUserID) && role.getViewHR())
//        				isHR = true;
//                	}
//                }
//            }
				if (isHR) {
					String scriptTemp = script.EncrytResponse("true", searchUserId, nBits);
					System.out.println("TRUE");
					String message = encoding(scriptTemp);
					isHRJson.put("message", message);
				} else {
					System.out.println("FALSE");
					String scriptTemp = script.EncrytResponse("false", searchUserId, nBits);
					String message = encoding(scriptTemp);
					isHRJson.put("message", message);
				}
//            isHRJson.put("Is_HR",isHR);
			} else {
				String scriptTemp = script.EncrytResponse("false", searchUserId, nBits);
				String message = encoding(scriptTemp);
				isHRJson.put("message", message);
			}
		} catch (NullPointerException nullex) {
			nullex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return isHRJson;
	}

	@Override
	public JSONObject searchForRelation(SearchDTO searchDTO) {

		EntityManager em = emf.createEntityManager();
		Role role = new Role();
		boolean viewHR = false;
		String[] loggedInUserGrd = null;

		Staging stag = stagRepo.findUserByEmpNumber(searchDTO.getEmployeeCode());
		// String loggedInUserGrade = "";

		if (null != stag) {
			if (null != stag.getROLE())
				role = roleRepo.findRoleByRoleName(stag.getROLE().trim());
			// loggedInUserGrade = stag.getDESIGNATION_LABEL_ID().replaceAll("[^0-9]", "");
		}

		if (null != role) {
			if (null != role.getViewHR()) {
				if (role.getViewHR())
					viewHR = true;
			}
			loggedInUserGrd = role.getGrades();

		}

		// HR available in HRRM mapping table
		List<String> hrAvailable = stagRepo.findHrAvailable();
		JSONObject responseList = new JSONObject();
		// List<Staging> listEmployees = new ArrayList<Staging>();
		JSONArray employeeList = new JSONArray();
//		List<String> customHR = new ArrayList<String>();
//		char InclOrExcl = 'I';
		List<String> ResponseNameList = custRepo.findResponseNameByEmpNumber(searchDTO.getEmployeeCode());

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		String today = formatter.format(date);

		/*
		 * Map<String, String> str = new HashMap<String, String>(); str.put("LOB_CODE",
		 * searchDTO.getLOB()); str.put("LOC_CODE", searchDTO.getLOC());
		 * str.put("CC_CODE", searchDTO.getCC()); str.put("DOJ",
		 * searchDTO.getDateOfJoining().toString()); str.put("DOJ_KOTAK_GROUP",
		 * searchDTO.getDateOfGroupJoining().toString()); str.put("LAST_WORKING_DATE",
		 * searchDTO.getLastDate().toString()); str.put("Status", searchDTO.getLOB());
		 * str.values().removeIf(Objects :: isNull); str.values().removeIf(strVal ->
		 * strVal.trim().toString().equals(""));
		 */

		try {
			String qry = "";
			String custqry = "";
			if (null != searchDTO.getLOB() && !searchDTO.getLOB().equals("") && null != searchDTO.getLOC()
					&& !searchDTO.getLOC().equals("") && null != searchDTO.getCC() && !searchDTO.getCC().equals("")) {

				qry = "Select stag.EMPLOYEE_NUMBER, stag.DESIGNATION_LABEL_ID, stag.HR_PERSON_NUMBER  from HRUPM.HRMS_HRRM stag where stag.LOB_CODE = '"
						+ searchDTO.getLOB() + "' AND stag.LOC_CODE= '" + searchDTO.getLOC() + "' AND stag.CC_CODE='"
						+ searchDTO.getCC() + "' ";

//		       	customHR = custRepo.getPersonNumber(searchDTO.getLOB(), searchDTO.getLOC(), searchDTO.getCC());

//		       	InclOrExcl = custRepo.getIncOrExcl(searchDTO.getLOB(), searchDTO.getLOC(), searchDTO.getCC());

				if (null != searchDTO.getStatus()) {
					if (searchDTO.getStatus().equalsIgnoreCase("all"))
						qry = qry;

					else if (searchDTO.getStatus().equalsIgnoreCase("active")) {
						qry = qry + "AND stag.LAST_WORKING_DATE >= TO_DATE('" + today + "' , 'yyyy/MM/dd') ";
					} else if (searchDTO.getStatus().equalsIgnoreCase("inactive")) {
						qry = qry + "AND stag.LAST_WORKING_DATE <= TO_DATE('" + today + "' , 'yyyy/MM/dd') ";
					}

				}

				if (null != searchDTO.getDateOfGroupJoining()
						&& !searchDTO.getDateOfGroupJoining().toString().trim().equals("")) {
					String dogj = formatter.format(searchDTO.getDateOfGroupJoining());
					qry = qry + "AND stag.DOJ_KOTAK_GROUP= TO_DATE('" + dogj + "' , 'yyyy/MM/dd') ";
				}
				if (null != searchDTO.getLastDate() && !searchDTO.getLastDate().toString().trim().equals("")) {
					String lastDate = formatter.format(searchDTO.getLastDate());
					qry = qry + "AND stag.LAST_WORKING_DATE=TO_DATE('" + lastDate + "' , 'yyyy/MM/dd') ";
				}

				if (null != searchDTO.getDateOfJoining()
						&& !searchDTO.getDateOfJoining().toString().trim().equals("")) {
					String doj = formatter.format(searchDTO.getDateOfJoining());
					qry = qry + " AND stag.DOJ= TO_DATE('" + doj + "' , 'yyyy/MM/dd') ";

					// For DOJ between
					// qry= qry +"Select stag.EMPLOYEE_NUMBER, stag.ROLE, stag.DESIGNATION_LABEL_ID,
					// stag.HR_PERSON_NUMBER from HRUPM.HRMS_HRRM stag where stag.DOJ between
					// TO_DATE('1990/10/1', 'yyyy/MM/dd') and TO_DATE('"+doj+"','yyyy/MM/dd')";

				}

			} else {
				if (null != searchDTO.getDateOfJoining()
						&& !searchDTO.getDateOfJoining().toString().trim().equals("")) {
					String doj = formatter.format(searchDTO.getDateOfJoining());
					qry = qry
							+ "Select stag.EMPLOYEE_NUMBER, stag.DESIGNATION_LABEL_ID, stag.HR_PERSON_NUMBER  from HRUPM.HRMS_HRRM stag where stag.DOJ= TO_DATE('"
							+ doj + "' , 'yyyy/MM/dd') ";

					// For DOJ between
					// qry= qry +"Select stag.EMPLOYEE_NUMBER, stag.ROLE, stag.DESIGNATION_LABEL_ID,
					// stag.HR_PERSON_NUMBER from HRUPM.HRMS_HRRM stag where stag.DOJ between
					// TO_DATE('1990/10/1', 'yyyy/MM/dd') and TO_DATE('"+doj+"','yyyy/MM/dd')";

				}
			}

			Query query = em.createNativeQuery(qry);
			List<Object[]> resultList = query.getResultList();
			System.out.println(resultList.toString());
			boolean isUserHR = false;
			List<String> employees = new ArrayList<String>();

//	       	List<String>  ResponseNameList = custRepo.findResponseNameByEmpNumber(searchDTO.getEmployeeCode());

			if (null != resultList && !resultList.isEmpty()) {

//	        	for (Object[] result : resultList) {
//					employees.add(result[0].toString());
//				}
//	        	if(!ResponseNameList.isEmpty()) {
//	            	if(ResponseNameList.contains(wildCard)){
//	            		log.debug(searchDTO.getEmployeeCode() +" is mapped to WildCard");
//	            		employeeList.put(resultList.get(0));
//	            	}
//	            	else {
//	            		Iterator<String> iterator = ResponseNameList.iterator();
//	            		boolean flag = true;
//	            		char InclnOrExcln = 'I';
//	            		loop:while(iterator.hasNext()) {
//	            			String ResponseString = iterator.next();
//	            			InclnOrExcln = custRepo.findIncnOrExln(ResponseString);
//	            			String[] ResponseName = ResponseString.split("-");
//	            			List<String> ResponseEmployeeList = custRepo.getEmployeeNumber(ResponseName[0],ResponseName[1],ResponseName[2]);
//	            			if(ResponseEmployeeList.contains(employees.toString()) && InclnOrExcln == 'I'){
//	            				isUserHR = true;
//	            				flag = false;
//	            				break loop;
//	            			}
//	            			else if(ResponseEmployeeList.containsAll(employees) && InclnOrExcln == 'E') {
//	            				isUserHR = false;
//	            				flag= false;
//	            				break loop;
//	            			}
//	            			else if(ResponseEmployeeList.isEmpty()) {
//	            			}
//	            		}
//	            		if(flag && InclnOrExcln == 'E') {
//	            			isUserHR = true;
//	            		}
//	            	}
//		        	Boolean gradeAccess = false;
//		        	if (null != loggedInUserGrd) {
//		        		for (String string : loggedInUserGrd) {
//		            		if (resultList.get(1).toString().trim().equals(string)) 
//		            		{
//		            			gradeAccess = true;
//								break;
//		            		}
//		    			}
//					}
//	        	}

				for (Object[] staging : resultList) {
					if (!ResponseNameList.isEmpty()) {
						if (ResponseNameList.contains(wildCard)) {
							log.debug(searchDTO.getEmployeeCode() + " is mapped to WildCard");
							employeeList.put(staging[0]);
							break;
						} else {
							Iterator<String> iterator = ResponseNameList.iterator();
							boolean flag = true;
							char InclnOrExcln = 'I';
							loop: while (iterator.hasNext()) {
								String ResponseString = iterator.next();
								InclnOrExcln = custRepo.findIncnOrExln(ResponseString, searchDTO.getEmployeeCode());
								String[] ResponseName = ResponseString.split("-");
								List<String> ResponseEmployeeList = custRepo.getEmployeeNumber(ResponseName[0],
										ResponseName[1], ResponseName[2]);
								if (ResponseEmployeeList.contains(staging[0]) && InclnOrExcln == 'I') {
									isUserHR = true;
									flag = false;
									break loop;
								} else if (ResponseEmployeeList.contains(staging[0]) && InclnOrExcln == 'E') {
									isUserHR = false;
									flag = false;
									break loop;
								} else if (ResponseEmployeeList.isEmpty()) {
								}
							}
						}
						Boolean gradeAccess = false;
						if (null != loggedInUserGrd) {
							for (String string : loggedInUserGrd) {
								if (staging[1].toString().trim().equals(string)) {
									gradeAccess = true;
									break;
								}
							}
						}
						if (gradeAccess && isUserHR) {
							employeeList.put(staging[0]);
						}
					} else {
						if (viewHR) {
							if (null != staging[2]) {
								String hrLst = staging[2].toString();
								String[] values = hrLst.split(",");
								Arrays.toString(values);
								for (String hr : values) {
									if (hr.trim().equals(searchDTO.getEmployeeCode())) {

										Boolean gradeAccess = false;
										if (null != loggedInUserGrd) {
											for (String string : loggedInUserGrd) {
												if (staging[1].toString().trim().equals(string)) {
													gradeAccess = true;
													break;
												}

											}
										}

										if (gradeAccess) {
											employeeList.put(staging[0]);
										}

									}
								}

							}
						} else {
							if (null != loggedInUserGrd) {
								if (!hrAvailable.contains("staging[0].toString().trim()")) {
									if (null != staging[2]) {
										String hrLst = staging[2].toString();
										String[] values = hrLst.split(",");
										Arrays.toString(values);
										for (String hr : values) {
											if (hr.trim().equals(searchDTO.getEmployeeCode())) {

												/*
												 * Role listRole = new Role(); Boolean personViewHR = false;
												 */
												Boolean gradeAccess = false;
												if (null != loggedInUserGrd) {
													for (String string : loggedInUserGrd) {
														if (staging[1].toString().trim().equals(string)) {
															gradeAccess = true;
															break;
														}

													}
												}
												if (viewHR == false && gradeAccess) {
													employeeList.put(staging[0]);
												}

											}
										}

									}
								}
							}

						}
					}

				}

			}
			System.out.println(employeeList);
			responseList.put("EmployeeList", employeeList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * 
		 * 
		 * 
		 * 
		 * //listRole = roleRepo.findRoleByRoleName(staging[1].toString());
		 * 
		 * //if(null != listRole){ /*if(null != listRole.getViewHR()){
		 * if(listRole.getViewHR()) personViewHR = true; }
		 */

		/*
		 * String listEmpGrade = staging[2].toString(); String listEmpGradeNo =
		 * listEmpGrade.replaceAll("[^0-9]", "");
		 * 
		 * 
		 * if (Integer.parseInt(listEmpGradeNo) > Integer.parseInt(loggedInUserGrade ))
		 * { gradeAccess = false; }
		 */

		/*
		 * String []str = listRole.getGrades(); Arrays.sort(str);
		 * System.out.println(str.toString());
		 */

		/*
		 * for (int i=0; i< str.length ; i++) {
		 * if(str[i].equalsIgnoreCase(staging[2].toString())){ if (i+1 < str.length)
		 * gradeAccess = false; break; }
		 * 
		 * }
		 * 
		 * //}
		 * 
		 * 
		 * if(null != searchDTO.getDateOfGroupJoining()){ Date date =
		 * searchDTO.getDateOfGroupJoining(); SimpleDateFormat formatter = new
		 * SimpleDateFormat("yyyy/MM/dd"); String dogj= formatter.format(date);
		 * 
		 * listEmployees =
		 * stagRepo.findEmployeeByDOGJ(searchDTO.getLOB(),searchDTO.getLOC(),searchDTO.
		 * getCC(),dogj); if (null != listEmployees && !listEmployees.isEmpty()) { for
		 * (Staging staging : listEmployees) { if (null != hrAvailable &&
		 * !hrAvailable.isEmpty()) { JSONObject employeeObj = new JSONObject(); for
		 * (String string : hrAvailable) {
		 * 
		 * if (string.equals(staging.getEMPLOYEE_NUMBER())) { personisHR = true;
		 * employeeObj.put("EmployeeCode", staging.getEMPLOYEE_NUMBER());
		 * employeeObj.put("EmployeeIsHR", personisHR); } else {
		 * employeeObj.put("EmployeeCode", staging.getEMPLOYEE_NUMBER());
		 * employeeObj.put("EmployeeIsHR", personisHR); } }
		 * employeeList.put(employeeObj); } } } responseList.put("EmployeeList",
		 * employeeList); return responseList; } else if (null !=
		 * searchDTO.getDateOfJoining()) { Date date = searchDTO.getDateOfJoining();
		 * SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd"); String doj=
		 * formatter.format(date);
		 * 
		 * listEmployees =
		 * stagRepo.findEmployeeByDOJ(searchDTO.getLOB(),searchDTO.getLOC(),searchDTO.
		 * getCC(),doj); responseList.put("EmployeeList", listEmployees); return
		 * responseList; }else if (null != searchDTO.getLastDate()) { Date date =
		 * searchDTO.getLastDate(); SimpleDateFormat formatter = new
		 * SimpleDateFormat("yyyy/MM/dd"); String lastDate= formatter.format(date);
		 * 
		 * listEmployees =
		 * stagRepo.findEmployeeByLastDate(searchDTO.getLOB(),searchDTO.getLOC(),
		 * searchDTO.getCC(),lastDate); responseList.put("EmployeeList", listEmployees);
		 * return responseList; }else if (null != searchDTO.getStatus()) {
		 * 
		 * Date date = new Date(); SimpleDateFormat formatter = new
		 * SimpleDateFormat("yyyy/MM/dd"); String today= formatter.format(date);
		 * 
		 * if(searchDTO.getStatus().equalsIgnoreCase("all")){ listEmployees =
		 * stagRepo.findEmployeeStatusAll(searchDTO.getLOB(),searchDTO.getLOC(),
		 * searchDTO.getCC()); responseList.put("EmployeeList", listEmployees); return
		 * responseList; } if (searchDTO.getStatus().equalsIgnoreCase("active")) {
		 * listEmployees =
		 * stagRepo.findEmployeeIsActive(searchDTO.getLOB(),searchDTO.getLOC(),searchDTO
		 * .getCC(),today); responseList.put("EmployeeList", listEmployees); return
		 * responseList; } else if (searchDTO.getStatus().equalsIgnoreCase("inactive"))
		 * { listEmployees =
		 * stagRepo.findEmployeeIsInActive(searchDTO.getLOB(),searchDTO.getLOC(),
		 * searchDTO.getCC(), today); responseList.put("EmployeeList", listEmployees);
		 * return responseList; }
		 * 
		 * }
		 */

		return responseList;
	}

	@Override
	public JSONObject getUserDetailForSearch(String userId) {
		System.out.println("User to check in AD for Search " + userId);
		Role role = new Role();
		JSONObject jobj = new JSONObject();
		try {
			Staging stag = stagRepo.findUserDomain(userId);
			if (null != stag.getROLE()) {
				role = roleRepo.findRoleByRoleName(stag.getROLE());
				if (null != role) {
					List<Documents> docs = role.getDocuments();

					Set<String> docArry = new HashSet<String>();
					for (Documents documents : docs) {
						if (null != documents.getParentId()) {
							Documents parentDoc = documents.getParentId();
							JSONArray subDocArray = new JSONArray();
							subDocArray.put(documents.getDocumentName());
							jobj.put(parentDoc.getDocumentName(), subDocArray);
							docArry.add(parentDoc.getDocumentName());

						}
					}
					if (null != docArry)
						jobj.put("DOCUMENT_TYPE", docArry);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jobj;
	}

	@Override
	public void addToRole() {
		EntityManager em = emf.createEntityManager();
		try {
			String qry = "select distinct role from  HRUPM.HRMS_HRRM";
			Query query = em.createNativeQuery(qry);
			List<String> resultList = query.getResultList();
			System.out.println(resultList);
			for (String string : resultList) {
				if (null != string) {
					Role role = new Role();
					role.setRoleName(string);
					roleRepo.save(role);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public JSONObject searchByDOJ(Map<String, String> doj) {
		JSONObject jobj = new JSONObject();
		/*
		 * EntityManager em = emf.createEntityManager(); String sdate =
		 * doj.get("startdate"); String edate = doj.get("endDate"); String qry = null;
		 * 
		 * if(edate!=null&&edate!=""){ qry =
		 * "Select stag.EMPLOYEE_NUMBER, stag.ROLE, stag.DESIGNATION_LABEL_ID, stag.HR_PERSON_NUMBER  from HRUPM.HRMS_HRRM stag where doj between TO_DATE('+"
		 * +sdate+"', 'yyyy/MM/dd') and TO_DATE('"+edate+"','yyyy/MM/dd')"; }else { qry
		 * =
		 * "Select stag.EMPLOYEE_NUMBER, stag.ROLE, stag.DESIGNATION_LABEL_ID, stag.HR_PERSON_NUMBER  from HRUPM.HRMS_HRRM stag where doj = TO_DATE('+"
		 * +sdate+"', 'yyyy/MM/dd') "; }
		 * 
		 * 
		 * Query query = em.createNativeQuery(qry);
		 * 
		 * List<String> resultList = query.getResultList();
		 * 
		 * if (null != resultList) { jobj.put("Employee_List", resultList); }
		 */

		return jobj;
	}

	/**
	 *
	 */
	@Override
	public JSONObject mapCustomUserWithRole(String userId, String employeeNumber, String role, boolean isCustom) {
		/*
		 * EntityManager em = emf.createEntityManager(); String qry = "";
		 */
		JSONObject json = new JSONObject();
		Role roleToBeMapped = roleRepo.findRoleByRoleName(role);
		if (roleToBeMapped == null) {
			json.put("User Domain Id", userId); // changed by sumit
			json.put("User Role", role);
			json.put("Status", "Failed");
			json.put("Description", role + " is not an Active Role");

			return json;
		} else {

			boolean isCustomCheck = false;
			if (roleToBeMapped.getIsCustomRole() != null)
				isCustomCheck = roleToBeMapped.getIsCustomRole();
			List<CustomRole> custRoles = custRepo.getCustRoleDetails(role);
//		List<CustomRole> existingCustRoles = custRepo.custRoleDetailsFromUserId(userId);

			try {
				if (!isCustomCheck) {
					json.put("User Domain Id", userId); // changed by sumit
					json.put("User Role", role);
					json.put("Status", "Failed");
					json.put("Description", role + " is not a Custom Role");

					return json;
				} else {
					Staging stag = stagRepo.findUserDomainForMapping(userId);
					boolean insert = false;

					if (stag == null)
						insert = true;

//						String roleHRCheck = stag.getROLE();
//						Role isRoleHR = roleRepo.findRoleByRoleName(roleHRCheck);
					List<String> hrAvailable = stagRepo.findHrAvailable();
					if (hrAvailable.contains(employeeNumber.toUpperCase())) {
						// json.put("Message", userId + "is already assigned to a HR Role");

						json.put("User Domain Id", userId); // changed by sumit
						json.put("User Role", role);
						json.put("Status", "Failed");
						json.put("Description", employeeNumber + " is already assigned to a HR Role");
						return json;
					}

					else {

						deactivateCustomUserData(userId);

						if (isCustom && !custRoles.isEmpty() && null != roleToBeMapped) {
							for (CustomRole customRole : custRoles) {

								// CustomRole custrole = new CustomRole();
								customRole.setDomainName(userId);
								customRole.setuID(employeeNumber);
								customRole.setActive(true);
								if (roleToBeMapped.getEndDate() != null)
									customRole.setLastWorkingDate(roleToBeMapped.getEndDate());
								custRepo.save(customRole);

								json.put("User Domain Id", userId); // changed by sumit
								json.put("User Role", role);
								json.put("Status", "Success");

							}

							if (insert) {
								Staging stagRe = new Staging();
								stagRe.setDOMAIN_LOGIN_ID(userId);
								stagRe.setEMPLOYEE_NUMBER(employeeNumber);
								stagRe.setLAST_WORKING_DATE(roleToBeMapped.getEndDate());
								stagRe.setROLE(roleToBeMapped.getRoleName());
								stagRepo.save(stagRe);
							} else {
								stag.setROLE(roleToBeMapped.getRoleName());
								stagRepo.save(stag);
								// stagRepo.updateHRRMS(role, userId,employeeNumber); // changed by sumit &
								// pradeep on 30-01-2020
							}
						} else if (isCustom && custRoles.isEmpty() && null != roleToBeMapped) {
							CustomRole customRole = new CustomRole();
							customRole.setDomainName(userId);
							customRole.setuID(employeeNumber);
							customRole.setActive(true);
							customRole.setRoleName(role);
							if (roleToBeMapped.getEndDate() != null)
								customRole.setLastWorkingDate(roleToBeMapped.getEndDate());
							custRepo.save(customRole);

							if (insert) {
								Staging stagRe = new Staging();

								stagRe.setDOMAIN_LOGIN_ID(userId);
								stagRe.setEMPLOYEE_NUMBER(employeeNumber);
								stagRe.setLAST_WORKING_DATE(roleToBeMapped.getEndDate());
								stagRe.setROLE(roleToBeMapped.getRoleName());
								stagRepo.save(stagRe);
							} else {

								stag.setROLE(roleToBeMapped.getRoleName());
								stagRepo.save(stag); // changed by sumit & pradeep on 30-01-2020
							}

							json.put("User Domain Id", userId); // changed by sumit
							json.put("User Role", role);
							json.put("Status", "Success");
						} else {
							json.put("User Domain Id", userId); // changed by sumit
							json.put("User Role", role);
							json.put("Status", "Failed");
							json.put("Description", role + " is not a Custom Role");
							return json;
						}
						if (null != roleToBeMapped.getCopy()) {
							if (roleToBeMapped.getCopy()) {
								upmLdap.AddUserToGroup("FN_HR_DOWNLOAD", userId);
							} else
								upmLdap.RemoveUserFromGroup("FN_HR_DOWNLOAD", userId);
						} else
							upmLdap.RemoveUserFromGroup("FN_HR_DOWNLOAD", userId);

						if (null != roleToBeMapped.getCreate()) {
							if (roleToBeMapped.getCreate()) {
								upmLdap.AddUserToGroup("FN_HR_CREATE", userId);
							} else
								upmLdap.RemoveUserFromGroup("FN_HR_CREATE", userId);
						} else
							upmLdap.RemoveUserFromGroup("FN_HR_CREATE", userId);

						if (null != roleToBeMapped.getModify()) {
							if (roleToBeMapped.getModify()) {
								upmLdap.AddUserToGroup("FN_HR_MODIFY", userId);
							} else
								upmLdap.RemoveUserFromGroup("FN_HR_MODIFY", userId);
						} else
							upmLdap.RemoveUserFromGroup("FN_HR_MODIFY", userId);

						if (null != roleToBeMapped.getDelete()) {
							if (roleToBeMapped.getDelete()) {
								upmLdap.AddUserToGroup("FN_HR_DELETE", userId);
							} else
								upmLdap.RemoveUserFromGroup("FN_HR_DELETE", userId);
						} else
							upmLdap.RemoveUserFromGroup("FN_HR_DELETE", userId);

						if (null != roleToBeMapped.getPrint()) {
							if (roleToBeMapped.getPrint()) {
								upmLdap.AddUserToGroup("FN_HR_PRINT", userId);
							} else
								upmLdap.RemoveUserFromGroup("FN_HR_PRINT", userId);
						} else
							upmLdap.RemoveUserFromGroup("FN_HR_PRINT", userId);

						if (null != roleToBeMapped.getRead()) {
							if (roleToBeMapped.getRead()) {
								upmLdap.AddUserToGroup("FN_HR_VIEW", userId);
							} else
								upmLdap.RemoveUserFromGroup("FN_HR_VIEW", userId);
						} else
							upmLdap.RemoveUserFromGroup("FN_HR_VIEW", userId);

						upmLdap.AddUserToGroup("HR_USERS", userId); // added on 23-01-2019 by Sumit

					}

				}

			} catch (Exception e) {
				e.printStackTrace();
				json.put("User Domain Id", userId); // changed by sumit
				json.put("User Role", role);
				json.put("Status", "Failed");
				json.put("Description", "Service Failed due to " + e.getCause());

			}

		}
		return json;

	}

	@Override
	public JSONObject mapCustomUserWithRoleDTO(MappingDTO mappingDTO){
		JSONObject json = new JSONObject();
		Role roleToBeMapped = roleRepo.findRoleByRoleName(mappingDTO.getRole());
		if(roleToBeMapped == null) {
			json.put("User Domain Id", mappingDTO.getUserId()); // changed by sumit
			json.put("User Role", mappingDTO.getRole());
			json.put("Status", "Failed");
			json.put("Description", mappingDTO.getRole() + " is not an Active Role");
			
			return json;
		}
		else {
			
		boolean isCustomCheck=false;
		if(roleToBeMapped.getIsCustomRole()!= null)	
		 isCustomCheck = roleToBeMapped.getIsCustomRole();
		List<CustomRole> custRoles = custRepo.getCustRoleDetails(mappingDTO.getRole());
//		List<CustomRole> existingCustRoles = custRepo.custRoleDetailsFromUserId(userId);

			try {
				if(!isCustomCheck) {
					json.put("User Domain Id", mappingDTO.getUserId()); // changed by sumit
					json.put("User Role", mappingDTO.getRole());
					json.put("Status", "Failed");
					json.put("Description", mappingDTO.getRole() + " is not a Custom Role");
					
					return json;
				}
				else {
					Staging stag = stagRepo.findUserDomainForMapping(mappingDTO.getUserId());
					boolean insert=false;
					
					if(stag==null )insert=true;	
					
					
//						String roleHRCheck = stag.getROLE();
//						Role isRoleHR = roleRepo.findRoleByRoleName(roleHRCheck);
					    List<String> hrAvailable = stagRepo.findHrAvailable();
						if(hrAvailable.contains( mappingDTO.getEmployeeNumber().toUpperCase())) {
							//json.put("Message", userId + "is already assigned to a HR Role");
							
							json.put("User Domain Id",  mappingDTO.getUserId()); // changed by sumit
							json.put("User Role",  mappingDTO.getRole());
							json.put("Status", "Failed");
							json.put("Description",  mappingDTO.getRole() + " is already assigned to a HR Role");
							return json;
						}
						
					
						else {
						
						deactivateCustomUserData(mappingDTO.getUserId());
							
							if (mappingDTO.isCustomRole() && !custRoles.isEmpty() && null != roleToBeMapped ) {
								for (CustomRole customRole : custRoles) {
									
									//CustomRole custrole = new CustomRole();
									customRole.setDomainName(mappingDTO.getUserId());
									customRole.setuID(mappingDTO.getEmployeeNumber());
									customRole.setActive(true);
									if(roleToBeMapped.getEndDate()!=null) {
										customRole.setLastWorkingDate(roleToBeMapped.getEndDate());
									}
									custRepo.save(customRole);
									
									
									json.put("User Domain Id", mappingDTO.getUserId()); // changed by sumit
									json.put("User Role", mappingDTO.getRole());
									json.put("Approved By", mappingDTO.getApprovedBy());
									json.put("Approved On", mappingDTO.getApprovedOn());
									json.put("Modified By", mappingDTO.getModifiedBy());
									json.put("Modified On", mappingDTO.getModifiedOn());
									json.put("Status", "Success");
									
								}
								
								if(insert) {
									Staging stagRe = new Staging();
									stagRe.setDOMAIN_LOGIN_ID(mappingDTO.getUserId());
									stagRe.setEMPLOYEE_NUMBER(mappingDTO.getEmployeeNumber());
									stagRe.setLAST_WORKING_DATE(roleToBeMapped.getEndDate());
									stagRe.setROLE(roleToBeMapped.getRoleName());
									stagRepo.save(stagRe);
								}else {
									stag.setROLE(roleToBeMapped.getRoleName());
									stagRepo.save(stag);
							//	stagRepo.updateHRRMS(role, userId,employeeNumber); // changed by sumit & pradeep on 30-01-2020
								}
							}
							else if(mappingDTO.isCustomRole() && custRoles.isEmpty() && null != roleToBeMapped) {
								CustomRole customRole= new CustomRole();
								customRole.setDomainName(mappingDTO.getUserId());
								customRole.setuID(mappingDTO.getEmployeeNumber());
								customRole.setActive(true);
								customRole.setRoleName(mappingDTO.getRole());
								if(roleToBeMapped.getEndDate()!=null) {
									customRole.setLastWorkingDate(roleToBeMapped.getEndDate());
								}
								custRepo.save(customRole);
								
								if(insert) {
									Staging stagRe = new Staging();
									
									stagRe.setDOMAIN_LOGIN_ID(mappingDTO.getUserId());
									stagRe.setEMPLOYEE_NUMBER(mappingDTO.getEmployeeNumber());
									stagRe.setLAST_WORKING_DATE(roleToBeMapped.getEndDate());
									stagRe.setROLE(roleToBeMapped.getRoleName());
									stagRepo.save(stagRe);
								}else {
						
									stag.setROLE(roleToBeMapped.getRoleName());
									stagRepo.save(stag); // changed by sumit & pradeep on 30-01-2020
								}
								
								json.put("User Domain Id", mappingDTO.getUserId()); // changed by sumit
								json.put("User Role", mappingDTO.getRole());
								json.put("Approved By", mappingDTO.getApprovedBy());
								json.put("Approved On", mappingDTO.getApprovedOn());
								json.put("Modified By", mappingDTO.getModifiedBy());
								json.put("Modified On", mappingDTO.getModifiedOn());
								json.put("Status", "Success");
								
							}
							else {
								json.put("User Domain Id", mappingDTO.getUserId()); // changed by sumit
								json.put("User Role", mappingDTO.getRole());
								json.put("Status", "Failed");
								json.put("Description", mappingDTO.getRole() + " is not a Custom Role");
								return json;
							}
							if (null != roleToBeMapped.getCopy()) {
								if (roleToBeMapped.getCopy()) {
									upmLdap.AddUserToGroup("FN_HR_DOWNLOAD",mappingDTO.getUserId());
								}
								else
									upmLdap.RemoveUserFromGroup("FN_HR_DOWNLOAD",mappingDTO.getUserId());
							}
							else
								upmLdap.RemoveUserFromGroup("FN_HR_DOWNLOAD",mappingDTO.getUserId());
				
							if (null != roleToBeMapped.getCreate()) {
								if (roleToBeMapped.getCreate()) {
									upmLdap.AddUserToGroup("FN_HR_CREATE",mappingDTO.getUserId());
								}
								else
									upmLdap.RemoveUserFromGroup("FN_HR_CREATE",mappingDTO.getUserId());
							}
							else
								upmLdap.RemoveUserFromGroup("FN_HR_CREATE",mappingDTO.getUserId());
							
							if (null != roleToBeMapped.getModify()) {
								if (roleToBeMapped.getModify()) {
									upmLdap.AddUserToGroup("FN_HR_MODIFY",mappingDTO.getUserId());
								}
								else
									upmLdap.RemoveUserFromGroup("FN_HR_MODIFY",mappingDTO.getUserId());
							}
							else
								upmLdap.RemoveUserFromGroup("FN_HR_MODIFY",mappingDTO.getUserId());
							
							
							if (null != roleToBeMapped.getDelete()) {
								if (roleToBeMapped.getDelete()) {
									upmLdap.AddUserToGroup("FN_HR_DELETE",mappingDTO.getUserId());
								}
								else
									upmLdap.RemoveUserFromGroup("FN_HR_DELETE",mappingDTO.getUserId());
							}
							else
								upmLdap.RemoveUserFromGroup("FN_HR_DELETE",mappingDTO.getUserId());
							
							
							if (null != roleToBeMapped.getPrint() ) {
								if (roleToBeMapped.getPrint()) {
									upmLdap.AddUserToGroup("FN_HR_PRINT",mappingDTO.getUserId());		
								}
								else
									upmLdap.RemoveUserFromGroup("FN_HR_PRINT",mappingDTO.getUserId());
							}
							else
								upmLdap.RemoveUserFromGroup("FN_HR_PRINT",mappingDTO.getUserId());
							
							
							if (null != roleToBeMapped.getRead()) {
								if (roleToBeMapped.getRead()) {
									upmLdap.AddUserToGroup("FN_HR_VIEW",mappingDTO.getUserId());
								}
								else
									upmLdap.RemoveUserFromGroup("FN_HR_VIEW",mappingDTO.getUserId());
							}
							else
								upmLdap.RemoveUserFromGroup("FN_HR_VIEW",mappingDTO.getUserId());
							
							
							upmLdap.AddUserToGroup("HR_USERS", mappingDTO.getUserId()); // added on 23-01-2019 by Sumit
							
						}
			}
					
			} catch (Exception e) {
				e.printStackTrace();
				json.put("User Domain Id", mappingDTO.getUserId()); // changed by sumit
				json.put("User Role", mappingDTO.getUserId());
				json.put("Status", "Failed");
				json.put("Description", "Service Failed due to " + e.getCause());
				
			}

		}
		return json;
	}

	@Override
	public JSONObject deactivateCustomUser(String userId) {
		JSONObject json = new JSONObject();

		/*
		 * EntityManager em = emf.createEntityManager(); String qry = "";
		 */

		try {
			List<CustomRole> custRoles = custRepo.custRoleDetailsFromUserId(userId);
			if (custRoles.isEmpty()) {
				json.put("Message", userId + " is not mapped to Custom Role");
			} else {
				boolean flag = true;
				for (CustomRole customRole : custRoles) {
					customRole.setDomainName(null);
					customRole.setuID(null);
					customRole.setActive(false);
					if (customRole.isActive()) {
						flag = false;
					}
				}
				json.put("User Domain Id", userId);
				if (flag) {
					json.put("Status", "Deactivated");
				}

				try {
					upmLdap.RemoveUserFromGroup("FN_HR_DOWNLOAD", userId);
				} catch (Exception e) {
					e.getCause();
				}
				try {
					upmLdap.RemoveUserFromGroup("FN_HR_CREATE", userId);
				} catch (Exception e) {
					e.getCause();
				}
				try {
					upmLdap.RemoveUserFromGroup("FN_HR_VIEW", userId);
				} catch (Exception e) {
					e.getCause();
				}
				try {
					upmLdap.RemoveUserFromGroup("FN_HR_PRINT", userId);
				} catch (Exception e) {
					e.getCause();
				}
				try {
					upmLdap.RemoveUserFromGroup("FN_HR_DELETE", userId);
				} catch (Exception e) {
					e.getCause();
				}
				try {
					upmLdap.RemoveUserFromGroup("FN_HR_MODIFY", userId);
				} catch (Exception e) {
					e.getCause();
				}
				try {
					upmLdap.RemoveUserFromGroup("FN_HR_CREATE", userId);
				} catch (Exception e) {
					e.getCause();
				}
				try {
					upmLdap.RemoveUserFromGroup("HR_USERS", userId);
				} catch (Exception e) {
					e.getCause();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return json;

	}

	public void deactivateCustomUserData(String userId) {

		/*
		 * EntityManager em = emf.createEntityManager(); String qry = "";
		 */

		try {
			List<CustomRole> custRoles = custRepo.custRoleDetailsFromUserId(userId);
			if (custRoles.isEmpty()) {

			} else {
				for (CustomRole customRole : custRoles) {
					customRole.setDomainName(null);
					customRole.setuID(null);
					customRole.setActive(false);
				}

				try {
					upmLdap.RemoveUserFromGroup("FN_HR_DOWNLOAD", userId);
				} catch (Exception e) {
					e.getCause();
				}
				try {
					upmLdap.RemoveUserFromGroup("FN_HR_CREATE", userId);
				} catch (Exception e) {
					e.getCause();
				}
				try {
					upmLdap.RemoveUserFromGroup("FN_HR_VIEW", userId);
				} catch (Exception e) {
					e.getCause();
				}
				try {
					upmLdap.RemoveUserFromGroup("FN_HR_PRINT", userId);
				} catch (Exception e) {
					e.getCause();
				}
				try {
					upmLdap.RemoveUserFromGroup("FN_HR_DELETE", userId);
				} catch (Exception e) {
					e.getCause();
				}
				try {
					upmLdap.RemoveUserFromGroup("FN_HR_MODIFY", userId);
				} catch (Exception e) {
					e.getCause();
				}
				try {
					upmLdap.RemoveUserFromGroup("FN_HR_CREATE", userId);
				} catch (Exception e) {
					e.getCause();
				}
				try {
					upmLdap.RemoveUserFromGroup("HR_USERS", userId);
				} catch (Exception e) {
					e.getCause();
				}
			}

//
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public JSONObject addLogoutTime(String userId, Date loginTime, String system) {
		UserLogin user = new UserLogin();
		JSONObject json = new JSONObject();
		try {
			user = loginRepo.userLatestLogin(userId, system);
			user.setLogout_time(loginTime);
			user.setSystem(system);
			loginRepo.save(user);
			json.put("User Id", userId);
			json.put("LogOut Time", loginTime);
		} catch (NullPointerException nullex) {
			nullex.printStackTrace();
			json.put("DOMAIN_ID", "NOT FOUND");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return json;
	}

	@Override
	public void addLoginTime(String userId, Date loginTime, String system) {
		UserLogin user = new UserLogin();
		JSONObject json = new JSONObject();

		try {
			user.setDomainId(userId);
			user.setLogin_time(loginTime);
			user.setSystem(system);
			loginRepo.save(user);
			json.put("User Id", userId);
			json.put("Time", loginTime);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public String encoding(String text) {
		try {
			//String encoded = new sun.misc.BASE64Encoder().encode(text.getBytes());
			String encoded = Base64.getEncoder().encodeToString(text.getBytes());
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
			//byte[] decoded = new sun.misc.BASE64Decoder().decodeBuffer(text);
			byte[] decoded = Base64.getDecoder().decode(text.getBytes());
			System.out.println("Decoding" + text + "to >>>>>" + decoded);
			String decode = new String(decoded);
			return decode;
		} catch (Exception ex) {
			System.out.println("Error at Decoding Text" + text);
			System.out.println(ex.getStackTrace());
			return text;
		}
	}
}

//getADDetails FUnction on 10-02-2020
//for (Documents documents : docs) {   
//if (documents.getIsActive()) {
//	if(null != documents.getParentId()){
//		Documents parentDoc = documents.getParentId();
//		JSONArray subDocArray = new JSONArray();
//		List<Documents> chilDocs = parentDoc.getChildren();
//		for(Documents doc : chilDocs){
//			subDocArray.put(doc.getDocumentName());	
//		}
//		jobj.put(parentDoc.getDocumentName(), subDocArray);
//		docArry.add(parentDoc.getDocumentName());
//	}
//} 
//}

//List<Documents> docs = role.getDocuments();
//
//Set<String> docArry = new HashSet<String>();
//
//List<Documents> parentDoc = getDocuments(docs);
//
//
//for (Documents documents : parentDoc) {
//JSONArray subDocArray = new JSONArray();
//List<Documents> childDocs = documents.getChildren();
//for (Documents child : childDocs) {
//	if(null != child.getDocumentName()) {
//		subDocArray.put(child.getDocumentName());
//	}	
//}
//jobj.put(documents.getDocumentName(),subDocArray);
//docArry.add(documents.getDocumentName());
//} 	
//
//
//if(null != docArry)
//jobj.put("DOCUMENT_TYPE",docArry);
//
//if(null == role.getCreate() || role.getCreate() == false)
//jobj.put("CreateAllowed", false);
//else
//jobj.put("CreateAllowed", role.getCreate());
//
///*
//*  //      will be handled in Document Types
//* 			if(null == role.getCTC())
//			jobj.put("CTCAllowed", false);
//		else
//			jobj.put("CTCAllowed", role.getCTC());
//*/			
//if(null == role.getRead() || role.getRead() == false)
//jobj.put("View_Allowed", false);
//else
//jobj.put("View_Allowed", role.getRead());
//
//if(null == role.getCopy() || role.getCopy() == false)
//jobj.put("Download_Allowed", false);
//else
//jobj.put("Download_Allowed", role.getCopy());
//
//if(null == role.getCTC() || role.getCTC() == false)
//jobj.put("CTC_Allowed", false);
//else
//jobj.put("CTC_Allowed", role.getCTC());
//
//if(null == role.getPrint() || role.getPrint() == false)
//jobj.put("Print_Allowed", false);
//else
//jobj.put("Print_Allowed", role.getPrint());
//
//if(null == role.getRoleName())
//jobj.put("ROLE_NAME", "");
//else
//jobj.put("ROLE_NAME", role.getRoleName());
//

//else if(null == stag) {
//	List<String> roleName = custRepo.getRoleName(userId);
//	if(!roleName.isEmpty() && roleName.size() == 1) {
//		String customRoleName = roleName.get(0);
//		role = roleRepo.findRoleByRoleName(customRoleName);
//		Date custEndDate = role.getEndDate();
//		int customTemp = custEndDate.compareTo(date);
//		if(customTemp >= 0) {
//			try { //addded by sumit on 31-01-2020
//				List<Documents> docs = role.getDocuments();
//				
//				Set<String> docArry = new HashSet<String>();
//				
//				for (Documents documents : docs) {
//					if (documents.getIsActive()) {
//						if(null != documents.getParentId()){
//							Documents parentDoc = documents.getParentId();
//							JSONArray subDocArray = new JSONArray();
//							List<Documents> chilDocs = parentDoc.getChildren();
//							for(Documents doc : chilDocs){
//								subDocArray.put(doc.getDocumentName());	
//							}
//							jobj.put(parentDoc.getDocumentName(), subDocArray);
//							docArry.add(parentDoc.getDocumentName());
//						}
//					} 
//				}
//				if(null != docArry)
//					jobj.put("DOCUMENT_TYPE",docArry);
//				}
//				catch(Exception ex) {
//					ex.printStackTrace();
//				}
//					
//					if(null == role.getCreate() || role.getCreate() == false)
//						jobj.put("CreateAllowed", false);
//					else
//						jobj.put("CreateAllowed", role.getCreate());
//					
//					/*
//					 *  //      will be handled in Document Types
//					 * 			if(null == role.getCTC())
//									jobj.put("CTCAllowed", false);
//								else
//									jobj.put("CTCAllowed", role.getCTC());
//					*/			
//					if(null == role.getRead() || role.getRead() == false)
//						jobj.put("View_Allowed", false);
//					else
//						jobj.put("View_Allowed", role.getRead());
//
//					if(null == role.getCopy() || role.getCopy() == false)
//						jobj.put("Download_Allowed", false);
//					else
//						jobj.put("Download_Allowed", role.getCopy());
//					
//					if(null == role.getCTC() || role.getCTC() == false)
//						jobj.put("CTC_Allowed", false);
//					else
//						jobj.put("CTC_Allowed", role.getCTC());
//					
//					if(null == role.getPrint() || role.getPrint() == false)
//						jobj.put("Print_Allowed", false);
//					else
//						jobj.put("Print_Allowed", role.getPrint());
//					
//					if(null == role.getRoleName())
//						jobj.put("ROLE_NAME", "");
//					else
//						jobj.put("ROLE_NAME", role.getRoleName());
//		}
//	}
//	else {
//		return null;
//	}
//}
//