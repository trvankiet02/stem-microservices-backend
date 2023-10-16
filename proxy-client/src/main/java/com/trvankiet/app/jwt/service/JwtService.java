package com.trvankiet.app.jwt.service;

import com.trvankiet.app.business.user.model.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.function.Function;

public interface JwtService {
	
	String extractUserId(final String token);
	Date extractExpiration(final String token);
	<T> T extractClaims(final String token, final Function<Claims, T> claimsResolver);
	Boolean validateToken(final String token);
	
}










