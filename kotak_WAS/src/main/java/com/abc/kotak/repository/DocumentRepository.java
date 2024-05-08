package com.abc.kotak.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.abc.kotak.model.Documents;



@Repository
public interface DocumentRepository extends JpaRepository<Documents, BigDecimal>{

	@Query("select d from Documents d where d.isActive = true")
	List<Documents> getAllActiveDocuments();
	
	@Query(value = "select * from documents where documents.parent_id is null and is_active = 1 and document_name is not null", nativeQuery = true)
	List<Documents> getParentDocuments();
	
	@Query("select d from Documents d where d.parent is not null")
	List<Documents> getChildDocuments();
	
	@Query(value= "select * from HRUPM.Documents d where d.PARENT_ID =?1",nativeQuery = true)
	List<Documents> getChildDocumentsForParent(Long parentId);
	
	@Query(value = "select distinct documents_docid from hrupm.role_document_mapping where role_id= ?1", nativeQuery = true)
	List<BigDecimal> getDocumentId(Long roleId);
	
	@Query(value= "select * from HRUPM.Documents d where d.DOCID =?1",nativeQuery = true)
	Documents getDocuments(BigDecimal docId);
}
