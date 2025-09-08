package com.example.demo.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserCredentialsResponseDTO {

    private UUID userId;
    private String login;

}
