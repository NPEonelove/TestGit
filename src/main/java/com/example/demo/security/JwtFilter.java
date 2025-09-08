package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserService customUserService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String accessToken = getAccessTokenFromRequest(request);

        if (accessToken != null && jwtService.validateJwtToken(accessToken)) {
            setCustomUserDetailsToSecurityContextHolder(accessToken);
        }

        filterChain.doFilter(request, response);
    }

    // получение access токена из запроса
    private String getAccessTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // непосредственно, авторизация пользователя в системе
    private void setCustomUserDetailsToSecurityContextHolder(String accessToken) {
        String userId = jwtService.getUserIdFromJwtToken(accessToken);

        CustomUserDetails customUserDetails = customUserService.loadUserByUsername(userId);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null,
                        customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
