package com.example.demo.dto.jwt;

import lombok.Data;

@Data
public class JwtAuthenticationDTO {

    private String accessToken;
    private String refreshToken;

}
