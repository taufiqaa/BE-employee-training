package com.example.myemployee.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class Login {

    @NotEmpty(message = "username is required.")
    private String username;

    @NotEmpty(message = "password is required.")
    private String password;
}
