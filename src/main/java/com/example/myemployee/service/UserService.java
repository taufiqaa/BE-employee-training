package com.example.myemployee.service;

import com.example.myemployee.model.Login;
import com.example.myemployee.model.Register;

import java.util.Map;

public interface UserService {

    Map registerManual(Register objModel);

    Map login(Login objModel);

}
