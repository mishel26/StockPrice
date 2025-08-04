package auth.jwt.service;


import auth.jwt.model.RefreshTokenInfo;
import auth.jwt.model.User;
import auth.jwt.security.JwtUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class LoginService {



    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;



    public ResponseEntity<?> verifyAndLoginUser(String userName, String pass){
        if (!userService.userExists(userName)) {
            return ResponseEntity.status(302)
                    .header("Location", "/auth/signup")
                    .body("User not found. Redirecting to signup.");
        }
        if (!userService.isValidPassword(userName, pass)) {
            return ResponseEntity.status(401).body("Incorrect password");
        }
        //User user = userService.findByUsername(userName);

        //Claims claims =  jwtUtil.validateToken(user.getToken());
        String accessToken = jwtUtil.generateAccessToken(userName);
        String refreshToken = jwtUtil.generateRefreshToken(userName);
        Instant expiry = jwtUtil.getExpiry(refreshToken);

        userService.saveRefreshToken(userName,refreshToken,expiry);

        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "token", accessToken,
                "refreshToken",refreshToken
        ));


    }

    public ResponseEntity<?> signUpUser(String userName, String pass){
            if(userService.userExists(userName)){
                return ResponseEntity.status(409).body("user already exists");
            }

        userService.saveUser(new User(userName,pass));
        return ResponseEntity.ok("Signup successful. You can now log in.");
    }

    public Map<String,String> checkForRefresh(String refreshToken){
        if (!jwtUtil.validateToken(refreshToken)) {
            return Map.of("error", "Invalid refresh token");
        }

        String username = jwtUtil.extractUsername(refreshToken);
        RefreshTokenInfo stored = userService.getRefreshToken(username);

        if (stored == null || !stored.getToken().equals(refreshToken)) {
            return Map.of("error", "Refresh token mismatch");
        }

        if (stored.getExpiry() < (Instant.now().getEpochSecond())) {
            return Map.of("error", "Refresh token expired");
        }

        String newAccessToken = jwtUtil.generateAccessToken(username);
        return Map.of("accessToken", newAccessToken);

    }

    public Map<String,String> checkHello(String header){
        String token = header.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return Map.of("error", "Invalid or expired access token");
        }

        String username = jwtUtil.extractUsername(token);
        return Map.of("message", "Hello, " + username);
    }


}
