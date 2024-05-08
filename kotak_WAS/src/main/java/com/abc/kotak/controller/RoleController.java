package com.abc.kotak.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abc.kotak.Exceptions.BadRequestAlertException;
import com.abc.kotak.dto.RoleDTO;
import com.abc.kotak.model.CustomRole;
import com.abc.kotak.model.Documents;
import com.abc.kotak.model.Role;
import com.abc.kotak.service.DocumentService;
import com.abc.kotak.service.RoleService;
import com.abc.kotak.util.HeaderUtil;
import com.abc.kotak.util.RoleComparator;

@RestController
@RequestMapping("/role")
@CrossOrigin
public class RoleController {

	private static Logger log =LoggerFactory.getLogger(RoleController.class);
	private static final String ENTITY_NAME = "RoleService";
	private RoleService roleService;
	private DocumentService docService;
	
	@Value("${grades}")
	private String grades;
	
	public RoleController(RoleService roleService) {
		super();
		this.roleService = roleService;
	}

	@GetMapping("/getAllGrades")
	public List<String> getAllGrades()
	{
		System.out.println("Rest Services to get all grades >>>>");
		List<String> gradeList = Arrays.asList(grades.split(","));
		return gradeList;
		
	}
	
	@GetMapping("/roleList")
	public List<RoleDTO> getAllRoles(){
		log.info("REST request to get all Documents ");
	    List<RoleDTO> roles = new ArrayList<RoleDTO>();
	    roleService.findAll().forEach(roles::add);
	    
	    RoleComparator roleComparator = new RoleComparator();
	    
	    roles.sort(roleComparator);

	    System.out.println("List of Roles are >>>>"+roles);
	    log.info("List of Roles are "+roles);
	    return roles;
	}
	
	@PostMapping("/addRole")
	public ResponseEntity<Role> createRole(@RequestBody Role role, BindingResult bindingResult) throws URISyntaxException{
		Role roleAdded = new Role();
		try {
			log.info("REST request to save Document  : {}", role);
			System.out.println("REST request to save Document  : {}"+ role);
	        if (role.getRoleId() != null) {
	        	System.out.println(new BadRequestAlertException("A new role cannot already have an ID", ENTITY_NAME, "idexists"));
	            throw new BadRequestAlertException("A new role cannot already have an ID", ENTITY_NAME, "idexists");
	        }
	        List<RoleDTO> existingRole = roleService.findAll();
			boolean roleAlreadyExist = false;
			//existingRole.contains(role);
			for (RoleDTO existRole : existingRole) {
				if(existRole.getRoleName().equalsIgnoreCase(role.getRoleName())){
					roleAlreadyExist=true;
					log.info("Requested Role already exist- " + role.getRoleName());
					System.out.println("Requested Role already exist- " + role.getRoleName());
					return ResponseEntity.status(HttpStatus.CONFLICT).body(null);	
				}
			}
			if(!roleAlreadyExist)
			 roleAdded = roleService.save(role);
			System.out.println("Role Saved Successfully >>>>"+role);
		} catch (Exception e) {
			System.out.println("Exception in saving role >>>>" + e.getCause());
			return ResponseEntity.badRequest().body(null);
			
		}
		return ResponseEntity.ok()
            .body(roleAdded);
	}
	
	@PostMapping("/addCustRole")
	public ResponseEntity<Role> createCustomeRole(@RequestBody RoleDTO role,  BindingResult bindingResult) throws URISyntaxException{
		Role roleAdded = new Role();
		try {
			log.info("REST request to save Custom Role  : {}", role);
			System.out.println("REST request to save Custom Role  : {}" + role);
	        if (role.getRoleId() != null) {
	        	System.out.println(new BadRequestAlertException("A new Custom Role cannot already have an ID", ENTITY_NAME, "idexists"));
	            throw new BadRequestAlertException("A new Custom Role cannot already have an ID", ENTITY_NAME, "idexists");
	        }
	        List<RoleDTO> existingRole = roleService.findAll();
			boolean roleAlreadyExist = false;
			//existingRole.contains(role);
			for (RoleDTO existRole : existingRole) {
				if(existRole.getRoleName().equalsIgnoreCase(role.getRoleName())){
					roleAlreadyExist=true;
					log.info("Requested Role already exist- " + role.getRoleName());
					System.out.println("Requested Role already exist- " + role.getRoleName());
					return ResponseEntity.status(HttpStatus.CONFLICT).body(null);	
				}
			}
			if(!roleAlreadyExist)
			 roleAdded = roleService.saveCustomRole(role);
			System.out.println("Custom Role Successfully added >>>>");
		} catch (Exception e) {
			System.out.println("Exception in adding custom Role >>>>" + e.getCause());
			return ResponseEntity.badRequest().body(null);
		}
		return ResponseEntity.ok()
            .body(roleAdded);
	}
	
	@PutMapping("/updateRole")
	public  ResponseEntity<Role> updateRole(@RequestBody Role role, BindingResult bindingResult) throws URISyntaxException{
		Role roleAdded = new Role();
		try {
			log.info("REST request to update Role  : {}", role);
			System.out.println("REST request to update Role  : {}"+ role);
	        if (role.getRoleId() == null) {
	        	System.out.println(new BadRequestAlertException("A role must have an ID", ENTITY_NAME, "idnotexists"));
	            throw new BadRequestAlertException("A role must have an ID", ENTITY_NAME, "idnotexists");
	        }
	        roleAdded = roleService.save(role);
	        System.out.println("Role Successfully Updated >>>>");
		}catch(Exception e){
			System.out.println("Exception updating Role" + e.getCause());
		}
		return ResponseEntity.created(new URI("/roleList" + roleAdded.getRoleId()))
	            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, roleAdded.getRoleId().toString()))
	            .body(roleAdded);
	}
	
	@PutMapping("/updateCustomRole")
	public  ResponseEntity<RoleDTO> updateCustomRole(@RequestBody RoleDTO role, BindingResult bindingResult) throws URISyntaxException{
		RoleDTO roleAdded = new RoleDTO();
		try {
			log.info("REST request to Update Custom Role  : {}", role);
			System.out.println("REST request to update Custom Role  : {}"+ role);
	        if (role.getRoleId() == null) {
	        	System.out.println(new BadRequestAlertException("A role must have an ID", ENTITY_NAME, "idnotexists"));
	            throw new BadRequestAlertException("A role must have an ID", ENTITY_NAME, "idnotexists");
	        }
	        roleAdded = roleService.updateCustomRole(role);
	        System.out.println("Custom Role Successfully Updated >>>>");
		}catch(Exception e){
			System.out.println("Exception updating Custom Role" + e.getCause());
		}
		return ResponseEntity.created(new URI("/roleList" + roleAdded.getRoleId()))
	            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, roleAdded.getRoleId().toString()))
	            .body(roleAdded);
	}
	
	@PutMapping("/approve")
	public ResponseEntity<Role> approve(@RequestBody Role role, BindingResult bindingResult) throws URISyntaxException{
		Role roleAdded = new Role();
		try {
			log.info("REST request to approve Role  : {}", role);
			System.out.println("REST request to approve Role  : {}"+ role);
	        if (role.getRoleId() == null) {
	        	System.out.println(new BadRequestAlertException("A role must have an ID", ENTITY_NAME, "idnotexists"));
	            throw new BadRequestAlertException("A role must have an ID", ENTITY_NAME, "idnotexists");
	        }
	        roleAdded = roleService.approve(role);
	        System.out.println("Role Successfully Approved >>>>");
		}catch(Exception e){
			System.out.println("Exception in Approving Role>>>>>"+e.getCause());
			
			e.printStackTrace();
		}
		return ResponseEntity.ok().body(roleAdded);
	}
	
	@PostMapping("/getCustomRole")
	public List<String> getCustomRoleData(@RequestBody Role role){
		List<String> resp = new ArrayList<String>();
		try {
			log.info("REST request to get Custom Role Responsibility : {}", role);
	        if (role.getRoleId() == null) {
	        	System.out.println(new BadRequestAlertException("A role must have an ID", ENTITY_NAME, "idnotexists"));
	            throw new BadRequestAlertException("A role must have an ID", ENTITY_NAME, "idnotexists");
	        }
	        resp = roleService.getCustRoleResponsiblity(role);
	        System.out.println("Responisbility list>>>>>" + resp );
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
		}
		
		return resp;
	}
	
	/*@GetMapping("/getGrades")
	public void getGrades(){
		List<String> str = roleService.getAllGrades();
		System.out.println(str);
	}*/
	
	
}
