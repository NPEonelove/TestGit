package com.example.demo.controller;

import com.example.demo.dto.jwt.JwtAuthenticationDTO;
import com.example.demo.dto.jwt.RefreshTokenDTO;
import com.example.demo.dto.user.UserCredentialsRequestDTO;
import com.example.demo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<JwtAuthenticationDTO> signUp(@RequestBody UserCredentialsRequestDTO userCredentialsRequestDTO) {
        return ResponseEntity.ok(authService.signUp(userCredentialsRequestDTO));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<JwtAuthenticationDTO> signIn(@RequestBody UserCredentialsRequestDTO userCredentialsRequestDTO) throws AuthenticationException {
        return ResponseEntity.ok(authService.signIn(userCredentialsRequestDTO));
    }

    @PostMapping("/refresh-access-token")
    public ResponseEntity<JwtAuthenticationDTO> refreshAccessToken(@RequestBody RefreshTokenDTO refreshTokenDTO) throws AuthenticationException {
        return ResponseEntity.ok(authService.refreshAccessToken(refreshTokenDTO));
    }
}
