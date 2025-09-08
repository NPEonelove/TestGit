package com.example.demo.dto.jwt;

import com.example.demo.model.UserRoleEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GetJwtUserClaimsResponseDTO {

    private UUID userId;

    private UserRoleEnum role;
}