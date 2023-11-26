package com.example.myemployee.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class Register {

    public Long id;

    @NotEmpty(message = "username is required.")
    private String username;

    @NotEmpty(message = "password is required.")
    private String password;

    @NotEmpty(message = "fullname is required.")
    private String fullname;

    private String phoneNumber;

    private String domicile;

    private String gender;

}
