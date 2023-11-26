package com.example.myemployee.controller;

import com.example.myemployee.config.Config;
import com.example.myemployee.model.Register;
import com.example.myemployee.model.oauth.User;
import com.example.myemployee.repository.oauth.UserRepository;
import com.example.myemployee.service.UserService;
import com.example.myemployee.utils.TemplateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/register")
public class RegisterGoogleController {
    @Autowired
    private UserRepository userRepository;
    Config config = new Config();
    @Autowired
    public UserService serviceReq;
    @Value("${BASEURL:}") // FILE_SHOW_RUL
    private String BASEURL;
    @Autowired
    public TemplateResponse response;

    @PostMapping("")
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map> saveRegisterManual(@Valid @RequestBody Register objModel) throws RuntimeException {
        Map map = new HashMap();
        User user = userRepository.checkExistingEmail(objModel.getUsername());
        if (null != user) {
            return new ResponseEntity<Map>(
                    response.templateError("Email is Registered, try another email or click Forget Password"),
                    HttpStatus.OK);
        }
        map = serviceReq.registerManual(objModel);
        return new ResponseEntity<Map>(map, HttpStatus.OK);
    }
}

