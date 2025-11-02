package com.example.cvservice.configs;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.cvservice.securities.AuthenticatedUser;
import com.example.cvservice.securities.JwtTokenProvider;
import com.example.cvservice.securities.JwtValidationException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // System.out.println("========== JWT FILTER DEBUG ==========");
        // System.out.println(">>> Request URI: " + request.getRequestURI());
        // System.out.println(">>> Request Method: " + request.getMethod());

        // Try to get token from Authorization header first
        String token = null;
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        // System.out.println(">>> Authorization Header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            // System.out.println(">>> Token from Header: "
            //         + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));
        }

        // If not in header, try to get from cookie
        if (token == null && request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                // System.out.println(">>>   Cookie: " + cookie.getName() + " = "
                //         + (cookie.getValue() != null
                //                 ? cookie.getValue().substring(0, Math.min(20, cookie.getValue().length())) + "..."
                //                 : "null"));
                if ("access_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    // System.out.println(">>> Token from Cookie: "
                    //         + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));
                    break;
                }
            }
        }

        // System.out.println(">>> Final Token: " + (token != null ? "FOUND" : "NOT FOUND"));
        // System.out.println("======================================");

        // If no token found, continue without authentication
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            AuthenticatedUser principal = tokenProvider.validateAndExtract(token);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    principal,
                    token,
                    principal.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtValidationException ex) {
            SecurityContextHolder.clearContext();
            String message = ex.getMessage() != null ? ex.getMessage().replace('"', ' ') : "Invalid token";
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter()
                    .write("{\"status\":401,\"message\":\"" + message + "\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}