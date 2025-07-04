package com.exam.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.exam.serviceImpl.UserDetailsServiceImpl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		final String requestTokenHeader = request.getHeader("Authorization");
		String username = null;
		String jwtToken = null;

		// Check if the header starts with "Bearer "
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			jwtToken = requestTokenHeader.substring(7).trim();

			try {
				// Validate token structure (must have 3 parts separated by '.')
				if (jwtToken.split("\\.").length != 3) {
					throw new MalformedJwtException("JWT must contain exactly 2 periods");
				}

				username = this.jwtUtil.extractUsername(jwtToken);

			} catch (ExpiredJwtException e) {
				System.out.println("JWT token has expired: " + e.getMessage());
			} catch (MalformedJwtException e) {
				System.out.println("Malformed JWT token: " + e.getMessage());
			} catch (Exception e) {
				System.out.println("JWT token error: " + e.getMessage());
			}

		} else {
			System.out.println("Authorization header is missing or doesn't start with 'Bearer '");
		}

		// If token is valid and no authentication exists in context
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

			if (this.jwtUtil.validateToken(jwtToken, userDetails)) {
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			} else {
				System.out.println("Token validation failed");
			}
		}

		filterChain.doFilter(request, response);
	}
}
