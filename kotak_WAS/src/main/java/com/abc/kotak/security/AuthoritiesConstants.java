package com.abc.kotak.security;

import org.springframework.beans.factory.annotation.Value;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";
    
    public static final String CREATOR = "ROLE_CREATOR";
    
    public static final String APPROVER = "ROLE_APPROVER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";
    
    @Value("${HR_GROUP}")
    public static  String HRM="ROLE_HR_ADMINS";
    
    @Value("${SMS}")
    public static String SMS="ROLE_FN_SMS";;
    

    private AuthoritiesConstants() {
    }
}
