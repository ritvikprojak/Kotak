package com.abc.kotak.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.abc.kotak.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

	@Query(value="Select * from HRUPM.roles rol where rol.ROLENAME = ?1 AND rol.end_date >= LOCALTIMESTAMP AND isactive = '1'", nativeQuery=true)
	Role findRoleByRoleName(String roleName);

	@Modifying
	@Query(value="Delete from HRUPM.ROLE_DOCUMENT_MAPPING rolmap where rolmap.ROLE_ID = ?1 ", nativeQuery=true)
	void removeDocuments(Long roleId);

	@Query(value="Select count(*) from HRUPM.ROLE_DOCUMENT_MAPPING rolmap where rolmap.ROLE_ID = ?1 ", nativeQuery=true)
	int getDocumentsCount(Long roleId);
	
	
}
