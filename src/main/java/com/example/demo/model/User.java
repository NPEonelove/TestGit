package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "username")
    private String username;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRoleEnum role;

}
