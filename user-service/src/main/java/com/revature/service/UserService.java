package com.revature.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.revature.models.AppUser;
import com.revature.repository.UserRepository;

/**
 * This class contains methods that should be accessed by the controller to find and edit users.
 * @author Caleb
 *
 */
@Service
public class UserService {
	
	private UserRepository repo;
	
	public UserService() {}
	
	@Autowired
	public UserService(UserRepository repo) {
		this.repo = repo;
	}
	
	/**
	 * This method will find all users
	 * @return All users
	 */
	@Transactional(readOnly=true, isolation=Isolation.SERIALIZABLE)
	public List<AppUser> findAllUsers(){
		return repo.findAll();
	}
	
	/**
	 * This method will find a user with the specified id
	 * @param id
	 * @return The user with the specified id
	 */
	@Transactional(readOnly=true, isolation=Isolation.READ_COMMITTED)
	public AppUser findById(int id) {
		Optional<AppUser> optUser = repo.findById(id);
		if(optUser.isPresent()) return optUser.get();
		else return null;
	}
	
	/**
	 * This method will find a user with a given username
	 * @param username
	 * @return null if there is no user with that username
	 * @return The user with the given username
	 */
	@Transactional(readOnly=true, isolation=Isolation.READ_COMMITTED)
	public AppUser findUserByUsername(String username) {
		if(repo.findUserByUsername(username) == null) return null;
		else return repo.findUserByUsername(username);
	}
	
	/**
	 * This method will find a user with a given username
	 * @param email
	 * @return The user with the given username
	 */
	@Transactional(readOnly=true, isolation=Isolation.READ_COMMITTED)
	public AppUser findUserByEmail(String email) {
		return repo.findUserByEmail(email);
	}
	
	/**
	 * This method will save a new user to the DB if it does not already exist
	 * @param newUser
	 * @return null if the username or email already exists
	 * @return the new user that was created
	 */
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public AppUser addUser(AppUser newUser) {
		AppUser tempUser = findUserByUsername(newUser.getUsername());
		if(tempUser != null) return null;
		tempUser = findUserByEmail(newUser.getEmail());
		if(tempUser != null) return null;
		newUser.setRole("ROLE_USER");
		return repo.save(newUser);
	}

	/**
	 * This method will update a user's information
	 * @param user
	 * @return false if the user does not exist
	 * @return true if the user exists and was updated
	 */
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public boolean updateUser(AppUser user) {
		if(user == null) return false;
		user.setRole("ROLE_USER");
		repo.save(user);
		return true;
	}
	
	/**
	 * This method should delete a user form the DB given an id
	 * @param id
	 * @return true if the user with that id was found and deleted
	 * @return false if the user with the given id was not found
	 */
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public boolean deleteUserById(int id) {
		Optional<AppUser> optUser = repo.findById(id);
		if(optUser.isPresent()) {
			AppUser tempUser = optUser.get();
			repo.delete(tempUser);
			return true;
		}
		else {
			return false;
		}
	}
	
}
