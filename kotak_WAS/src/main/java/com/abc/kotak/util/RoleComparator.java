package com.abc.kotak.util;

import java.util.Comparator;

import com.abc.kotak.dto.RoleDTO;
import com.abc.kotak.model.Role;

public class RoleComparator implements Comparator<RoleDTO> {

	@Override
	public int compare(RoleDTO o1, RoleDTO o2) {

		return o1.getRoleName().compareTo(o2.getRoleName());
	}

}
