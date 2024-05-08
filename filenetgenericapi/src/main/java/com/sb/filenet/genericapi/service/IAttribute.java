package com.sb.filenet.genericapi.service;

import com.sb.filenet.genericapi.util.DataType;

import java.io.Serializable;

/**
 * @author Praveen Kumar Yerra
 * 
 */
public interface IAttribute extends Serializable
{

	String getName();
	
	String getValue();
	
	DataType getDataType();
	
	boolean isRequired();

}
