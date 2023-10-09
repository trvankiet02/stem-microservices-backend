package com.trvankiet.app.jwt.util.impl;

import com.trvankiet.app.business.user.model.UserDetailsImpl;
import com.trvankiet.app.constant.AppConstant;
import com.trvankiet.app.jwt.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtilImpl implements JwtUtil {

	private static final Long JWT_ACCESS_EXPIRATION = 3600000L;
	private static final Long JWT_REFRESH_EXPIRATION = 604800000L;
	private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
	private Key getSigningKey() {
		return AppConstant.getSecretKey();
	}
	
	@Override
	public String extractCredentialId(final String token) {
		return this.extractClaims(token, Claims::getSubject);
	}
	
	@Override
	public Date extractExpiration(final String token) {
		return this.extractClaims(token, Claims::getExpiration);
	}
	
	@Override
	public <T> T extractClaims(final String token, Function<Claims, T> claimsResolver) {
		final Claims claims = this.extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	
	private Claims extractAllClaims(final String token) {
		return Jwts.parserBuilder()
					.setSigningKey(this.getSigningKey())
					.build()
					.parseClaimsJws(token)
					.getBody();
	}
	
	private Boolean isTokenExpired(final String token) {
		return this.extractExpiration(token).before(new Date());
	}
	
	@Override
	public String generateAccessToken(final UserDetailsImpl userDetails) {
		final Map<String, Object> claims = new HashMap<>();
		return this.createAccessToken(claims, userDetails.getUsername());
	}

	@Override
	public String generateRefreshToken(UserDetailsImpl userDetails) {
		final Map<String, Object> claims = new HashMap<>();
		return this.createRefreshToken(claims, userDetails.getCredentialDto().getCredentialId());
	}

	private String createAccessToken(final Map<String, Object> claims, final String subject) {
		return Jwts.builder()
					.setClaims(claims)
					.setSubject(subject)
					.setIssuedAt(new Date(System.currentTimeMillis()))
					.setExpiration(new Date(System.currentTimeMillis() + JWT_ACCESS_EXPIRATION))
					.signWith(this.getSigningKey(), SignatureAlgorithm.HS512)
		.compact();
	}

	private String createRefreshToken(final Map<String, Object> claims, final String subject) {
		return Jwts.builder()
					.setClaims(claims)
					.setSubject(subject)
					.setIssuedAt(new Date(System.currentTimeMillis()))
					.setExpiration(new Date(System.currentTimeMillis() + JWT_REFRESH_EXPIRATION))
					.signWith(this.getSigningKey(), SignatureAlgorithm.HS512)
		.compact();
	}
	
	@Override
	public Boolean validateToken(final String token, final UserDetailsImpl userDetails) {
		final String credentialId = this.extractCredentialId(token);
		return (
				credentialId.equals(userDetails.getCredentialDto().getCredentialId()) && !isTokenExpired(token)
		);
	}
	
	
	
}










