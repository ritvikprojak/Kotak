package com.sb.filenet.genericapi.service.impl;

import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.ObjectStore;
import com.sb.filenet.genericapi.service.ICESession;

import javax.security.auth.Subject;


/**
 * @author MK
 * 
 */
public class CESession implements ICESession
{

    private static final long serialVersionUID = 131264789665537411L;

    private final String user;
    private final String password;
    private Connection conn;
    private Domain domain;
    private ObjectStore objectStore;
    private Subject subject = null;

    /**
     * @param user
     * @param password
     * @param conn
     * @param domain
     * @param objectStore
     */
    protected CESession(String user, String password, Connection conn, Domain domain, ObjectStore objectStore)
    {
        super();
        this.user = user;
        this.password = password;
        this.conn = conn;
        this.domain = domain;
        this.objectStore = objectStore;
    }

    protected CESession(String user, String password, Connection conn, Domain domain, ObjectStore objectStore, Subject subject)
    {
        super();
        this.user = user;
        this.password = password;
        this.conn = conn;
        this.domain = domain;
        this.objectStore = objectStore;
        this.subject = subject;
    }

    /**
     * @return the user
     */
    public String getUser()
    {
        return user;
    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @return the conn
     */
    public Connection getConn()
    {
        return conn;
    }

    /**
     * @return the domain
     */
    public Domain getDomain()
    {
        return domain;
    }

    /**
     * @return the objectStore
     */
    public ObjectStore getObjectStore()
    {
        return objectStore;
    }

    public void disConnect()
    {
        conn = null;
        domain = null;
        objectStore = null;

    }
}