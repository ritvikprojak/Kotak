package com.abc.kotak.service.mapper;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;


import com.abc.kotak.dto.RoleDTO;
import com.abc.kotak.model.Role;

@Service
public class RoleMapper {

	public RoleDTO roleToRoleDTO(Role role) {
        return new RoleDTO(role);
    }

    public List<RoleDTO> rolesToRolesDTOs(List<Role> roles) {
        return roles.stream()
            .filter(Objects::nonNull)
            .map(this::roleToRoleDTO)
            .collect(Collectors.toList());
    }
    
    public Role roleDTOToRole(RoleDTO roleDTO) {
        if (roleDTO == null) {
            return null;
        } else {
            Role role = new Role();
    		role.setActive(roleDTO.getActive());
    		role.setApproved(roleDTO.getApproved());
    		role.setApprovedBy(roleDTO.getApprovedBy());
    		role.setApprovedOn(roleDTO.getApprovedOn());
    		role.setCopy(roleDTO.getCopy());
    		role.setCreate(roleDTO.getCreate());
    		role.setCreatedBy(roleDTO.getCreatedBy());
    		role.setCreatedOn(roleDTO.getCreatedOn());
    		role.setCTC(roleDTO.getCtc());
    		role.setDelete(roleDTO.getDelete());
    		role.setDocuments(roleDTO.getDocuments());
    		role.setEndDate(roleDTO.getEndDate());
    		role.setGrades(roleDTO.getGrades());
    		role.setIsCustomRole(true);
    		role.setModify(roleDTO.getModify());
    		role.setPrint(roleDTO.getPrint());
    		role.setRead(roleDTO.getRead());
    		role.setRoleName(roleDTO.getRoleName());
    		role.setViewHR(roleDTO.getViewHR());
    		if(null != roleDTO.getRoleId())
    			role.setRoleId(roleDTO.getRoleId());
            return role;
        }
    }

    public List<Role> roleDTOsToRoles(List<RoleDTO> roleDTOs) {
        return roleDTOs.stream()
            .filter(Objects::nonNull)
            .map(this::roleDTOToRole)
            .collect(Collectors.toList());
    }
	
}
