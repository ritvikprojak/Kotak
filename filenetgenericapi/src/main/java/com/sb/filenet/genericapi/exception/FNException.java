package com.sb.filenet.genericapi.exception;


/**
 * @author Praveen Kumar Yerra
 * 
 */
public class FNException extends RuntimeException
{
	

	private static final long serialVersionUID = 1959169048546524152L;

	public FNException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public FNException(String message)
	{
	    super(message);
	}

}
