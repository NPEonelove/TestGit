package com.example.demo.security;

import com.example.demo.dto.jwt.GetJwtUserClaimsResponseDTO;
import com.example.demo.dto.jwt.JwtAuthenticationDTO;
import com.example.demo.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    @Value("${access-token.ttl}")
    private int accessTokenTtl;

    @Value("${refresh-token.ttl}")
    private int refreshTokenTtl;

    private final UserService userService;

    // получение access и refresh токена
    public JwtAuthenticationDTO generateAuthToken(UUID userId) {
        JwtAuthenticationDTO jwtAuthenticationDTO = new JwtAuthenticationDTO();
        jwtAuthenticationDTO.setAccessToken(generateAccessToken(userId));
        jwtAuthenticationDTO.setRefreshToken(generateRefreshToken(userId));
        return jwtAuthenticationDTO;
    }

    // обновление access токена
    public JwtAuthenticationDTO refreshAccessToken(UUID userId, String refreshToken) {
        JwtAuthenticationDTO jwtAuthenticationDTO = new JwtAuthenticationDTO();
        jwtAuthenticationDTO.setAccessToken(generateAccessToken(userId));
        jwtAuthenticationDTO.setRefreshToken(refreshToken);
        return jwtAuthenticationDTO;
    }

    // генерация access токена
    private String generateAccessToken(UUID userId) {
        GetJwtUserClaimsResponseDTO user = userService.getJwtUserClaims(userId);

        Date date = Date.from(LocalDateTime.now().plusMinutes(accessTokenTtl)
                .atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .subject(user.getUserId().toString())
                .claim("role", user.getRole().getValue())
                .expiration(date)
                .signWith(getSignKey())
                .compact();
    }

    // генерация refresh токена
    private String generateRefreshToken(UUID userId) {
        GetJwtUserClaimsResponseDTO user = userService.getJwtUserClaims(userId);

        Date date = Date.from(LocalDateTime.now().plusYears(refreshTokenTtl)
                .atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .subject(user.getUserId().toString())
                .claim("role", user.getRole().getValue())
                .expiration(date)
                .signWith(getSignKey())
                .compact();
    }

    // получение userId из access токена
    public String getUserIdFromJwtToken(String accessToken) {
        Claims claims = getClaimsFromAccessToken(accessToken);
        return claims.getSubject();
    }

    // получение роли из access токена
    public String getUserRoleFromJwtToken(String accessToken) {
        Claims claims = getClaimsFromAccessToken(accessToken);
        return claims.get("role", String.class);
    }

    // валидация jwt токена
    public boolean validateJwtToken(String token) {
        try {
            getClaimsFromAccessToken(token);
        } catch (Exception e) {
            throw e;
        }

        return true;
    }

    // получение payload из access токена
    private Claims getClaimsFromAccessToken(String accessToken) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseClaimsJws(accessToken)
                .getPayload();
    }

    // генерация подписи
    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
