package com.trvankiet.app.jwt.util;

import com.trvankiet.app.business.user.model.UserDetailsImpl;
import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.function.Function;

public interface JwtUtil {
	
	String extractCredentialId(final String token);
	Date extractExpiration(final String token);
	<T> T extractClaims(final String token, final Function<Claims, T> claimsResolver);
	String generateAccessToken(final UserDetailsImpl userDetails);
	String generateRefreshToken(final UserDetailsImpl userDetails);
	Boolean validateToken(final String token, final UserDetailsImpl userDetails);
	
}
