package com.abc.kotak.ldap;

import javax.naming.directory.SearchControls;

/**
 * this class is use to generate search queries for LDAP
 * @author Dilan
 *
 */
class SearchQueries {
	/**
	 * Display All User Groups which are user is belong
	 */
	static SearchControls GetUserGroupsByUserName() {
		SearchControls cons = new SearchControls();
		cons.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String[] attrIDs = { "memberOf" };
		cons.setReturningAttributes(attrIDs);
		return cons;
    }
	
	/**
	 * Display (User/User Group) Distinguished Name
	 */
	static SearchControls GetDistinguishedName() {
		SearchControls cons = new SearchControls();
		cons.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String[] attrIDs = { "distinguishedName" };
		cons.setReturningAttributes(attrIDs);
		return cons;
    }
	
	/**
	 * Display All User Groups and each group members
	 */
	static SearchControls ListAllUserGroupsAndMembers() {
		SearchControls cons = new SearchControls();
		cons.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String[] attrIDs = { "cn", "member" };
		cons.setReturningAttributes(attrIDs);
		return cons;
    }
	/**
	 * Display All User Groups
	 */
	static SearchControls GetAllUserGroups() {
		SearchControls cons = new SearchControls();
		cons.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String[] attrIDs = { "distinguishedName" };
		cons.setReturningAttributes(attrIDs);
		return cons;
    }
}
