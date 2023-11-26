package com.example.myemployee.model;

import lombok.Data;

@Data
public class ResetPassword {

    public String username;

    public String otp;

    public String newPassword;
}
