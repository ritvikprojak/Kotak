package com.abc.kotak.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.abc.kotak.model.CustomRole;
import com.abc.kotak.model.Staging;

public interface CustomRoleRepo extends JpaRepository<CustomRole, Long> {

	@Query(value="Select RESPONSIBILITY_NAME from HRUPM.CUST_ROLES_USR_MAPPING where ROLE_NAME= ?1", nativeQuery=true)
	List<String> getRoleResponsiblity(String roleName);
	
	@Query(value="Select * from HRUPM.CUST_ROLES_USR_MAPPING where ROLE_NAME= ?1", nativeQuery=true)
	List<CustomRole> getCustRoleDetails(String roleName);

	@Query(value="Delete from HRUPM.CUST_ROLES_USR_MAPPING where ROLE_NAME= ?1", nativeQuery=true)
	void deleteCustomReln(String roleName);
	
	@Query(value="select RESPONSIBILITY_NAME from  HRUPM.CUST_ROLES_USR_MAPPING  where PERSON_NUMBER =?1 AND IS_ACTIVE= 1",nativeQuery = true)
	List<String> findResponseNameByEmpNumber(String loggedInUserID);
	
	@Query(value="select DISTINCT PERSON_NUMBER FROM HRUPM.CUST_ROLES_USR_MAPPING",nativeQuery = true)
	List<String> findUserByEmpNumber();
	
	@Query(value = "select EMPLOYEE_NUMBER from HRUPM.HRMS_HRRM where lob_code = ?1 AND loc_code = ?2 AND cc_code = ?3 ", nativeQuery = true)
	List<String> getEmployeeNumber(String lob , String loc , String cc);
	
	@Query(value = "select person_number from HRUPM.cust_roles_usr_mapping where lob_code = ?1 AND loc_code = ?2 AND cc_code = ?3 ", nativeQuery = true)
	List<String> getPersonNumber(String lob , String loc , String cc);
	
	@Query(value = "select Inclusion_Ecxlusion from HRUPM.cust_roles_usr_mapping where lob_code = ?1 AND loc_code = ?2 AND cc_code = ?3 ", nativeQuery = true)
	char getIncOrExcl(String lob , String loc , String cc);
	
	@Query(value="select distinct Inclusion_Ecxlusion from HRUPM.CUST_ROLES_USR_MAPPING  where RESPONSIBILITY_NAME =?1 and PERSON_NUMBER=?2 AND IS_ACTIVE = 1",nativeQuery = true)
	char findIncnOrExln(String ResponseString, String loggedInUserID);
	
	@Query(value="Select * from HRUPM.CUST_ROLES_USR_MAPPING where DOMAIN_ID= ?1", nativeQuery=true)
	List<CustomRole> custRoleDetailsFromUserId(String userId);
	
	@Query(value="Select DISTINCT ROLE_NAME FROM HRUPM.CUST_ROLES_USR_MAPPING where DOMAIN_ID=?1 AND IS_ACTIVE = 1", nativeQuery = true)
	List<String> getRoleName(String userId);
	
	@Query(value="Select DISTINCT ROLE_NAME FROM HRUPM.CUST_ROLES_USR_MAPPING where PERSON_NUMBER=?1 AND IS_ACTIVE = 1", nativeQuery = true)
	List<String> getRoleNameFromEmployeeNumber(String userId);
}