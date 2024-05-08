package com.abc.kotak.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abc.kotak.model.Documents;
import com.abc.kotak.repository.DocumentRepository;
import com.abc.kotak.service.DocumentService;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

	private final DocumentRepository docRepository;
	
	public DocumentServiceImpl(DocumentRepository docRepository) {
		this.docRepository = docRepository;
	}

	@Override
	@Transactional
	public Documents save(Documents document) {
		Documents documents = docRepository.save(document);
		return documents;
	}
	
	@Override
	@Transactional
	public Documents update(Documents document) {
		Documents documents = new Documents();
		List<Documents> docs = findChildsForParent(document.getId());
		try {
			if (null == document.getParentId()) 
			{
				if (document.getIsActive()) {
					if (null != docs) {
					for (Documents docm: docs){ 
						docm.setIsActive(true);
						docRepository.save(docm);
					}
					}
					documents = docRepository.save(document);
				}
				else{
					if (null != docs) {
						for (Documents docm: docs){ 
							docm.setIsActive(false);
							docRepository.save(docm);
						}	
					}
					
					documents = docRepository.save(document);
				}
			}
			else
				documents = docRepository.save(document);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return documents;
	}

	@Override
	public List<Documents> findChildsForParent(Long id) {
		 List<Documents> docs = docRepository.getChildDocumentsForParent(id);
		return docs;
	}

	@Override
	@Transactional
	public List<Documents> findAll() {
		//List<Documents> emptyChild = new ArrayList<Documents>();
		List<Documents> documents = docRepository.findAll();
		return documents;
	}

	@Override
	@Transactional
	public List<Documents> findAllParents() {
		List<Documents> documents = new ArrayList<Documents>();
		documents = docRepository.getParentDocuments();
		return documents;
	}
	
	@Override
	@Transactional
	public List<Documents> findAllChilds() {
		List<Documents> documents = new ArrayList<Documents>();
		documents = docRepository.getChildDocuments();
		return documents;
	}
	
	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public List<Documents> getRoleDocuments(Long roleId){
		
		List<Documents> docs = new ArrayList<Documents>();
		
		List<BigDecimal> documentId = docRepository.getDocumentId(roleId);
		
		try {
			
			for (BigDecimal docId : documentId) {
				
				Documents doc = docRepository.getDocuments(docId);
				
				docs.add(doc);
				
			}
			
		}
		
		catch(Exception e) {
			
			System.out.println(e.getCause());
		
		}
		
		
		return docs;
	}

	
}
