package com.exam.serviceImpl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import com.google.code.kaptcha.Producer;

import org.springframework.stereotype.Service;

import com.exam.model.User;
import com.exam.model.UserRole;
import com.exam.repository.RoleRepository;
import com.exam.repository.UserRepository;
import com.exam.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private Producer captchaProducer;
	@Autowired

	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;

// creating user
	@Override
	public User createUser(User user, Set<UserRole> userRoles) throws Exception {

		User local = this.userRepository.findByUsername(user.getUsername());
		if (local != null) {
			System.out.println("Üser is already there !!");
			throw new Exception("User already Present!!");
		} else {
			// user create
			for (UserRole ur : userRoles) {
				roleRepository.save(ur.getRole());
			}
			user.getUserRoles().addAll(userRoles);
			local = this.userRepository.save(user);
		}

		return local;
	}

	// getting user by username
	@Override
	public User getUser(String username) {

		return this.userRepository.findByUsername(username);
	}

	// delete user by id
	@Override
	public void deleteUser(Long userId) {
		this.userRepository.deleteById(userId);

	}

	@Override
	public String generateCaptcha(HttpServletResponse response) {
		String captchaText = captchaProducer.createText();
		BufferedImage image = captchaProducer.createImage(captchaText);

		response.setContentType("image/png");
		try {
			ImageIO.write(image, "png", response.getOutputStream());
		} catch (IOException e) {
			throw new RuntimeException("Failed to write captcha image", e);
		}

		return captchaText;
	}

	@Override
	public boolean validateCaptcha(String userInputCaptcha, String sessionCaptcha) {
		return userInputCaptcha != null && userInputCaptcha.equalsIgnoreCase(sessionCaptcha);
	}

}

