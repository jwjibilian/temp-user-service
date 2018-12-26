package com.revature.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.revature.exceptions.UserCreationException;
import com.revature.exceptions.UserNotFoundException;
import com.revature.models.AppUser;
import com.revature.models.UserErrorResponse;
import com.revature.service.UserService;

/*
 * TODO 
 * 
 * 		1) Currently, users can edit any other user if using Postman or curl.
 * 		   There needs to be a way to ensure that a user record can only be 
 * 		   updated by a user a matching id, or an admin.
 * 
 * 		2) Should we implement method-level security, instead of using the 
 * 		   HttpSecurity object in the SecurityCredentialsConfig.class? Delete
 * 		   mapping for this controller already uses it.
 */

/**
 * This class contains all CRUD functionality for the users
 * 
 * @author Caleb
 *
 */
@RestController
@RequestMapping("/users")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class UserController {

	private UserService userService;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	/**
	 * This method will get all users
	 * 
	 * @return All users
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public List<AppUser> getAllUsers() {
		return userService.findAllUsers();
	}

	/**
	 * This method will get the user with the specified id
	 * 
	 * @param id
	 * @return The user with specified id
	 * @throws UserNotFoundException
	 */
	@GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public AppUser getUserById(@PathVariable int id) {
		if (userService.findById(id) == null)
			throw new UserNotFoundException("There is no user with that ID.");
		return userService.findById(id);
	}

	/**
	 * This method will get the user with the specified username
	 * 
	 * @param username
	 * @return The user with specified username
	 * @throws UserNotFoundException
	 */
	@GetMapping(value = "/username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public AppUser getUserByUsername(@PathVariable String username) {
		if (userService.findUserByUsername(username) == null)
			throw new UserNotFoundException("There is no user with that username.");
		return userService.findUserByUsername(username);
	}

	/**
	 * This method will return the user with the specified email
	 * 
	 * @param email
	 * @return The user with the specified email
	 * @throws UserNotFoundException
	 */
	@GetMapping(value = "/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public AppUser getUserByEmail(@PathVariable String email) {
		if (userService.findUserByEmail(email) == null)
			throw new UserNotFoundException("There is no user with that email address.");
		return userService.findUserByEmail(email);
	}

	/**
	 * This is the method for registering a new user
	 * 
	 * @param user
	 * @return The user who was just registered
	 * @throws UserCreationException
	 */
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public AppUser registerUser(@RequestBody AppUser user) {
		if (userService.addUser(user) == null)
			throw new UserCreationException("That username or email already exists.");
		return user;
	}

	/**
	 * This method will update a user with the newly provided information
	 * 
	 * @param frontEndUser This is the user information that is taken from the front end.
	 * @param auth
	 * @return frontEndUser This is the updated frontEndUser with information filled in from the back
	 * @return null if any of the fields are blank
	 * @throws UserNotFoundException
	 */
	/*
	 * TODO This needs to be cleaned up. There is likely a more efficient way to
	 * check if passed in values are null.
	 */
	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public AppUser updateUser(@RequestBody AppUser frontEndUser, Authentication auth) {
		AppUser backEndUser = userService.findUserByUsername(auth.getPrincipal().toString());
		if (!backEndUser.getUsername().equals(frontEndUser.getUsername())) {
			throw new UserNotFoundException("Username cannot be changed.");
		}
		if (!backEndUser.getPassword().equals(frontEndUser.getPassword())) {
			throw new UserNotFoundException("Password is not the same.");
		}
		if (frontEndUser.getEmail() == null) {
			frontEndUser.setEmail(backEndUser.getEmail());
		}
		if (frontEndUser.getFirstName() == null) {
			frontEndUser.setFirstName(backEndUser.getFirstName());
		}
		if (frontEndUser.getLastName() == null) {
			frontEndUser.setLastName(backEndUser.getLastName());
		}
		/*	This function will be used when updating password for the separate form to just update passwords.
		 * if (frontEndUser.getPassword() == null) {
			frontEndUser.setPassword(backEndUser.getPassword());
		}*/
		if (frontEndUser.getId() == null) {
			throw new UserNotFoundException("Id cannot be null!");
		}
		if (userService.findById(frontEndUser.getId()) == null) {
			throw new UserNotFoundException("User with id: " + frontEndUser.getId() + " not found");
		}
		if (!userService.updateUser(frontEndUser)) {
			throw new UserNotFoundException("User with id: " + frontEndUser.getId() + "not found");
		}
		return frontEndUser;
	}
	
	

	/**
	 * This method will delete a user given that user's id. This method is only
	 * accessible to users with the ADMIN role
	 * 
	 * @param id
	 * @throws UserNotFoundException
	 */
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping(value = "/id/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public void deleteUser(@PathVariable int id) {
		AppUser user = userService.findById(id);
		if (user.getId() == null)
			throw new UserNotFoundException("Id cannot be null!");
		if (userService.findById(user.getId()) == null)
			throw new UserNotFoundException("User with id: " + user.getId() + " not found");
		if (!userService.deleteUserById(user.getId()))
			throw new UserNotFoundException("User with id: " + user.getId() + " does not exist.");
	}

	/**
	 * This handles any UserNotFoundException thrown in the AuthController.
	 * 
	 * @param unfe
	 * @return This method will return an error of type UserErrorResponse
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public UserErrorResponse handleUserNotFoundException(UserNotFoundException unfe) {
		UserErrorResponse error = new UserErrorResponse();
		error.setStatus(HttpStatus.NOT_FOUND.value());
		error.setMessage(unfe.getMessage());
		error.setTimestamp(System.currentTimeMillis());
		return error;
	}

	/**
	 * This handles any UserCreationException thrown in the AuthController.
	 * 
	 * @param uce
	 * @return This method will return an error of type UserErrorResponse
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.CONFLICT)
	public UserErrorResponse handleUserCreationException(UserCreationException uce) {
		UserErrorResponse error = new UserErrorResponse();
		error.setStatus(HttpStatus.CONFLICT.value());
		error.setMessage(uce.getMessage());
		error.setTimestamp(System.currentTimeMillis());
		return error;
	}

}
