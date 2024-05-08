package com.abc.kotak.service;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.abc.kotak.dto.RoleDTO;
import com.abc.kotak.model.Role;

public interface RoleService {
	
	Role save(Role role);
	
	Role findOne(Long id);
	
	List<RoleDTO> findAll();
	
	void delete(Long id);

	List<String> getAllGrades();

	Role approve(Role role);

	Role saveCustomRole(RoleDTO role);

	List<String> getCustRoleResponsiblity(Role role);

	RoleDTO updateCustomRole(RoleDTO roleDTO);
	
	
	
	

}
 