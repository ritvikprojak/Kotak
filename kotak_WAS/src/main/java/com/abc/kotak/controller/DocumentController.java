package com.abc.kotak.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.abc.kotak.Exceptions.BadRequestAlertException;
//import com.abc.kotak.dto.DocumentDTO;
import com.abc.kotak.model.Documents;
import com.abc.kotak.service.DocumentService;
import com.abc.kotak.util.HeaderUtil;
//import com.fasterxml.jackson.annotation.JsonIgnore;

@RestController
@CrossOrigin
@RequestMapping("/document")
public class DocumentController {
	private static Logger log = LoggerFactory.getLogger(DocumentController.class);

	private static final String ENTITY_NAME = "DocumentService";

	private final DocumentService docService;

	public DocumentController(DocumentService docService) {
		this.docService = docService;
	}

	@PostMapping("/addDocument")
	
	public ResponseEntity<Documents> addDocument(@RequestBody Documents document, BindingResult bindingResult)
			throws URISyntaxException, BadRequestAlertException {
		

		log.info("REST request to save Document  : {}", document);
		System.out.println("REST request to save Document  : {}" + document.toString());
		if (document.getId() != null) {
			System.out.println(new BadRequestAlertException("A new document cannot already have an ID", ENTITY_NAME, "idexists"));
			throw new BadRequestAlertException("A new document cannot already have an ID", ENTITY_NAME, "idexists");
		}
		
		List<Documents> existingDocument = docService.findAll();
		Documents result = new Documents();
		boolean documentAlreadyExist = false;
		//existingDocument.contains(document);
		for (Documents existdoc : existingDocument) {
			if(existdoc.getDocumentName() == null || existdoc.getDocumentName() == "") {}
			else if(existdoc.getDocumentName().equalsIgnoreCase(document.getDocumentName())){
				documentAlreadyExist=true;
				System.out.println("Requested Document type already exist- " + document.getDocumentName());
				log.info("Requested Document type already exist- " + document.getDocumentName());
				System.out.println(ResponseEntity.status(HttpStatus.CONFLICT).body(null));
				return ResponseEntity.status(HttpStatus.CONFLICT).body(null);	
			}
			else {}
		}
		if(!documentAlreadyExist) {
			result = docService.save(document);
			System.out.println("Rest request to create Document type - " + result);
			log.info("Rest request to create Document type - " + result);
		}
		System.out.println(ResponseEntity.created(new URI("documentList" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString())).body(result));
		return ResponseEntity.created(new URI("documentList" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString())).body(result);

	}
	
	@PutMapping("/updateDocument")
	
	public ResponseEntity<Documents> updateDocument(@RequestBody Documents document , BindingResult result) throws BadRequestAlertException, URISyntaxException
	{
		System.out.println("REST request to update Document  : {}" + document.toString());
		log.info("REST request to update Document  : {}", document);
		if (document.getId() == null) {
			System.out.println(new BadRequestAlertException("An existing document must already have an ID", ENTITY_NAME, "idexists"));
			throw new BadRequestAlertException("An existing document must already have an ID", ENTITY_NAME, "idexists");
		}
		
		Documents resultDoc = docService.update(document);

		if(null == resultDoc){
			System.out.println(ResponseEntity.badRequest().body(null));
			return ResponseEntity.badRequest().body(null);	
		}
		System.out.println("Document Updated >>>>"+ resultDoc.toString());
		return ResponseEntity.accepted().body(resultDoc);
	}

	@GetMapping("/getAllDocuments")
	public List<Documents> getAllDocuments() {
		log.info("Rest request to get all Documents");
		System.out.println("Rest request to get all Documents");
		List<Documents> documents = new ArrayList<Documents>();
		List<Documents> documentList = new ArrayList<Documents>();
		List<Documents> docParent = docService.findAllParents();
		Iterator<Documents> iterator = docParent.iterator();
		documentList.addAll(docParent);
		while(iterator.hasNext()) {
			Documents doc = iterator.next();
			List<Documents> child = docService.findChildsForParent(doc.getId());
			for (Documents documents2 : child) {
				documents2.setParentId(doc);
			}
			documentList.addAll(child);
		}
		
//		for (Documents doc : documents) {
//			if(doc.getIsActive() != null && doc.getDocumentName() != null && doc.getId() != null)
//				documentList.add(doc);
//			else {}
//		}
//		Iterator<Documents> iterator = documents.iterator();
//		try {
//		while(iterator.hasNext()) {
//			Documents doc = iterator.next();
//			Documents docParent = new Documents();
//			String docName = doc.getDocumentName();
//			long id = doc.getId();
//			Boolean isActive = doc.getIsActive();
//			if(isActive != null && docName != null && id != 0 && doc.getParentId() != null) {
//				documentList.add(doc);
//			}
//			if(doc.getParentId() == null && docName != null && isActive != null && id != 0) {
//				docParent.setId(id);
//				docParent.setParentId(null);
//				docParent.setDocumentName(docName);
//				docParent.setIsActive(isActive);
//				documentList.add(docParent);
//			}
//		}
//		}
//		catch(NullPointerException nullex) {
//			nullex.printStackTrace();
//		}
		
		log.info("Rest request to get All Document types - " + documentList);
		System.out.println("Rest request to get All Document types - " + documentList);
		return documentList;
	}
	
	@GetMapping("/getSubDocuments")
	public List<Documents> getAllChildDocuments() {
		List<Documents> documents = new ArrayList<Documents>();
		documents = docService.findAllChilds();
		log.info("Rest request to get Document types - " + documents);
		System.out.println("Rest request to get Document types - " + documents);
		return documents;
	}
	
	@GetMapping("/getParentDocuments")
	public List<Documents> getParentDocuments() {
		log.info("Rest request to get all Documents...");
		System.out.println("Rest request to get all Documents...");
		List<Documents> documents = new ArrayList<Documents>();
		documents = docService.findAllParents();
		System.out.println("Rest request to create Document type - " + documents);
		log.info("Rest request to create Document type - " + documents);
		return documents;
	}
	
	@GetMapping("/getRoleDocuments")
	public List<Documents> getRoleDocuments(@RequestParam(value = "roleId") Long roleId){
		log.info("Document Retrieval for RoleId>>>>" +roleId );
		System.out.println("Document Retrieval for RoleId>>>>" +roleId);
		List<Documents> docs = docService.getRoleDocuments(roleId);

		return docs;
		
	}

}
