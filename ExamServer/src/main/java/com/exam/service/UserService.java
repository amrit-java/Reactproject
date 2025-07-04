package com.exam.service;

import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import com.exam.model.User;
import com.exam.model.UserRole;

public interface UserService {

	// creating user
	public User createUser(User user, Set<UserRole> userRoles) throws Exception;

	// get user by username
	public User getUser(String username);

	// GET DELETE user by user id
	public void deleteUser(Long userId);

	String generateCaptcha(HttpServletResponse response);

	boolean validateCaptcha(String userInputCaptcha, String sessionCaptcha);

}