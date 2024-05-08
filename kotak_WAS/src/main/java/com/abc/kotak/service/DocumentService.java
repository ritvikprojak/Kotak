package com.abc.kotak.service;

import java.util.List;

import com.abc.kotak.model.Documents;


public interface DocumentService {
	
	Documents save(Documents document);
	
	List<Documents> findChildsForParent(Long id);
	
	List<Documents> findAll();
	
	List<Documents> findAllParents();
	
	List<Documents> findAllChilds();
	
	
	void delete(Long id);
	
	Documents update(Documents document);
	
	List<Documents> getRoleDocuments(Long roleId );

}
