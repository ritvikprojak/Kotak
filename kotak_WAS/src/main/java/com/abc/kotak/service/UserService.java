package com.abc.kotak.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.abc.kotak.config.ApplicationProperties;
import com.abc.kotak.config.Constants;
import com.abc.kotak.model.Authority;
import com.abc.kotak.model.RestUser;
import com.abc.kotak.model.User;
import com.abc.kotak.model.UserLogin;
import com.abc.kotak.repository.AuthorityRepository;
import com.abc.kotak.repository.LdapRepository;
import com.abc.kotak.repository.UserLoginRepository;
import com.abc.kotak.repository.UserRepository;
import com.abc.kotak.security.ApplicationNotActiveException;
import com.abc.kotak.security.AuthoritiesConstants;
import com.abc.kotak.security.SecurityUtils;
import com.abc.kotak.service.dto.UserDTO;
import com.abc.kotak.service.mapper.UserMapper;
import com.abc.kotak.service.util.RandomUtil;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;
	
	//private final ReportingPeriodRepository reportingPeriodRepository;

    private final CacheManager cacheManager;
    
    private final ApplicationProperties applicationProperties;
    
    private final LdapRepository ldapRepository;
    
    
    @Value("${application.rest.url}")
    private String pathToUpmUrl;


	@Autowired
    private UserMapper userMapper;
	
	@Value("${application.rest.ascAprover}")
    private String ascAprover;

	
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository, ApplicationProperties applicationProperties,LdapRepository ldapRepository, CacheManager cacheManager, UserLoginRepository loginRepo) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.cacheManager = cacheManager;
		//this.reportingPeriodRepository = reportingPeriodRepository;
		this.applicationProperties = applicationProperties;
		this.ldapRepository=ldapRepository;
    }

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository.findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
                cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
                log.debug("Activated user: {}", user);
                return user;
            });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
       log.debug("Reset user password for reset key {}", key);

       return userRepository.findOneByResetKey(key)
           .filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(86400)))
           .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
                cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
                return user;
           });
    }

    public Optional<User> requestPasswordReset(String mail) {
        return userRepository.findOneByEmailIgnoreCase(mail)
            .filter(User::getActivated)
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
                cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
                return user;
            });
    }

	public User createUser(String login, String password, String firstName, String lastName, String email,
        String imageUrl, String langKey) {

        User newUser = new User();
        Authority authority = authorityRepository.findOne(AuthoritiesConstants.USER);
        Set<Authority> authorities = new HashSet<>();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(login);
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setImageUrl(imageUrl);
        newUser.setLangKey(langKey);
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        authorities.add(authority);
        newUser.setAuthorities(authorities);
        //userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }
    public User registerUser(UserDTO userDTO, String password) {

        User newUser = new User();
        Authority authority = authorityRepository.findOne(AuthoritiesConstants.USER);
        Set<Authority> authorities = new HashSet<>();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(userDTO.getLogin());
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        newUser.setEmail(userDTO.getEmail());
        newUser.setImageUrl(userDTO.getImageUrl());
        newUser.setLangKey(userDTO.getLangKey());
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        authorities.add(authority);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(newUser.getLogin());
        cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(newUser.getEmail());
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public User createUser(UserDTO userDTO) {
        User user = new User();
        user.setLogin(userDTO.getLogin());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setImageUrl(userDTO.getImageUrl());
        if (userDTO.getLangKey() == null) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
       /* if (userDTO.getAuthorities() != null) {
            Set<Authority> authorities = userDTO.getAuthorities().stream()
                .map(authorityRepository::findOne)
                .collect(Collectors.toSet());
            user.setAuthorities(authorities);
        }*/
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);
        userRepository.save(user);
        cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
        cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
        log.debug("Created Information for User: {}", user);
        return user;
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user
     * @param lastName last name of user
     * @param email email id of user
     * @param langKey language key
     * @param imageUrl image URL of user
     */
    public void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmail(email);
                user.setLangKey(langKey);
                user.setImageUrl(imageUrl);
                /*cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
                cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());*/
                log.debug("Changed Information for User: {}", user);
            });
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update
     * @return updated user
     */
    public Optional<UserDTO> updateUser(UserDTO userDTO) {
        return Optional.of(userRepository
            .findOne(userDTO.getId()))
            .map(user -> {
                user.setLogin(userDTO.getLogin());
                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                user.setEmail(userDTO.getEmail());
                user.setImageUrl(userDTO.getImageUrl());
                user.setActivated(userDTO.isActivated());
                user.setLangKey(userDTO.getLangKey());
                Set<Authority> managedAuthorities = user.getAuthorities();
                managedAuthorities.clear();
                userDTO.getAuthorities().stream()
                    .map(authorityRepository::findOne)
                    .forEach(managedAuthorities::add);
                cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
                cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
                log.debug("Changed Information for User: {}", user);
                return user;
            })
            .map(UserDTO::new);
    }

    public void deleteUser(String login) {
        userRepository.findOneByLogin(login).ifPresent(user -> {
            userRepository.delete(user);
            cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
            cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
            log.debug("Deleted User: {}", user);
        });
    }

    public void changePassword(String password) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                String encryptedPassword = passwordEncoder.encode(password);
                user.setPassword(encryptedPassword);
                cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
                cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
                log.debug("Changed password for User: {}", user);
            });
    }
    
    
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAllByLoginNot(pageable, Constants.ANONYMOUS_USER).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneWithAuthoritiesByLogin(login);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities(Long id) {
        return userRepository.findOneWithAuthoritiesById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {
    	LocalDate a = LocalDate.now();
    	/*Set <ReportingPeriod> reportingPeriod = reportingPeriodRepository.getReportingPeriods();
    	String errMsg=null;
    	for (ReportingPeriod rp : reportingPeriod) {
    		errMsg=rp.getMessage();
		}
    	System.out.println("reportingPeriod : "+errMsg);
    	*/
    	Optional<String> userDeatils=SecurityUtils.getCurrentUserLogin();
    	//String strLdapUser=ldapUser.getUsername();
    	String strLdapUser=userDeatils.get();
    	
    	System.out.println("LDAP USER "+strLdapUser);
    	String restURL=applicationProperties.getRest().getUrl();
    	System.out.println("REST URL "+restURL);
    	Set<Authority> authorities = new HashSet<>();
        Authority dafaultAuthority = new Authority();
        
        dafaultAuthority.setName(AuthoritiesConstants.USER);
        authorities.add(dafaultAuthority);
        
        RestTemplate restTemplate = new RestTemplate();
        RestUser responseEntity=null;
        
        String  ascEnterer = applicationProperties.getRest().getAscEnterer();
		
		//String ascAprover = applicationProperties.getRest().getAscAprover();
	   
		//String divcodeBranch = applicationProperties.getRest().getDivcodeBranch(); // Currently Not in Use
		
		//String divcodeDept = applicationProperties.getRest().getDivcodeDept(); // Currently Not in Use
		
		String divcodeComp = applicationProperties.getRest().getDivcodeComp(); 
		
		String adminUser = applicationProperties.getRest().getAdminUser();
        
        Optional<User> user2;
        try{
        	responseEntity = restTemplate.getForObject(strLdapUser, RestUser.class);		//http://192.125.125.96  143(dev)
        
        RestUser restUser=responseEntity;
       
        Authority roleAuthority = new Authority();
        
       // reportingPeriod = reportingPeriodRepository.getReportingPeriod();
        if(null != restUser)
        { 
        	log.info("User Authenticated Successfully - "+strLdapUser+"-  login time -"+new Date(System.currentTimeMillis()));
        	/*if(reportingPeriod.isEmpty()){
        		
        		throw new ApplicationNotActiveException(errMsg);
        	}*/
        	roleAuthority.setName(AuthoritiesConstants.CREATOR);
        }else if(restUser.getAPPLICATIONSECURITYCLASS().equals(ascAprover) && restUser.getDIVCODE().equalsIgnoreCase(divcodeComp)){
        	log.info("User Authenticated Successfully - "+strLdapUser+"-  login time -"+new Date(System.currentTimeMillis()));
        	roleAuthority.setName(AuthoritiesConstants.APPROVER);
    	}else{
    		throw new ApplicationNotActiveException("User "+strLdapUser+" not have access ");
    	}
        authorities.add(roleAuthority);
        if(strLdapUser.equals("admin") || strLdapUser.equals(adminUser)){
        	Authority adminAuthority = new Authority();
        	adminAuthority.setName(AuthoritiesConstants.ADMIN);
        	authorities.add(adminAuthority);
        }
        User user = createUser(restUser.getUSERID(), "password", restUser.getFIRSTNAME(), restUser.getLASTNAME(), ""+restUser.getEMAIL(), "http://placehold.it/50x50", "en-US");
        user.setLogin(restUser.getUSERID());
        if(restUser.getACTIVESTATUS().equalsIgnoreCase("A"))
        	user.setActivated(true);
        else
        	user.setActivated(false);
        user.setCreatedDate(Instant.now().minus(30, ChronoUnit.DAYS));
        user.setAuthorities(authorities);
        user2=Optional.ofNullable(user);
        
        }catch (Exception e) {
            throw new ApplicationNotActiveException("User authentication failed for "+strLdapUser+" due to "+e.getMessage()); 
        }
        
        return user2;
        //return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS));
        for (User user : users) {
            log.debug("Deleting not activated user {}", user.getLogin());
            userRepository.delete(user);
            cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).evict(user.getLogin());
            cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).evict(user.getEmail());
        }
    }

    /**
     * @return a list of all the authorities
     */
    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).collect(Collectors.toList());
    }

	public List<UserDTO> getAllApprovers() {
		String restURL=applicationProperties.getRest().getUrl();
		List<User> userDTOList=new LinkedList<User>();
		RestTemplate restTemplate = new RestTemplate();
		//changes done by akshay on 26th Dec 2018-Approver drop down issue
		String ascAprover = applicationProperties.getRest().getAscAprover();
		String compdivCode = applicationProperties.getRest().getDivcodeComp();
        RestUser[] responseEntity = restTemplate.getForObject(pathToUpmUrl+"/GetUsersForRole/"+compdivCode+"/"+ascAprover+"/SCR/", RestUser[].class);
        List<RestUser> restUsers=Arrays.asList(responseEntity);
        for (RestUser restUser : restUsers) {
        	User user=new User();
        	user.setLogin(restUser.getUSERID());
        	user.setFirstName(restUser.getFIRSTNAME());
        	user.setLastName(restUser.getLASTNAME());
        	user.setEmail(""+restUser.getEMAIL());
        	userDTOList.add(user);
		}
        return userMapper.usersToUserDTOs(userDTOList);
	}
	
}