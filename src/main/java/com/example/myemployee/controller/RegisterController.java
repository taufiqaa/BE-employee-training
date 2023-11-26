package com.example.myemployee.controller;

import com.example.myemployee.config.Config;
import com.example.myemployee.model.Register;
import com.example.myemployee.model.oauth.User;
import com.example.myemployee.repository.oauth.UserRepository;
import com.example.myemployee.service.UserService;
import com.example.myemployee.service.oauth.EmailSender;
import com.example.myemployee.utils.EmailTemplate;
import com.example.myemployee.utils.SimpleStringUtils;
import com.example.myemployee.utils.TemplateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user-register/")
public class RegisterController {
    @Autowired
    private UserRepository userRepository;
    Config config = new Config();
    @Autowired
    public UserService userService;
    @Autowired
    public TemplateResponse templateResponse;

    @PostMapping("/register")
    public ResponseEntity<Map> saveRegisterManual(@RequestBody Register objModel) throws RuntimeException {
        Map map = new HashMap();
        User user = userRepository.checkExistingEmail(objModel.getUsername());
        if (null != user) {
            return new ResponseEntity<Map>(templateResponse.notFound("Username sudah ada"), HttpStatus.OK);
        }
        map = userService.registerManual(objModel);

        Map sendOtp = sendEmailegister(objModel);

        return new ResponseEntity<Map>(map, HttpStatus.OK);
    }

    @Value("${expired.token.password.minute:}") // FILE_SHOW_RUL
    private int expiredToken;

    @Autowired
    public EmailTemplate emailTemplate;

    @Autowired
    public EmailSender emailSender;

    // Step 2: sendp OTP berupa URL: guna updet enable agar bisa login:

    @PostMapping("/send-otp") // send OTP
    public Map sendEmailegister(@RequestBody Register user) {
        String message = "Thanks, please check your email for activation.";

        if (user.getUsername() == null)
            return templateResponse.templateError("No email provided");
        User found = userRepository.findOneByUsername(user.getUsername());
        if (found == null)
            return templateResponse.notFound("Email not found"); // throw new BadRequest("Email not found");

        String template = emailTemplate.getRegisterTemplate();
        if (StringUtils.isEmpty(found.getOtp())) {
            User search;
            String otp;
            do {
                otp = SimpleStringUtils.randomString(6, true);
                search = userRepository.findOneByOTP(otp);
            } while (search != null);
            Date dateNow = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateNow);
            calendar.add(Calendar.MINUTE, expiredToken);
            Date expirationDate = calendar.getTime();

            found.setOtp(otp);
            found.setOtpExpiredDate(expirationDate);
            template = template.replaceAll("\\{\\{USERNAME}}",
                    (found.getFullname() == null ? found.getUsername() : found.getFullname()));
            template = template.replaceAll("\\{\\{VERIFY_TOKEN}}", otp);
            userRepository.save(found);
        } else {
            template = template.replaceAll("\\{\\{USERNAME}}",
                    (found.getFullname() == null ? found.getUsername() : found.getFullname()));
            template = template.replaceAll("\\{\\{VERIFY_TOKEN}}", found.getOtp());
        }
        emailSender.sendAsync(found.getUsername(), "Register", template);
        return templateResponse.templateSukses(message);
    }

    @GetMapping("/register-confirm-otp/{token}")
    public ResponseEntity<Map> saveRegisterManual(@PathVariable(value = "token") String tokenOtp)
            throws RuntimeException {

        User user = userRepository.findOneByOTP(tokenOtp);
        if (null == user) {
            return new ResponseEntity<Map>(templateResponse.templateError("OTP tidak ditemukan"), HttpStatus.OK);
        }
        if (user.isEnabled()) {
            return new ResponseEntity<Map>(
                    templateResponse.templateSukses("Akun Anda sudah aktif, Silahkan melakukan login"), HttpStatus.OK);
        }
        String today = config.convertDateToString(new Date());
        String dateToken = config.convertDateToString(user.getOtpExpiredDate());

        if (Long.parseLong(today) > Long.parseLong(dateToken)) {
            return new ResponseEntity<Map>(templateResponse.templateError("Your Token is expired. Please Get token again."),
                    HttpStatus.OK);
        }
        // update user
        user.setEnabled(true);
        userRepository.save(user);

        return new ResponseEntity<Map>(templateResponse.templateSukses("Sukses, Silahkan Melakukan Login"), HttpStatus.OK);
    }

}
