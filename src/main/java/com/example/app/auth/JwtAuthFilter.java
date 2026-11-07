
package com.example.app.auth;

import com.example.app.service.JwtService;
import com.example.app.service.UserDetailsServiceImpl;

import io.jsonwebtoken.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Get Authorization header
        String authHeader = request.getHeader("Authorization");

        String token = null;
        String username = null;

        // 2. Check whether header contains Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            token = authHeader.substring(7);

            try {
                // 3. Extract username from JWT
                username = jwtService.extractUsername(token);

            } catch (JwtException | IllegalArgumentException e) {
                // Invalid or expired token
                username = null;
            }
        }

        // 4. Check username and authentication status
        if (username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            try {
                // 5. Fetch user details from database
                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                // 6. Validate JWT token
                if (jwtService.validateToken(token, userDetails)) {

                    // 7. Create authentication object
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // 8. Set request details
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    // 9. Store authentication in SecurityContext
                    SecurityContextHolder.getContext()
                            .setAuthentication(authenticationToken);
                }

            } catch (JwtException | IllegalArgumentException |
                     UsernameNotFoundException e) {
                // Do not authenticate requests with invalid tokens/users
                SecurityContextHolder.clearContext();
            }
        }

        // 10. Continue filter chain
        filterChain.doFilter(request, response);
    }
}
