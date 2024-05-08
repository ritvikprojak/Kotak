/*package com.abc.kotak.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.abc.kotak.security.UserNotActivatedException;
import com.abc.kotak.model.User;
import com.abc.kotak.repository.LdapRepository;
import com.abc.kotak.repository.UserRepository;


@Service
public class JwtUserDetailsService implements UserDetailsService {
		private final Logger log = LoggerFactory.getLogger(JwtUserDetailsService.class);

	    private final LdapRepository ldapUserRepository;
	    
	    private final UserRepository userRepository;
	    

	    public JwtUserDetailsService(UserRepository userRepository,LdapRepository ldapUserRepository) {
	        this.userRepository = userRepository;
	        this.ldapUserRepository=ldapUserRepository;
	    }
	
	@Override
	public UserDetails loadUserByUsername(final String login) throws UsernameNotFoundException {
		if ("p8admin".equals(username)) {
			return new User("p8admin", "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6",
					new ArrayList<>());
		} else {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		
		 log.debug("Authenticating {}", login);
	        String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
	        Optional<User> userFromDatabase = ldapUserRepository.findOneWithAuthoritiesByLogin(lowercaseLogin);
	        return userFromDatabase.map(user -> {
	            if (!user.getActivated()) {
	                throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
	            }
	            List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
	                    .map(authority -> new SimpleGrantedAuthority(authority.getName()))
	                .collect(Collectors.toList());
	            return new org.springframework.security.core.userdetails.User(lowercaseLogin,
	                user.getPassword(),
	                grantedAuthorities);
	        }).orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the " +
	        "database"));
		
	}
	
	
	private org.springframework.security.core.userdetails.User createSpringSecurityUser(String lowercaseLogin, User user) {
        if (!user.getActivated()) {
            throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
        }
        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
            .map(authority -> new SimpleGrantedAuthority(authority.getName()))
            .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(user.getLogin(),
            user.getPassword(),
            grantedAuthorities);
    }

}*/