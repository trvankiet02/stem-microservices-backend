package com.trvankiet.app.jwt.service;

import com.trvankiet.app.dto.CredentialDto;
import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.function.Function;

public interface JwtService {
	
	String extractCredentialId(final String token);
	Date extractExpiration(final String token);
	<T> T extractClaims(final String token, final Function<Claims, T> claimsResolver);
	Boolean validateToken(final String token, final CredentialDto credential);
	
}










