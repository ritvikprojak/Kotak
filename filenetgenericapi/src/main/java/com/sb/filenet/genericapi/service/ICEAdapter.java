package com.sb.filenet.genericapi.service;

import com.sb.filenet.genericapi.exception.FNException;
import com.sb.filenet.genericapi.response.RetrieveDocResponse;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;

/**
 * 
 * @author MK
 *
 */

public interface ICEAdapter extends Serializable
{
    ICESession connect(String user, String password, Properties prop) throws FNException;

    void disconnect(ICESession session);

    RetrieveDocResponse getContentAsStream(ICESession session, String documentId) throws FNException;
    

    String importDocument(ICESession session, InputStream inputStream, List<IAttribute> attributes, String docClassName, String fileName) throws FNException;


	List<RetrieveDocResponse> searchAttachDocByAttributes(ICESession session, List<IAttribute> keyValuePairs,String docClass) throws FNException;

}
