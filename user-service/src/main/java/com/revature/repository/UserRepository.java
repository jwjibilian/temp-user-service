package com.revature.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revature.models.AppUser;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Integer>{
	
	AppUser findUserByUsername(String username);
	AppUser findUserByEmail(String email);
	AppUser findUserByUsernameAndPassword(String username, String password);

}
