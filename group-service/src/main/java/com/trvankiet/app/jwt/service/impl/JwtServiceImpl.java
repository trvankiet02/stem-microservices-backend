package com.trvankiet.app.jwt.service.impl;

import com.trvankiet.app.dto.CredentialDto;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.jwt.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
	
	private final JwtUtil jwtUtil;
	
	@Override
	public String extractCredentialId(final String token) {
		log.info("JwtServiceImpl, String, extractCredentialId");
		return this.jwtUtil.extractCredentialId(token);
	}
	
	@Override
	public Date extractExpiration(final String token) {
		log.info("JwtServiceImpl, Date, extractExpiration");
		return this.jwtUtil.extractExpiration(token);
	}
	
	@Override
	public <T> T extractClaims(final String token, final Function<Claims, T> claimsResolver) {
		log.info("JwtServiceImpl, <T> T, extractClaims");
		return this.jwtUtil.extractClaims(token, claimsResolver);
	}

	@Override
	public Boolean validateToken(final String token, final CredentialDto credential) {
		log.info("JwtServiceImpl, Boolean, validateToken");
		return this.jwtUtil.validateToken(token, credential);
	}
	
	
	
}










