package com.abc.kotak.repository;

import com.abc.kotak.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.time.Instant;

import org.springframework.stereotype.Component;

@Component
public class LdapRepository{

	public Optional<User> findOneByActivationKey(String activationKey){
        return null;
    }

    public List<User> findAllByActivatedIsFalseAndCreatedDateBefore(Instant dateTime){
        return null;
    }

    public Optional<User> findOneByResetKey(String resetKey){
        return null;
    }

    public Optional<User> findOneByEmail(String email){
        return null;
    }

    public Optional<User> findOneByLogin(String login){
        return null;
    }

    @EntityGraph(attributePaths = "authorities")
    public User findOneWithAuthoritiesById(Long id){
        return null;
    }

    @EntityGraph(attributePaths = "authorities")
    public Optional<User> findOneWithAuthoritiesByLogin(String login){
        return null;
    }

    public Page<User> findAllByLoginNot(Pageable pageable, String login){
        return null;
    }

}