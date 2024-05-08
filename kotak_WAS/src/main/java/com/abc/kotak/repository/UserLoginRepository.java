package com.abc.kotak.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.abc.kotak.model.UserLogin;


@Repository
public interface UserLoginRepository extends JpaRepository<UserLogin, String> {

	
	@Query(value="SELECT * FROM USER_LOGIN_TABLE WHERE LOGIN_TIME = "
			+ "(SELECT MAX(LOGIN_TIME) FROM USER_LOGIN_TABLE WHERE DOMAIN_ID = ?1 AND SYSTEM = ?2)", nativeQuery= true)
	UserLogin userLatestLogin(String domainId, String system);
	
}
