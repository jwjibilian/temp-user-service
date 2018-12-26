package com.revature.exceptions;

public class UserCreationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UserCreationException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public UserCreationException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public UserCreationException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
