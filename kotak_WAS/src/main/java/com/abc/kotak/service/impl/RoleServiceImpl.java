package com.abc.kotak.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abc.kotak.dto.CustomRoleDTO;
import com.abc.kotak.dto.RoleDTO;
import com.abc.kotak.model.CustomRole;
import com.abc.kotak.model.Documents;
import com.abc.kotak.model.Role;
import com.abc.kotak.repository.CustomRoleRepo;
import com.abc.kotak.repository.RoleRepository;
import com.abc.kotak.service.RoleService;
import com.abc.kotak.service.mapper.RoleMapper;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
	
	//private UserService userService; 
	
	//private RoleToDocumentMappingReposiotry roleToDocMapRepo;
	private RoleRepository roleRepository;
	private CustomRoleRepo custRoleRepo;
	private final RoleMapper roleMapper;
	
	
	
	public RoleServiceImpl(/*RoleToDocumentMappingReposiotry roleToDocMapRepo,*/
			RoleRepository roleRepository,CustomRoleRepo custRoleRepo, RoleMapper roleMapper) {
		super();
		//this.roleToDocMapRepo = roleToDocMapRepo;
		this.roleRepository = roleRepository;
		this.custRoleRepo=custRoleRepo;
		this.roleMapper=roleMapper;
	}

	@Override
	@Transactional
	public Role save(Role role) {
		//UserDetails userDetails = SecurityUtils.getCurrentUserDetails();
		if (null != role.getDocuments() || !role.getDocuments().isEmpty()) {
			if (null != role.getRoleId()) {
				int docsCount =  roleRepository.getDocumentsCount(role.getRoleId());
				if (docsCount > 0) 
					roleRepository.removeDocuments(role.getRoleId());
			}
		}
		
		if(null == role.getApproved() || !role.getApproved()){
			role.setCreatedOn(new Date());
			role.setActive(false);
		}
		else{
			role.setCreatedOn(new Date());
			role.setApproved(false);
			role.setApprovedBy(null);
			role.setActive(false);
		}
		
		roleRepository.save(role);
		return role;
	}

	@Override
	public Role findOne(Long id) {
		return null;
	}

	@Override
	@Transactional
	public List<RoleDTO> findAll() {
		List<Role> roleList = roleRepository.findAll();
		for (Role role : roleList) {
			try {
			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
			Date date = new Date();
			if (null == role.getEndDate()) {
	    		role.setActive(false);
			}
			else {
		    if(DateUtils.isSameDay(date, role.getEndDate())){
		    	boolean flag = role.getApproved();
		    	if(flag) {
		    		role.setActive(true);
		    	}
		    }
	    	else if (role.getEndDate().compareTo(new Date()) < 0) {
				role.setActive(false);

			}
			}
			roleRepository.save(role);
			}catch(NullPointerException nullex) {
				nullex.printStackTrace();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		List<RoleDTO> roleDto = new ArrayList<RoleDTO>();
		roleDto = roleMapper.rolesToRolesDTOs(roleList);
		
		List<RoleDTO> rolesDto = new ArrayList<RoleDTO>();
			for (RoleDTO rolDTO : roleDto) {
				
//				List<Documents> docs = rolDTO.getDocuments();
//				
//				
//				if(!docs.isEmpty()) {
//					List<Documents> docsTemp = new ArrayList<Documents>();
//				
//					for (Documents doc : docs) {
//						docsTemp.add(doc);
//					}
//					rolDTO.setDocuments(docsTemp);
//				}
				
				
				// changed by sumit on 31-01-2020
				if (null != rolDTO.getIsCustomRole() && rolDTO.getIsCustomRole() ) {
						//Map<String,String> lstStr = new HashMap<String, String>(); 
						List<CustomRole> custrole = custRoleRepo.getCustRoleDetails(rolDTO.getRoleName());
						List<String> str = new ArrayList<String>();
						for (CustomRole customRole : custrole) {
							try {
							str.add(customRole.getResponsiblityName());
							rolDTO.setIncluORExcl(customRole.getInclnORExcln().toString());
							}
							catch(Exception e){
								e.printStackTrace();
							}
							
							}
						rolDTO.setRelationship(str);
						rolesDto.add(rolDTO); 

				}
				else
					rolesDto.add(rolDTO);
			}


		return rolesDto;
	}
	
	

	@Override
	public void delete(Long id) {
	}
	
	@Override
	@Transactional
	public List<String> getAllGrades(){
		return null;
		
	}

	@Override
	@Transactional
	public Role approve(Role role) {
			role.setActive(true);
			role.setApproved(true);
			role.setApprovedOn(new Date());
			roleRepository.save(role);
		
		return role;
	}

	@Override
	public Role saveCustomRole(RoleDTO role) {
		Role custRole = new Role();
		custRole = roleMapper.roleDTOToRole(role);
		
		if(null == role.getApproved() || !role.getApproved()){
			custRole.setCreatedOn(new Date());
			custRole.setActive(false);
		}
		
		else{
			custRole.setCreatedOn(new Date());
			custRole.setApproved(false);
			custRole.setApprovedBy(null);
			custRole.setActive(false);
			
		}
		try {
			roleRepository.save(custRole);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List<String> listRelation = role.getRelationship();
		if(listRelation.toString() != null && listRelation.toString() != "" && !listRelation.isEmpty()) {
			for (String relation : listRelation) {
				CustomRole customRole =  new CustomRole();
				customRole.setRoleName(role.getRoleName());
				customRole.setResponsiblityName(relation);
				
				String[] str = relation.split("-");
				
				System.out.println(str.length);
				
				if(null != str && str.length==3)
				{
					customRole.setLob(str[0]);
					customRole.setLoc(str[1]);
					customRole.setCc(str[2]);
				}
				if (role.getIncluORExcl().equals("I")) {
					customRole.setInclnORExcln('I');
				}
				else{
					customRole.setInclnORExcln('E');
				}
				try {
					custRoleRepo.save(customRole);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		
			
		}
		else {
			CustomRole customRole =  new CustomRole();
			customRole.setRoleName(role.getRoleName());
			customRole.setResponsiblityName(null);
			custRoleRepo.save(customRole);
		}
		return custRole;
	}
	
	@Override
	public RoleDTO updateCustomRole(RoleDTO roleDTO) {
		Role role = new Role();
		role = roleMapper.roleDTOToRole(roleDTO);
		if (null != role.getDocuments() || !role.getDocuments().isEmpty()) {
			int docsCount =  roleRepository.getDocumentsCount(role.getRoleId());
			if (docsCount > 0) 
				roleRepository.removeDocuments(role.getRoleId());
			List<Documents> document = role.getDocuments();
		}
		
		if(null == role.getApproved() || !role.getApproved()){
			role.setCreatedOn(new Date());
			role.setActive(false);
		}
		
		else{
			role.setCreatedOn(new Date());
			role.setApproved(false);
			role.setApprovedBy(null);
			role.setActive(false);
			
		}
		
		roleRepository.save(role);
		String domainId = null;
		String personNumber = null;
		boolean isActive = true;
		Date LWD = new Date();
		
		if (null != roleDTO.getRelationship()) {
			List<CustomRole> remvCustRole = custRoleRepo.getCustRoleDetails(roleDTO.getRoleName());
			for (CustomRole customRole : remvCustRole) {
				if (null != customRole.getDomainName()) {
					domainId = customRole.getDomainName();
					personNumber = customRole.getuID();
					isActive = customRole.isActive();
					LWD = customRole.getLastWorkingDate();
					
					break;
				}
			}
			custRoleRepo.delete(remvCustRole);
		}
		List<String> listRelation = roleDTO.getRelationship();
		if(listRelation != null) {
			for (String relation : listRelation) {
				CustomRole customRole =  new CustomRole();
				customRole.setRoleName(roleDTO.getRoleName());
				customRole.setResponsiblityName(relation);
				customRole.setActive(isActive);
				customRole.setLastWorkingDate(LWD);

				
				String[] str = relation.split("-");
				
				if(null != str && str.length==3)
				{
					customRole.setLob(str[0]);
					customRole.setLoc(str[1]);
					customRole.setCc(str[2]);
				}
				
				if (roleDTO.getIncluORExcl().equals("I")) {
					customRole.setInclnORExcln('I');
				}
				else{
					customRole.setInclnORExcln('E');
				}
				if (null != domainId) {
					customRole.setDomainName(domainId);
					customRole.setuID(personNumber);
					customRole.setActive(isActive);
					customRole.setLastWorkingDate(LWD);
				}
				custRoleRepo.save(customRole);
			}
			
		}
		else {
			CustomRole customRole =  new CustomRole();
			customRole.setRoleName(role.getRoleName());
			customRole.setResponsiblityName(null);
			custRoleRepo.save(customRole);
		}
		
		return roleDTO;		
	}

	@Override
	public List<String> getCustRoleResponsiblity(Role role) {
		List<String> lstStr = custRoleRepo.getRoleResponsiblity(role.getRoleName());
		return lstStr;
	}
	
	
//	@Override
//	public List<JSONObject> mapToJSON(List<RoleDTO> roleDTO) {
//		
//		List<RoleDTO> tempDTO = roleDTO;
//		
//		List<JSONObject> listJson = new ArrayList<JSONObject>();
//		
//		try {
//		
//			
//		
//			for (RoleDTO rolDTO : tempDTO) {
//			
//				JSONObject jobj = new JSONObject();
//			if(rolDTO.getRoleId() == null) {
//				
//			}
//			else {
//				jobj.put("roleID", rolDTO.getRoleId());
//				
//				if(rolDTO.getRoleName() == null) {
//					
//				}
//				else {
//					
//					jobj.put("roleName", rolDTO.getRoleName());
//					
//					if(rolDTO.getCreate() == null || rolDTO.getCreate() == false) {
//						jobj.put("create", "false");
//					}
//					else {
//						jobj.put("create", "true");
//					}
//					
//					if(rolDTO.getRead() == null || rolDTO.getRead() == false) {
//						jobj.put("read", "false");
//					}
//					else {
//						jobj.put("read", "true");
//					}
//					
//					if(rolDTO.getCopy() == null || rolDTO.getCopy() == false) {
//						jobj.put("copy", "false");
//					}
//					else {
//						jobj.put("copy", "true");
//					}
//					
//					if(rolDTO.getModify() == null || rolDTO.getModify() == false) {
//						jobj.put("modify", "false");
//					}
//					else {
//						jobj.put("modify", "true");
//					}
//					
//					if(rolDTO.getPrint() == null || rolDTO.getPrint() == false) {
//						jobj.put("print", "false");
//					}
//					else {
//						jobj.put("print", "true");
//					}
//					
//					if(rolDTO.getDelete() == null || rolDTO.getDelete() == false) {
//						jobj.put("delete", "false");
//					}
//					else {
//						jobj.put("delete", "true");
//					}
//					
//					if(rolDTO.getViewHR() == null || rolDTO.getViewHR() == false) {
//						jobj.put("viewHR", "false");
//					}
//					else {
//						jobj.put("viewHR", "true");
//					}
//					
//					if(rolDTO.getActive() == null || rolDTO.getActive() == false) {
//						jobj.put("active", "false");
//					}
//					else {
//						jobj.put("active", "true");
//					}
//					
//					if(rolDTO.getApproved() == null || rolDTO.getApproved() == false) {
//						jobj.put("approved", "false");
//					}
//					else {
//						jobj.put("approved", "true");
//					}
//					
//					if(rolDTO.getCtc() == null || rolDTO.getCtc() == false) {
//						jobj.put("ctc", "false");
//					}
//					else {
//						jobj.put("ctc", "true");
//					}
//					
//					if(rolDTO.getIsCustomRole() == null || rolDTO.getIsCustomRole() == false) {
//						jobj.put("isCustomRole", "false");
//					}
//					else {
//						jobj.put("isCustomRole", "true");
//					}
//					
//					if(rolDTO.getIncluORExcl() == null || rolDTO.getIncluORExcl() == "I") {
//						jobj.put("incluORExcl", "I");
//					}
//					else {
//						jobj.put("incluORExcl", "E");
//					}
//					
//					
//					
//					jobj.put("grades", rolDTO.getGrades());
//					
//					jobj.put("createdBy", rolDTO.getCreatedBy());
//					
//					jobj.put("createdOn", rolDTO.getCreatedOn());
//					
//					jobj.put("approvedBy", rolDTO.getApprovedBy());
//					
//					jobj.put("approvedOn", rolDTO.getApprovedOn());
//					
//					jobj.put("endDate", rolDTO.getEndDate());
//
//					jobj.put("relationship" , rolDTO.getRelationship());
//					
//					
//					
//					
//				}
//				listJson.add(jobj);
//			}
//			
//		}
//		}
//		catch(Exception e) {
//			System.out.println(e.getCause());
//		}
//		
//		return listJson;
//	}

	

}
