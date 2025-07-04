package com.exam.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exam.model.Captcha;
import com.exam.model.Role;
import com.exam.model.User;
import com.exam.model.UserRole;
import com.exam.repository.CaptchaRepository;
import com.exam.service.CaptchaService;
import com.exam.service.UserService;
import com.google.code.kaptcha.Producer;

@RestController

@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private Producer captchaProducer; // ✅ Correctly autowired the producer bean

	@Autowired
	private CaptchaService captchaService;

	@Autowired
	private CaptchaRepository captchaRepository;

	@PostMapping("/")
	public User createUser(@RequestBody User user) throws Exception {

		user.setProfile("default.png");

		// encoding password with bcryptpasswordencoder
		user.setPassword(this.bCryptPasswordEncoder.encode(user.getPassword()));

		Set<UserRole> roles = new HashSet<>();
		Role role = new Role();
		role.setRoledId(45L);
		role.setRoleName("default");
		UserRole userRole = new UserRole();
		userRole.setUser(user);
		userRole.setRole(role);

		roles.add(userRole);
		return this.userService.createUser(user, roles);
	}

	// view(localhost:8080/user/SONU123)
	@GetMapping("/{username}")
	public User getUser(@PathVariable("username") String username) {

		return this.userService.getUser(username);

	}

	// delete the user by id
	@DeleteMapping("/{userId}")
	public void deleteUser(@PathVariable("userId") Long userId) {

		this.userService.deleteUser(userId);

	}
	// update

	// logout
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request) {

		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			// Optionally log token or blacklist it here
			System.out.println("Logout token: " + token);
		}

		return ResponseEntity.ok("Logout successful");
	}

	// Generate CAPTCHA image
	@GetMapping("/captcha")
	public ResponseEntity<Captcha> getCaptcha() throws IOException {
		String captchaText = captchaProducer.createText(); // ✅ this works
		BufferedImage captchaImage = captchaProducer.createImage(captchaText);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(captchaImage, "jpg", baos);
		String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());

		String id = UUID.randomUUID().toString();

		Captcha captcha = new Captcha();
		captcha.setId(id);
		captcha.setCaptcha(captchaText);
		captchaRepository.save(captcha);

		Captcha response = new Captcha();
		response.setId(id);
		response.setCaptcha(base64Image);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/validationcaptcha")
	public ResponseEntity<?> login(@RequestBody Map<String, String> requestMap) {
		String username = requestMap.get("username");
		String password = requestMap.get("password");
		String inputCaptcha = requestMap.get("captcha");
		String captchaId = requestMap.get("captchaId");

		// ✅ Fetch captcha by ID
		Optional<Captcha> optionalCaptcha = captchaRepository.findById(captchaId);

		if (!optionalCaptcha.isPresent()) {
			return ResponseEntity.badRequest()
					.body(Collections.singletonMap("message", "Captcha expired or not found"));
		}

		Captcha storedCaptcha = optionalCaptcha.get();

		// ✅ Match the captcha text (ignore case)
		if (!storedCaptcha.getCaptcha().equalsIgnoreCase(inputCaptcha)) {
			return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Invalid captcha"));
		}

		// ✅ Optional: delete captcha after one-time use
		captchaRepository.deleteById(captchaId);

		// ✅ Validate username and password
		User user = userService.getUser(username);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Collections.singletonMap("message", "Invalid username"));
		}

		if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Collections.singletonMap("message", "Invalid password"));
		}

		return ResponseEntity.ok("Login successful");
	}

}