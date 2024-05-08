package com.trvankiet.app.jwt.util;

import com.trvankiet.app.entity.Credential;
import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.function.Function;

public interface JwtUtil {
	
	String extractUserId(final String token);
	Date extractExpiration(final String token);
	<T> T extractClaims(final String token, final Function<Claims, T> claimsResolver);
	String generateAccessToken(final Credential credential);
	String generateRefreshToken(final Credential credential);
	Boolean validateToken(final String token);
	
}
