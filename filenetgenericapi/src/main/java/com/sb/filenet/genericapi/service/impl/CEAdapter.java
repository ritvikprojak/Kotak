package com.sb.filenet.genericapi.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.Subject;

import org.apache.log4j.Logger;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.IndependentObjectSet;
import com.filenet.api.collection.PageIterator;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Connection;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.exception.ExceptionCode;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.Properties;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.Id;
import com.filenet.api.util.UserContext;
import com.sb.filenet.genericapi.exception.FNException;
import com.sb.filenet.genericapi.response.RetrieveDocResponse;
import com.sb.filenet.genericapi.service.IAttribute;
import com.sb.filenet.genericapi.service.ICEAdapter;
import com.sb.filenet.genericapi.service.ICESession;
import com.sb.filenet.genericapi.util.DataType;
import com.sb.filenet.genericapi.util.DocumentConverter;
import ch.qos.logback.core.net.SyslogOutputStream;


/**
 * 
 * @author MK
 *
 */
public class CEAdapter implements ICEAdapter
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5126358532585177258L;

	private final Logger log = Logger.getLogger(CEAdapter.class);

	private static ICEAdapter instance = null;

	private static String objectstore_Name = null;


	private final PropertyFilter pf = new PropertyFilter();

	/**
	 * Exists only to defeat instantiation.
	 */
	private CEAdapter()
	{
		//pf.addIncludeType(0, null, Boolean.TRUE, FilteredPropertyType.ANY, null);
		pf.addIncludeProperty(new FilterElement(1, null, null,"Id CurrentVersion ReleasedVersion",1));
	}

	/**
	 * This method creates an CEAdapter instance, if it doesn't exist and returns the CEAdapter instance object
	 * 
	 * @return CEAdapter object
	 */
	public static ICEAdapter getInstance()
	{
		if (instance == null)
		{
			instance = new CEAdapter();
		}
		return instance;
	}

	/**
	 * This method creates the custom defined Domain Type CE Session object
	 * 
	 * @param user
	 * @param password
	 * @param prop
	 * @return ICESession object
	 */
	public ICESession connect(String user, String password, java.util.Properties prop) throws FNException
	{
		ICESession session = null;

		try
		{
			String uri = prop.getProperty("URI");
			String stanza = prop.getProperty("Stanza");
			String objectStoreName = prop.getProperty("ObjectStoreName");
			if (null == objectstore_Name)
			{
				objectstore_Name = objectStoreName;
			}

			// Get the connection
			Connection conn = Factory.Connection.getConnection(uri);

			// Get the user context
			//UserContext userContext = UserContext.get();
			// Build the subject using the stanza
			Subject subject = UserContext.createSubject(conn, user, password, stanza);
			// Push the subject into user context
			UserContext.get().pushSubject(subject);

			// Get the default domain
			Domain domain = Factory.Domain.getInstance(conn, null);

			// Get an object store
			ObjectStore objectStore = Factory.ObjectStore.getInstance(domain, objectStoreName);

			// Create the custom defined Domain Type CE Session object
			session = new CESession(user, password, conn, domain, objectStore);

			// Pop the subject from user context
			// userContext.popSubject();
		}
		catch (EngineRuntimeException e)
		{
			if (e.getExceptionCode() == ExceptionCode.E_NOT_AUTHENTICATED)
			{
				throw new FNException("Invalid login credentials supplied - please try again. ", e);
			}
			else if (e.getExceptionCode() == ExceptionCode.API_UNABLE_TO_USE_CONNECTION)
			{
				throw new FNException("Unable to connect to server.  Please check to see that URL is correct and server is running. ", e);
			}
			else
			{
				throw new FNException("An exception occurred while establishing an connection with content engine. " + e.getMessage(), e);
			}
		}

		return session;
	}

	/**
	 * This method nullifies connection related data members in the Domain Type CE session object
	 * 
	 * @param session
	 */
	@Override
	public void disconnect(ICESession session)
	{
		CESession ceSession = (CESession) session;
		ceSession.disConnect();
	}

	/**
	 * This method retrieves the content of an Document Object from Content Engine Object Store
	 * 
	 * @param session
	 * @param documentId
	 * @return The document object content as InputStream
	 */
	@Override
	public RetrieveDocResponse getContentAsStream(ICESession session, String documentId) throws FNException
	{
		pushSubject(session);

		InputStream is = null;
		RetrieveDocResponse  docResponse =  new RetrieveDocResponse();

		try
		{
			Id docId = new Id(documentId);
			Document document = Factory.Document.fetchInstance(getObjectStore(session), docId, null);
			if(log.isInfoEnabled()){
				log.info("No. of document content elements: " + document.get_ContentElements().size());
			}
			is = document.accessContentStream(0);

			docResponse.setFileContent(is);
			ContentElementList docContentList = document.get_ContentElements();
			if(null != docContentList) {
				ContentTransfer ct = (ContentTransfer) docContentList.get(0);
				// Print element sequence number and content type of the element.
				if (log.isInfoEnabled()) {
					log.info("\n file name : " + ct.get_RetrievalName() + "\n Content type: " + ct.get_ContentType() + "\n");
				}
				docResponse.setMimeType(ct.get_ContentType());
				docResponse.setFileName(ct.get_RetrievalName());
			}
		}catch (Exception e) {
			throw new FNException(e.getMessage(), e.getCause());
		}
		finally
		{
			popSubject();
		}

		return docResponse;
	}



	/**
	 * This method imports content into Content Engine Object Store as a Document Object
	 * 
	 * @param session
	 * @param inputStream
	 * @param attributes
	 * @param docClassName
	 * @param fileName
	 * @return Id of Document Object created
	 * @throws FNException
	 */
	@Override
	public String importDocument(ICESession session, InputStream inputStream, List<IAttribute> attributes, String docClassName, String fileName) throws FNException
	{
		String documentId = null;
		pushSubject(session);
		try
		{
			//no R/T
			Document document = Factory.Document.createInstance(getObjectStore(session), docClassName);
			populateDocumentProperties(document, attributes);
			// Prepare the content for attaching
			// Create the element list        //no R/T
			ContentElementList contentList = Factory.ContentElement.createList();
			// Create a content transfer element by attaching a simple file         //no R/T
			ContentTransfer element = Factory.ContentTransfer.createInstance();
			// Set the MIME type
			element.set_ContentType( document.get_MimeType());
			// Set the retrieval name
			element.set_RetrievalName( fileName );
			// Set the content source
			element.setCaptureSource(inputStream);           
			contentList.add(element);

			document.set_ContentElements(contentList);
			//no R/T
			document.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);

			document.save(RefreshMode.REFRESH);

			documentId = document.get_Id().toString();

		}
		catch (Exception e)
		{
			throw new FNException("An exception occurred while creating document object in content engine.", e);
		}
		finally
		{
			popSubject();
		}

		return documentId + "|" + objectstore_Name;
	}

	/**
	 * This method creates and pushes the Subject into UserContext for the CESession
	 * 
	 * @param session
	 */
	private void pushSubject(ICESession session)
	{
		CESession ceSession = (CESession) session;

		Subject subject = UserContext.createSubject(ceSession.getConn(), ceSession.getUser(), ceSession.getPassword(),"FileNetP8WSI");

		UserContext.get().pushSubject(subject);
	}

	/**
	 * This method pops out the Subject from UserContext
	 */
	private void popSubject()
	{
		UserContext.get().popSubject();
	}

	/**
	 * This method gets ObjectStore from CESession
	 *
	 * @param session
	 * @return ObjectStore object
	 */
	private ObjectStore getObjectStore(ICESession session)
	{
		CESession ceSession = (CESession) session;
		pushSubject(session);
		ObjectStore objectStore = ceSession.getObjectStore();
		return objectStore;
	}


	/**
	 * This method populates properties value of Document Object
	 *
	 * @param document
	 * @param attributes
	 */

	private void populateDocumentProperties(Document document, List<IAttribute> attributes)
	{
		Properties documentProperties = document.getProperties();
		for (Iterator<IAttribute> iterator = attributes.iterator(); iterator.hasNext();)
		{
			IAttribute attribute = iterator.next();
			String attribName = attribute.getName();
			DataType dataType = attribute.getDataType();
			String value = attribute.getValue();
			// DataCardinality dataCardinality = attribute.getDataCardinality();

			switch (dataType)
			{
			case STRING:
				if (value == null || value.length() == 0)
				{
					documentProperties.putValue(attribName, ((String) null));
				}
				else
				{
					documentProperties.putValue(attribName, value);
				}

				break;
			case DATE:
				if (value == null)
				{
					documentProperties.putValue(attribName, ((Date) null));
				}
				else
				{
					documentProperties.putValue(attribName, new Date(value));
				}
				break;
			case DOUBLE:
				if (value == null || value.length() == 0)
				{
					documentProperties.putValue(attribName, ((Double) null));
				}
				else
				{
					documentProperties.putValue(attribName, new Double(value));
				}
				break;
			case GUID:
				if (value == null || value.length() == 0)
				{
					documentProperties.putValue(attribName, ((Id) null));
				}
				else
				{
					documentProperties.putValue(attribName, new Id(value));
				}
				break;
			case LONG:
				if (value == null || value.length() == 0)
				{
					documentProperties.putValue(attribName, ((Integer) null));
				}
				else
				{
					documentProperties.putValue(attribName, new Integer(value));
				}
				break;

			case BOOLEAN:
				if (value == null || value.length() == 0)
				{
					documentProperties.putValue(attribName, (Boolean) null);
				}
				else
				{
					documentProperties.putValue(attribName, new Boolean(value));
				}
				break;

			case OBJECT:
				if (value == null || value.length() == 0)
				{
					documentProperties.putValue(attribName, ((String) null));
				}
				else
				{
					Object objectValue = (attribute).getValue();
					documentProperties.putObjectValue(attribName, objectValue);
				}
				break;
			case BINARY:
				if (value == null || value.length() == 0)
				{
					documentProperties.putValue(attribName, "".getBytes());
				}
				else
				{
					documentProperties.putValue(attribName, value.getBytes());
				}
				break;

			}
		}

	}

	@Override
	public List<RetrieveDocResponse> searchAttachDocByAttributes(ICESession session, List<IAttribute> keyValuePairs,String docCls) throws FNException
	{
		List<RetrieveDocResponse> documentList = new ArrayList<RetrieveDocResponse>();

		pushSubject(session);
		// com.filenet.wcm.api.ObjectStore os =
		// ObjectFactory.getObjectStore(osName, ceSession);

		ObjectStore objectStore = getObjectStore(session);



		try
		{
			StringBuilder whereClause = new StringBuilder();

			//Set<Entry<String, String>> entrySet = keyValuePairs.entrySet();
			//Iterator<Entry<String, String>> iterator = entrySet.iterator();
			Iterator<IAttribute> iterator = keyValuePairs.iterator();

			if (iterator.hasNext())
			{
				IAttribute entry = iterator.next();
				// TODO Classify String & non-String values to enclose with
				// Quotes or not ???

				whereClause.append(entry.getName());
				whereClause.append("=");
				/*
				 * if (entry.getDataType().equals("STRING") ) {
				 * whereClause.append("'"+entry.getValue()+"'");
				 * 
				 * 
				 * } else if (entry.getDataType().equals("LONG")) {
				 * 
				 * whereClause.append(entry.getValue());
				 * 
				 * }
				 */
				log.info(entry.getDataType());

				switch (entry.getDataType())
				{
				case STRING:
					whereClause.append("'"+entry.getValue()+"'");
					break;

				case LONG:
					whereClause.append(entry.getValue());
					break;
					
				case GUID:
					whereClause.append("'"+entry.getValue()+"'");
			        break;

				}

			}
			while (iterator.hasNext())
			{
				IAttribute entry = iterator.next();

				whereClause.append(" AND ");
				whereClause.append(entry.getName());
				whereClause.append("=");
				whereClause.append("'"+entry.getValue()+"'");

			}
			log.info("Where Cluase is "+whereClause.toString());

			SearchSQL searchSQL = new SearchSQL();
			searchSQL.setSelectList("*");
			searchSQL.setFromClauseInitialValue(docCls, null, true);
			searchSQL.setWhereClause(whereClause.toString());

			SearchScope scope = new SearchScope(objectStore);
			log.info(searchSQL.toString());
			IndependentObjectSet objectSet = scope.fetchObjects(searchSQL, 100, null, false);

			// TODO Modify PageIterator to Iterator, if pagination is not
			// required
			PageIterator pageIterator = objectSet.pageIterator();

			if (pageIterator.nextPage())
			{
				for (Object obj : pageIterator.getCurrentPage())
				{
					InputStream is = null;
					RetrieveDocResponse  docResponse1 =  new RetrieveDocResponse();
					Document document = (Document) obj;
					//Stdocument.get_AuditedEvents();

					if(log.isInfoEnabled()){
						log.info("No. of document content elements: " + document.get_ContentElements().size());
					}

					is = document.accessContentStream(0);
					DocumentConverter dc = new DocumentConverter();

					docResponse1.setFilebase64Content(dc.getBase64FromInputStream(is));
					docResponse1.setMimeType(document.get_MimeType());
					docResponse1.setFileName(document.get_Name());

					documentList.add(docResponse1);

				}
			}
		}
		catch (Exception e) {
			log.info("Exception while getting the document details",e);
			throw new FNException(e.getMessage(), e.getCause());
		}
		finally
		{
			popSubject();
		}

		return documentList;
	}


}
