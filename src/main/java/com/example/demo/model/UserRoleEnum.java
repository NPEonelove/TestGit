package com.example.demo.model;

import lombok.Getter;

@Getter
public enum UserRoleEnum {
    ADMIN("ADMIN"),
    USER("USER");

    private final String value;

    UserRoleEnum(String value) {
        this.value = value;
    }
}
