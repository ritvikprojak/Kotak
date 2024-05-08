package com.sb.filenet.genericapi.service;

import java.io.Serializable;
/**
 * 
 * @author MK
 *
 */
public interface ISession extends Serializable
{

	String getUser();

	String getPassword();

}
