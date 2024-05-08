package com.abc.kotak.service.mapper;

import java.util.List;

import com.abc.kotak.dto.RoleDTO;
import com.abc.kotak.model.Role;

//@Mapper
public interface RoleToRoleDTOMapper extends EntityMapper<RoleDTO, Role> {
	
	@Override
	public List<RoleDTO> toDto(List<Role> entityList);
	
	@Override
	public RoleDTO toDto(Role entity);

}
