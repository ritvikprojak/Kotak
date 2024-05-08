package Test;

import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;

public class LoginLDAP {

//    public static void main(String[] args) throws Exception {
//        Map<String,String> params = createParams(args);
//
//        String url = "ldaps://10.10.19.55:636/"; // ldap://1.2.3.4:389 or ldaps://1.2.3.4:636
//        String principalName = "fncmadmin@KGPUAT.com"; // firstname.lastname@mydomain.com
//        String domainName = "KGPUAT.com"; // mydomain.com or empty
//
//        if (domainName==null || "".equals(domainName)) {
//            int delim = principalName.indexOf('@');
//            domainName = principalName.substring(delim+1);
//        }
//
//        Properties props = new Properties();
//        props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
//        props.put(Context.PROVIDER_URL, url); 
//        props.put(Context.SECURITY_PRINCIPAL, principalName); 
//        props.put(Context.SECURITY_CREDENTIALS, "kotak@123");
//        props.put(Context.SECURITY_PROTOCOL, "ssl");
//        props.put(Context.SECURITY_AUTHENTICATION, "simple");
//        props.put("javax.net.ssl.trustStore", "C:\\Program Files\\Java\\jdk1.8.0_60\\jre\\lib\\security\\cacerts"); 
//        props.put("javax.net.ssl.trustStorePassword", "changeit");    
//      
//
//        InitialDirContext context = new InitialDirContext(props);
//        try {
//            SearchControls ctrls = new SearchControls();
//            ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
//            NamingEnumeration<SearchResult> results = context.search(toDC(domainName),"(& (userPrincipalName="+principalName+")(objectClass=user))", ctrls);
//            if(!results.hasMore())
//                throw new AuthenticationException("Principal name not found");
//
//            SearchResult result = results.next();
//            System.out.println("distinguisedName: " + result.getNameInNamespace() ); // CN=Firstname Lastname,OU=Mycity,DC=mydomain,DC=com
//
//            Attribute memberOf = result.getAttributes().get("memberOf");
//            if(memberOf!=null) {
//                for(int idx=0; idx<memberOf.size(); idx++) {
//                    System.out.println("memberOf: " + memberOf.get(idx).toString() ); // CN=Mygroup,CN=Users,DC=mydomain,DC=com
//                    //Attribute att = context.getAttributes(memberOf.get(idx).toString(), new String[]{"CN"}).get("CN");
//                    //System.out.println( att.get().toString() ); //  CN part of groupname
//                }
//            }
//        } finally {
//            try { context.close(); } catch(Exception ex) { }
//        }       
//    }

    /**
     * Create "DC=sub,DC=mydomain,DC=com" string
     * @param domainName    sub.mydomain.com
     * @return
     */
    private static String toDC(String domainName) {
        StringBuilder buf = new StringBuilder();
        for (String token : domainName.split("\\.")) {
            if(token.length()==0) continue;
            if(buf.length()>0)  buf.append(",");
            buf.append("DC=").append(token);
        }
        return buf.toString();
    }

    private static Map<String,String> createParams(String[] args) {
        Map<String,String> params = new HashMap<String,String>();  
        for(String str : args) {
            int delim = str.indexOf('=');
            if (delim>0) params.put(str.substring(0, delim).trim(), str.substring(delim+1).trim());
            else if (delim==0) params.put("", str.substring(1).trim());
            else params.put(str, null);
        }
        return params;
    }

}