package com.example.demo.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCredentialsRequestDTO {

    private String login;
    private String password;

}
