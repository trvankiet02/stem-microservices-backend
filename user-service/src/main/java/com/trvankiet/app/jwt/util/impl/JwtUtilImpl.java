package com.trvankiet.app.jwt.util.impl;

import com.trvankiet.app.constant.AppConstant;
import com.trvankiet.app.entity.Credential;
import com.trvankiet.app.jwt.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtilImpl implements JwtUtil {

	private static final Long JWT_ACCESS_EXPIRATION = 3600000L;
	private static final Long JWT_REFRESH_EXPIRATION = 604800000L;
	private Key getSigningKey() {
		return AppConstant.getSecretKey();
	}
	
	@Override
	public String extractUserId(final String token) {
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
	public String generateAccessToken(final Credential credential) {
		final Map<String, Object> claims = new HashMap<>();
		claims.put("role", credential.getUser().getRole().name());
		return this.createAccessToken(claims, credential.getUser().getId());
	}

	@Override
	public String generateRefreshToken(Credential credential) {
		final Map<String, Object> claims = new HashMap<>();
		return this.createRefreshToken(claims, credential.getUser().getId());
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
	public Boolean validateToken(final String token) {
		try {
			Jwts.parserBuilder().setSigningKey(getSigningKey().getEncoded()).build().parseClaimsJws(token);
			return !this.isTokenExpired(token);
		} catch (UnsupportedJwtException ex) {
			log.error("Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			log.error("JWT claims string is empty.");
		}
		return false;
	}
	
	
	
}










