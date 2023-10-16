package com.trvankiet.app.config.filter;

import com.trvankiet.app.business.auth.service.impl.UserDetailsServiceImpl;
import com.trvankiet.app.business.user.model.UserDetailsImpl;
import com.trvankiet.app.jwt.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {

        log.info("**JwtRequestFilter, once per request, validating and extracting token*\n");

        final var authorizationHeader = request.getHeader("Authorization");

        String userId = null;
        String jwt = null;
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                userId = jwtService.extractUserId(jwt);
            }

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                final UserDetailsImpl userDetails = this.userDetailsService.loadUserById(userId);

                if (this.jwtService.validateToken(jwt)) {
                    final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    response.addHeader("Authorization", "Bearer " + jwt);
                }

            }
        } catch (MalformedJwtException e) {
            response.setStatus(401);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print("{\"success\": false, \"message\": \"Access Denied\", \"result\": \"Invalid JWT token. Please login again!\", \"statusCode\": \"401\"}");
            out.flush();
            return;
        } catch (ExpiredJwtException e) {
            response.setStatus(401);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print("{\"success\": false, \"message\": \"Access Denied\", \"result\": \"Token is expired. Please login again!\", \"statusCode\": \"401\"}");
            out.flush();
            return;
        } catch (SignatureException ex) {
            response.setStatus(401);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print("{\"success\": false, \"message\": \"Access Denied\", \"result\": \"In valid JWT signature. Please login again!\", \"statusCode\": \"401\"}");
            out.flush();
            return;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            response.setStatus(401);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print("{\"success\": false, \"message\": \"Access Denied\", \"result\": \"Please login again!\", \"statusCode\": \"401\"}");
            out.flush();
            return;
        }

        filterChain.doFilter(request, response);
        log.info("**Jwt request filtered!*\n");
    }


}










