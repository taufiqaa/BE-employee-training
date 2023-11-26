package com.example.myemployee.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.myemployee.config.Config;
import com.example.myemployee.model.Login;
import com.example.myemployee.model.Register;
import com.example.myemployee.model.oauth.Role;
import com.example.myemployee.model.oauth.User;
import com.example.myemployee.repository.oauth.RoleRepository;
import com.example.myemployee.repository.oauth.UserRepository;
import com.example.myemployee.service.UserService;
import com.example.myemployee.utils.TemplateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;



@Service
public class UserImpl implements UserService {
    Config config = new Config();
    private static final Logger logger = LoggerFactory.getLogger(UserImpl.class);

    @Value("${BASEURL}")
    private String baseUrl;

    @Autowired
    RoleRepository repoRole;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Autowired
    UserRepository repoUser;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    public TemplateResponse templateResponse;

    @Override
    public Map registerManual(Register objModel) {
        Map map = new HashMap();
        try {
            String[] roleNames = { "ROLE_USER", "ROLE_READ", "ROLE_WRITE" }; // admin
            User user = new User();
            user.setUsername(objModel.getUsername().toLowerCase());
            user.setFullname(objModel.getFullname());
            user.setDomicile(objModel.getDomicile());
            user.setPhoneNumber(objModel.getPhoneNumber());
            user.setGender(objModel.getGender());

            // step 1 :
            user.setEnabled(false); // matikan user

            String password = encoder.encode(objModel.getPassword().replaceAll("\\s+", ""));
            List<Role> r = repoRole.findByNameIn(roleNames);
            user.setRoles(r);
            user.setPassword(password);
            User obj = repoUser.save(user);
            return templateResponse.templateSukses(obj);
        } catch (Exception e) {
            logger.error("Eror registerManual=", e);
            return templateResponse.templateError("eror:" + e);
        }
    }

    @Override
    public Map login(Login loginModel) {
        /**
         * bussines logic for login here
         **/
        try {
            Map<String, Object> map = new HashMap<>();
            User checkUser = repoUser.findOneByUsername(loginModel.getUsername());
            if ((checkUser != null) && (encoder.matches(loginModel.getPassword(), checkUser.getPassword()))) {
                if (!checkUser.isEnabled()) {
                    map.put("is_enabled", checkUser.isEnabled());
                    return templateResponse.templateError(map);
                }
            }
            if (checkUser == null) {
                return templateResponse.notFound("user not found");
            }
            if (!(encoder.matches(loginModel.getPassword(), checkUser.getPassword()))) {
                return templateResponse.templateError("wrong password");
            }
            String url = baseUrl + "/oauth/token?username=" + loginModel.getUsername() + "&password="
                    + loginModel.getPassword() + "&grant_type=password" + "&client_id=my-client-web"
                    + "&client_secret=password";
            ResponseEntity<Map> response = restTemplateBuilder.build().exchange(url, HttpMethod.POST, null,
                    new ParameterizedTypeReference<Map>() {
                    });
            if (response.getStatusCode() == HttpStatus.OK) {
                User user = repoUser.findOneByUsername(loginModel.getUsername());
                List<String> roles = new ArrayList<>();
                for (Role role : user.getRoles()) {
                    roles.add(role.getName());
                }

                // save token
                // checkUser.setAccessToken(response.getBody().get("access_token").toString());
                // checkUser.setRefreshToken(response.getBody().get("refresh_token").toString());
                // userRepository.save(checkUser);

                map.put("access_token", response.getBody().get("access_token"));
                map.put("token_type", response.getBody().get("token_type"));
                map.put("refresh_token", response.getBody().get("refresh_token"));
                map.put("expires_in", response.getBody().get("expires_in"));
                map.put("scope", response.getBody().get("scope"));
                map.put("jti", response.getBody().get("jti"));
                return map;
            } else {
                return templateResponse.notFound("user not found");
            }
        } catch (HttpStatusCodeException e) {
            e.printStackTrace();
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return templateResponse.templateError("invalid login");
            }
            return templateResponse.templateError(e);
        } catch (Exception e) {
            e.printStackTrace();
            return templateResponse.templateError(e);
        }
    }
}

