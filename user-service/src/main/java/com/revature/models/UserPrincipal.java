package com.revature.models;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;


/**
 * UserPrincipal
 * Custom implementation of provided User object from Spring Security. Extends User.
 */
public class UserPrincipal extends User {

	private static final long serialVersionUID = 1L;
	
	private final AppUser appUser;

	/**
	 * Constructor UserPrincipal
	 * @author StackOverflow
	 * @param appUser
	 * @param username
	 * @param password
	 * @param authorities
	 */
	public UserPrincipal(AppUser appUser, String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
		this.appUser = appUser;
		// TODO Auto-generated constructor stub
	}

	public AppUser getAppUser() {
		return appUser;
	}
	
}
