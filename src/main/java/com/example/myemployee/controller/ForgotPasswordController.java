package com.example.myemployee.controller;

import com.example.myemployee.config.Config;
import com.example.myemployee.model.ResetPassword;
import com.example.myemployee.model.oauth.User;
import com.example.myemployee.repository.oauth.UserRepository;
import com.example.myemployee.service.UserService;
import com.example.myemployee.service.oauth.EmailSender;
import com.example.myemployee.utils.EmailTemplate;
import com.example.myemployee.utils.SimpleStringUtils;
import com.example.myemployee.utils.TemplateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/forget-password/")
public class ForgotPasswordController {
    @Autowired
    private UserRepository userRepository;
    Config config = new Config();
    @Autowired
    public UserService userService;
    @Value("${expired.token.password.minute:}") // FILE_SHOW_RUL
    private int expiredToken;
    @Autowired
    public TemplateResponse templateResponse;
    @Autowired
    public EmailTemplate emailTemplate;
    @Autowired
    public EmailSender emailSender;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Step 1 : Send OTP
    @PostMapping("/send") // send OTP//send OTP
    public Map sendEmailPassword(@RequestBody ResetPassword user) {
        String message = "Thanks, please check your email";
        if (StringUtils.isEmpty(user.getUsername()))
            return templateResponse.templateError("No email provided");
        User found = userRepository.findOneByUsername(user.getUsername());
        if (found == null)
            return templateResponse.notFound("Email not found"); // throw new BadRequest("Email not found");
        String template = emailTemplate.getResetPasswordTemplate();
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
            template = template.replaceAll("\\{\\{PASS_TOKEN}}", otp);
            template = template.replaceAll("\\{\\{USERNAME}}",
                    (found.getUsername() == null ? "" + "@UserName" : "@" + found.getUsername()));
            userRepository.save(found);
        } else {
            template = template.replaceAll("\\{\\{USERNAME}}",
                    (found.getUsername() == null ? "" + "@UserName" : "@" + found.getUsername()));
            template = template.replaceAll("\\{\\{PASS_TOKEN}}", found.getOtp());
        }
        emailSender.sendAsync(found.getUsername(), "Chute - Forget Password", template);
        return templateResponse.templateSukses("success");
    }

    // Step 2 : CHek TOKEN OTP EMAIL
    @PostMapping("/validate")
    public Map cheKTOkenValid(@RequestBody ResetPassword model) {
        if (model.getOtp() == null)
            return templateResponse.notFound("Token " + config.isRequired);
        User user = userRepository.findOneByOTP(model.getOtp());
        if (user == null) {
            return templateResponse.templateError("Token not valid");
        }
        return templateResponse.templateSukses("Success");
    }

    // Step 3 : lakukan reset password baru
    @PostMapping("/change-password")
    public Map<String, String> resetPassword(@RequestBody ResetPassword model) {
        if (model.getOtp() == null)
            return templateResponse.notFound("Token " + config.isRequired);
        if (model.getNewPassword() == null)
            return templateResponse.notFound("New Password " + config.isRequired);
        User user = userRepository.findOneByOTP(model.getOtp());
        String success;
        if (user == null)
            return templateResponse.notFound("Token not valid");

        user.setPassword(passwordEncoder.encode(model.getNewPassword().replaceAll("\\s+", "")));
        user.setOtpExpiredDate(null);
        user.setOtp(null);
        try {
            userRepository.save(user);
            success = "success";
        } catch (Exception e) {
            return templateResponse.templateError("Gagal simpan user");
        }
        return templateResponse.templateSukses(success);
    }
}